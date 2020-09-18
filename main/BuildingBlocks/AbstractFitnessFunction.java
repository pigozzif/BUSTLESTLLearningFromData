package BuildingBlocks;

import eu.quanticol.moonlight.signal.Signal;

import java.util.List;
import java.util.function.Function;


public abstract class AbstractFitnessFunction<T> implements Function<TreeNode, Double> {

    public final static double PENALTY_VALUE = 1.0;

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

    public double monitorSignal(Signal<TrajectoryRecord> signal, TreeNode solution) {
        if (signal.size() <= solution.getNecessaryLength()) {
            return -PENALTY_VALUE;
        }
        return solution.getOperator().apply(signal).monitor(signal).valueAt(signal.end());
    }

}
