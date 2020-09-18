package BuildingBlocks.FitnessFunctions;

import BuildingBlocks.SignalBuilders.MaritimeSignalBuilder;
import BuildingBlocks.TrajectoryRecord;
import BuildingBlocks.TreeNode;
import eu.quanticol.moonlight.signal.Signal;

import java.io.IOException;
import java.util.*;


public class MaritimeFitnessFunction extends AbstractFitnessFunction<Signal<TrajectoryRecord>> {

    private final List<Signal<TrajectoryRecord>> positiveTraining = new ArrayList<>();
    private final List<Signal<TrajectoryRecord>> positiveTest = new ArrayList<>();
    private final List<Signal<TrajectoryRecord>> negativeTraining = new ArrayList<>();
    private final List<Signal<TrajectoryRecord>> negativeTest = new ArrayList<>();
    private final double[] labels;
    private final long numPositive;
    private final long numNegative;
    private int num = 0;

    public MaritimeFitnessFunction(Random random) throws IOException {
        super();
        MaritimeSignalBuilder signalBuilder = new MaritimeSignalBuilder();
        List<Integer> boolIndexes = new ArrayList<Integer>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<Integer>() {{ add(0); add(1); }};
        List<Signal<TrajectoryRecord>> signals = signalBuilder.parseSignals("./data/navalData2.csv", boolIndexes, doubleIndexes);
        this.labels = signalBuilder.readVectorFromFile("./data/navalLabels.csv");
        this.splitSignals(signals, 0.8, random);
        this.numPositive = Arrays.stream(this.labels).filter(x -> x > 0).count();
        this.numNegative = Arrays.stream(this.labels).filter(x -> x < 0).count();
    }

    @Override
    public Double apply(TreeNode monitor) {
        System.out.println("INDIVIDUAL: " + this.num);
        // System.out.println(monitor);
        double positiveSum = 0.0;
        double negativeSum = 0.0;
        double positiveSumSquared = 0.0;
        double negativeSumSquared = 0.0;
        double result;
        for (Signal<TrajectoryRecord> signal : this.positiveTraining) {
            result = this.monitorSignal(signal, monitor);
            positiveSum += result;
            positiveSumSquared += result * result;
        }
        for (Signal<TrajectoryRecord> signal : this.negativeTraining) {
            result = this.monitorSignal(signal, monitor);
            negativeSum += result;
            negativeSumSquared += result * result;
        }
        ++this.num;
        return ((positiveSum / this.numPositive) - (negativeSum / this.numNegative)) / (this.standardDeviation(positiveSumSquared, positiveSum, this.numPositive) +
                this.standardDeviation(negativeSumSquared, negativeSum, this.numNegative));
    }

    private double standardDeviation(double partialSumSquared, double partialSum, long num) {
        double mean = partialSum / (num - 1);
        return Math.sqrt((partialSumSquared / (num - 1)) - (mean * mean));
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
