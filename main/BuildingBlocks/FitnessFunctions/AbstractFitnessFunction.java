package BuildingBlocks.FitnessFunctions;

import BuildingBlocks.SignalBuilders.SignalBuilder;
import BuildingBlocks.TrajectoryRecord;
import BuildingBlocks.TreeNode;
import eu.quanticol.moonlight.signal.Signal;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


public abstract class AbstractFitnessFunction<T> implements Function<TreeNode, Double> {

    public final static double PENALTY_VALUE = 1.0;
    protected SignalBuilder<T> signalBuilder;

    public SignalBuilder<T> getSignalBuilder() {return this.signalBuilder;}

    public abstract BiFunction<TreeNode, double[], Double> getObjective();

    public List<T> getPositiveTraining() {
        return null;
    }

    public List<T> getNegativeTraining() {
        return null;
    }

    public List<T> getPositiveTest() {
        return null;
    }

    public List<T> getNegativeTest() {
        return null;
    }

    public double monitorSignal(Signal<TrajectoryRecord> signal, TreeNode solution, boolean min) {
        if (signal.size() <= solution.getNecessaryLength()) {
            return - PENALTY_VALUE;
        }
        double temp = solution.getOperator().apply(signal).monitor(signal).valueAt(signal.start());
        return (min) ? temp : - temp;
    }

}
