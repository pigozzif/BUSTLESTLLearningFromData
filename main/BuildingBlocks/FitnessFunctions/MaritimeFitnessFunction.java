package BuildingBlocks.FitnessFunctions;

import BuildingBlocks.SignalBuilders.MaritimeSignalBuilder;
import BuildingBlocks.TrajectoryRecord;
import BuildingBlocks.TreeNode;
import eu.quanticol.moonlight.signal.Signal;
import localSearch.gpOptimisation.GPOptimisation;
import localSearch.gpOptimisation.GpoOptions;
import localSearch.numeric.optimization.ObjectiveFunction;
import localSearch.sampler.GridSampler;
import localSearch.sampler.Parameter;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;


public class MaritimeFitnessFunction extends AbstractFitnessFunction<Signal<TrajectoryRecord>> {

    private final List<Signal<TrajectoryRecord>> positiveTraining = new ArrayList<>();
    private final List<Signal<TrajectoryRecord>> positiveTest = new ArrayList<>();
    private final List<Signal<TrajectoryRecord>> negativeTraining = new ArrayList<>();
    private final List<Signal<TrajectoryRecord>> negativeTest = new ArrayList<>();
    private final MaritimeSignalBuilder signalBuilder = new MaritimeSignalBuilder();
    private final BiFunction<double[], double[], Double> function = (x, y) -> (x[0] - y[0]) / (Math.abs(x[1] + y[1]));
    private final double[] labels;
    private final long numPositive;
    private final long numNegative;
    private int num = 0;

    public MaritimeFitnessFunction(Random random) throws IOException {
        super();
        List<Integer> boolIndexes = new ArrayList<Integer>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<Integer>() {{ add(0); add(1); }};
        List<Signal<TrajectoryRecord>> signals = this.signalBuilder.parseSignals("./data/navalData2.csv", boolIndexes, doubleIndexes);
        this.labels = this.signalBuilder.readVectorFromFile("./data/navalLabels.csv");
        this.splitSignals(signals, 0.8, random);
        this.numPositive = Arrays.stream(this.labels).filter(x -> x > 0).count();
        this.numNegative = Arrays.stream(this.labels).filter(x -> x < 0).count();
    }

    @Override
    public Double apply(TreeNode monitor) {
        //System.out.println(monitor);
        //if (this.num < 0) {
        //    this.num++;
        //    return 0.0;
        //}
        double[] newParams = this.averageMultiTrajectory(monitor, 50);
        double[] positiveResult = this.computeRobustness(monitor, this.positiveTraining, this.numPositive, newParams);
        double[] negativeResult = this.computeRobustness(monitor, this.negativeTraining, this.numNegative, newParams);
        ++this.num;
        // System.out.println("INDIVIDUAL: " + this.num);
        return - this.function.apply(positiveResult, negativeResult);
    }

    private double[] computeRobustness(TreeNode monitor, List<Signal<TrajectoryRecord>> data, long num, double[] parameters) {
        monitor.propagateParameters(parameters);
        double[] result = new double[2];
        double robustness;
        for (Signal<TrajectoryRecord> signal : data) {
            //try {
            robustness = this.monitorSignal(signal, monitor, false);
            //}
            //catch (Exception e) {
            //    System.out.println(monitor);
            //    throw e;
            //}
            result[0] += robustness;
            result[1] += robustness * robustness;
        }
        result[1] = this.standardDeviation(result[1], result[0], num);
        result[0] /= num;
        return result;
    }
    // TODO: they use plain old variance
    private double standardDeviation(double partialSumSquared, double partialSum, long num) {
        double mean = partialSum / (num - 1);
        return Math.sqrt((partialSumSquared / (num - 1)) - (mean * mean));
    }

    private double[] averageMultiTrajectory(TreeNode monitor, int maxIterations) {
        double[] timeBounds = this.signalBuilder.getTemporalBounds();
        List<String[]> variables = monitor.getVariables();
        int numVariables = variables.size();
        int numBounds = monitor.getNumBounds();
        HashMap<String, double[]> temp = this.signalBuilder.getVarsBounds();
        double[] lb = new double[numBounds + numVariables];
        double[] ub = new double[numBounds + numVariables];
        for (int i=0; i < numBounds; ++i) {
            lb[i] = (i % 2 == 0) ? timeBounds[0] : 1;
            ub[i] = timeBounds[1];
        }
        for (int j=0; j < numVariables; ++j) {
            lb[j + numBounds] = temp.get(variables.get(j)[0])[0];
            ub[j + numBounds] = temp.get(variables.get(j)[0])[1];
        }
        ObjectiveFunction function = point -> {
            //for (int i = 0; i < numBounds - 1; i += 2) {
                // point[i + 1] = point[i] + point[i + 1] * (1 - point[i]);
            //    System.out.println("####");
            //    System.out.println(point[i]);
            //    System.out.println(point[i + 1]);
            //}
            final double[] p = point;
            point = IntStream.range(0, point.length).mapToDouble(i -> lb[i] + p[i] * (ub[i] - lb[i])).toArray();
            //for (int i = 0; i < numBounds - 1; i += 2) {
            //    point[i + 1] = point[i] + point[i + 1];// * (1 - point[i]);
            //    System.out.println("####");
            //    System.out.println((int) point[i]);
            //    System.out.println((int) point[i] + (int) point[i + 1]);
            //}
            double[] value1 = this.computeRobustness(monitor, this.positiveTraining, this.numPositive, point);
            double[] value2 = this.computeRobustness(monitor, this.negativeTraining, this.numNegative, point);
            double abs = this.function.apply(value1, value2);
            if (Double.isNaN(abs)) {
                return 0;
            }
            return abs;
        };
        GridSampler custom = new GridSampler() {
            @Override
            public double[][] sample(int n, double[] lbounds, double[] ubounds) {
                double[][] res = new double[n][lbounds.length];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < numBounds; j += 2) {
                        res[i][j] = lbounds[j] + (Math.random() * (ubounds[j] - lbounds[j]));
                        res[i][j + 1] = /*res[i][j] + */(Math.random() * (ubounds[j + 1] - res[i][j]));
                        //res[i][j + 1] = Math.max(1.0 / ubounds[j + 1], Math.random() * (ubounds[j + 1] - res[i][j]));
                        // System.out.println(res[i][j]);
                        // System.out.println(res[i][j + 1]);
                    }
                    for (int j = numBounds; j < res[i].length; j++) {
                        res[i][j] = lbounds[j] + Math.random() * (ubounds[j] - lbounds[j]);
                    }
                }
                return res;
            }
            @Override
            public double[][] sample(int n, Parameter[] params) {
                return new double[0][];
            }
        };
        GPOptimisation gpo = new GPOptimisation();
        GpoOptions options = new GpoOptions();
        options.setInitialSampler(custom);
        options.setMaxIterations(maxIterations);
        options.setHyperparamOptimisation(true);
        options.setUseNoiseTermRatio(true);
        options.setNoiseTerm(0);
        options.setGridSampler(custom);
        options.setGridSampleNumber(200);
        gpo.setOptions(options);
        double[] lbU = IntStream.range(0, lb.length).mapToDouble(i -> 0).toArray();
        double[] ubU = IntStream.range(0, ub.length).mapToDouble(i -> 1).toArray();
        double[] v = gpo.optimise(function, lbU, ubU).getSolution();
        double[] vv = IntStream.range(0, v.length).mapToDouble(i -> lb[i] + v[i] * (ub[i] - lb[i])).toArray();
        double[] p1u1 = this.computeRobustness(monitor, this.positiveTraining, this.numPositive, vv);
        double[] p2u2 = this.computeRobustness(monitor, this.negativeTraining, this.numNegative, vv);
        double value;
        if (p1u1[0] > p2u2[0]) {
            value = ((p1u1[0] - p1u1[1]) + (p2u2[0] + p2u2[1])) / 2;
        } else {
            value = ((p2u2[0] - p2u2[1]) + (p1u1[0] + p1u1[1])) / 2;
        }
        for (int i = numBounds; i < vv.length; i++) {
            if (variables.get(i - numBounds)[1].equals(">")) {  // TODO: a little bit hardcoded
                vv[i] = Math.max(vv[i] + value, 0);
            } else {
                vv[i] = Math.max(vv[i] - value, 0);
            }
        }
        return vv;
    }

    private void splitSignals(List<Signal<TrajectoryRecord>> signals, double fold, Random random) {
        List<Integer> positiveIndexes = new ArrayList<>();
        List<Integer> negativeIndexes = new ArrayList<>();
        for (int i=0; i < this.labels.length; ++i) {
            if (this.labels[i] < 0) {
                negativeIndexes.add(i);
            } else {
                positiveIndexes.add(i);
            }
        }
        Collections.shuffle(positiveIndexes, random);
        Collections.shuffle(negativeIndexes, random);
        int posFold = (int) (positiveIndexes.size() * fold);
        int negFold = (int) (negativeIndexes.size() * fold);
        for (int i=0; i < posFold; ++i) {
            this.positiveTraining.add(signals.get(positiveIndexes.get(i)));
        }
        for (int i=posFold; i < positiveIndexes.size(); ++i) {
            this.positiveTest.add(signals.get(positiveIndexes.get(i)));
        }
        for (int i=0; i < negFold; ++i) {
            this.negativeTraining.add(signals.get(negativeIndexes.get(i)));
        }
        for (int i=negFold; i < negativeIndexes.size(); ++i) {
            this.negativeTest.add(signals.get(negativeIndexes.get(i)));
        }
    }

    @Override
    public List<Signal<TrajectoryRecord>> getPositiveTraining() {
        return this.positiveTraining;
    }

    @Override
    public List<Signal<TrajectoryRecord>> getNegativeTraining() {
        return this.negativeTraining;
    }

    @Override
    public List<Signal<TrajectoryRecord>> getPositiveTest() {
        return this.positiveTest;
    }

    @Override
    public List<Signal<TrajectoryRecord>> getNegativeTest() {
        return this.negativeTest;
    }

}
