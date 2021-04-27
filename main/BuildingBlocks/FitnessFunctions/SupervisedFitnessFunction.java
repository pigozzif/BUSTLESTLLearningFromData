package BuildingBlocks.FitnessFunctions;

import TreeNodes.AbstractTreeNode;
import BuildingBlocks.SignalBuilders.SupervisedSignalBuilder;
import eu.quanticol.moonlight.signal.Signal;
import LocalSearch.LocalSearch;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class SupervisedFitnessFunction extends AbstractFitnessFunction<Signal<Map<String, Double>>> {

    private final List<Signal<Map<String, Double>>> positiveTraining = new ArrayList<>();
    private final List<Signal<Map<String, Double>>> positiveTest = new ArrayList<>();
    private final List<Signal<Map<String, Double>>> negativeTraining = new ArrayList<>();
    private final List<Signal<Map<String, Double>>> negativeTest = new ArrayList<>();
    private final BiFunction<double[], double[], Double> function = (x, y) -> (x[0] - y[0]) / (Math.abs(x[1] + y[1]));
    private final double[] labels;
    private final long numPositive;
    private final long numNegative;
    private int num = 0;

    public SupervisedFitnessFunction(String path, boolean localSearch, Random random) throws IOException {
        super(localSearch);
        this.signalBuilder = new SupervisedSignalBuilder();
        List<Integer> boolIndexes = new ArrayList<>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<>() {{ add(0); add(1); }};
        List<Signal<Map<String, Double>>> signals = this.signalBuilder.parseSignals(path, boolIndexes, doubleIndexes);
        this.labels = this.signalBuilder.readVectorFromFile(path + "/labels.csv");
        this.splitSignals(signals, 0.8, random);
        this.numPositive = Arrays.stream(this.labels).filter(x -> x > 0).count();
        this.numNegative = Arrays.stream(this.labels).filter(x -> x < 0).count();
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
        //System.out.println(monitor);
        //if (this.num < 0) {
        //    this.num++;
        //    return 0.0;
        //}
        if (this.isLocalSearch) {
            double[] newParams = LocalSearch.optimize(monitor, this, 1);
            monitor.propagateParameters(newParams);
            double[] p1u1 = this.computeRobustness(monitor, this.getPositiveTraining(), numPositive);
            double[] p2u2 = this.computeRobustness(monitor, this.getNegativeTraining(), numNegative);
            double value;
            if (p1u1[0] > p2u2[0]) {
                value = ((p1u1[0] - p1u1[1]) + (p2u2[0] + p2u2[1])) / 2;
            } else {
                value = ((p2u2[0] - p2u2[1]) + (p1u1[0] + p1u1[1])) / 2;
            }
            int numBounds = monitor.getNumBounds();
            List<String[]> variables = monitor.getVariables();
            for (int i = numBounds; i < newParams.length; i++) {
                if (variables.get(i - numBounds)[1].equals(">")) {
                    newParams[i] = Math.max(newParams[i] + value, 0);
                } else {
                    newParams[i] = Math.max(newParams[i] - value, 0);
                }
            }
            monitor.propagateParameters(newParams);
        }
        double[] positiveResult = this.computeRobustness(monitor, this.positiveTraining, this.numPositive);
        double[] negativeResult = this.computeRobustness(monitor, this.negativeTraining, this.numNegative);
        ++this.num;
        // System.out.println("INDIVIDUAL: " + this.num);
        return - this.function.apply(positiveResult, negativeResult);
    }

    public double[] computeRobustness(AbstractTreeNode monitor, List<Signal<Map<String, Double>>> data, long num) {
        double[] result = new double[3];
        double robustness;
        for (Signal<Map<String, Double>> signal : data) {
            robustness = this.monitorSignal(signal, monitor, false);
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

    private void splitSignals(List<Signal<Map<String, Double>>> signals, double fold, Random random) {
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
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            double[] value1 = this.computeRobustness(node, this.positiveTraining, this.numPositive);
            double[] value2 = this.computeRobustness(node, this.negativeTraining, this.numNegative);
            return this.function.apply(value1, value2);};
    }

    @Override
    public List<Signal<Map<String, Double>>> getPositiveTraining() {
        return this.positiveTraining;
    }

    @Override
    public List<Signal<Map<String, Double>>> getNegativeTraining() {
        return this.negativeTraining;
    }

    @Override
    public List<Signal<Map<String, Double>>> getPositiveTest() {
        return this.positiveTest;
    }

    @Override
    public List<Signal<Map<String, Double>>> getNegativeTest() {
        return this.negativeTest;
    }

}
