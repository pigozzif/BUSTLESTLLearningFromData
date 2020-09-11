package BuildingBlocks;

import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MaritimeFitnessFunction implements Function<TreeNode, Double> {

    private final List<Signal<TrajectoryRecord>> signals;
    // private final int numFragments;
    // private int num = 1;
    private final static double PENALTY_VALUE = 1.0;

    public MaritimeFitnessFunction(String fileName) {
        MaritimeSignalBuilder signalBuilder = new MaritimeSignalBuilder();
        List<Integer> boolIndexes = new ArrayList<Integer>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<Integer>() {{add(0); add(1); }};
        this.signals = signalBuilder.parseSignals(fileName, boolIndexes, doubleIndexes);
        // this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
        // reader.close();
    }

    @Override
    public Double apply(TreeNode monitor, Listener listener) {
        return 0.0;
    }

}
