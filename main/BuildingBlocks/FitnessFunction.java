package BuildingBlocks;

import Expressions.ExpressionsFactory;
import it.units.malelab.jgea.core.function.NonDeterministicFunction;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FitnessFunction implements NonDeterministicFunction<TemporalMonitor<TrajectoryRecord, Double>, Double> {

    private final List<Signal<TrajectoryRecord>> signals;

    public FitnessFunction(String fileName) throws IOException {
        BufferedReader reader = SignalBuilder.createReaderFromFile(fileName);
        reader.readLine();
        String[] boolNames = ExpressionsFactory.retrieveBooleanNames();
        String[] doubleNames = ExpressionsFactory.retrieveNumericalNames();
        List<Integer> boolIndexes = new ArrayList<>() {{ add(3); add(20); add(21); }};
        List<Integer> doubleIndexes = new ArrayList<>() {{ add(1); add(2); add(4); add(5); add(6); add(7); add(8); add(9);
            add(10); add(11); add(12); add(13); add(14); add(15); add(16); add(17); add(18); add(19); }};
        signals = SignalBuilder.parseSignals(reader, boolNames, doubleNames, boolIndexes, doubleIndexes);
    }

    @Override
    public Double apply(TemporalMonitor<TrajectoryRecord, Double> monitor, Random random, Listener listener) {
        return this.signals.stream().mapToDouble(x -> monitor.monitor(x).valueAt(0)).average().orElse(-1.0);
    }

}
