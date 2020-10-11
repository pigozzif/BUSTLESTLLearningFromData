package BuildingBlocks.FitnessFunctions;

import BuildingBlocks.SignalBuilders.UdacitySignalBuilder;
import BuildingBlocks.TrajectoryRecord;
import BuildingBlocks.TreeNode;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;


public class UdacityFitnessFunction extends AbstractFitnessFunction<Signal<TrajectoryRecord>[]> {

    private final List<Signal<TrajectoryRecord>[]> signals;
    private final int numFragments;

    public UdacityFitnessFunction() throws IOException {
        UdacitySignalBuilder signalBuilder = new UdacitySignalBuilder();
        List<Integer> boolIndexes = new ArrayList<Integer>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<Integer>() {{add(1); add(2); add(3); }};
        this.signals = signalBuilder.parseSignals("./data/steering2p.csv", boolIndexes, doubleIndexes);
        this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
    }

    @Override
    public Double apply(TreeNode monitor) {
//        System.out.println("INDIVIDUAL: " + this.num);
        double count = 0.0;
        for (Signal<TrajectoryRecord>[] l : this.signals) {
            for (Signal<TrajectoryRecord> s : l) {
                    if (s.size() <= monitor.getNecessaryLength()) {
                        count += PENALTY_VALUE;
                    }
                    else {
                        count +=  Math.abs(monitor.getOperator().apply(s).monitor(s).valueAt(s.end()));
                    }
            }
        }
        return count / this.numFragments;
    }

    public double percSatisfaction(TemporalMonitor<TrajectoryRecord, Double> monitor) {
        return 0.0;/*
        double metric = 0;
        for (Signal<TrajectoryRecord> l : this.signals) {
            //for (Signal<TrajectoryRecord> s : l) {
                double local = 0;
                Signal<Double> out = monitor.monitor(l);
                System.out.println("Robustness: " + out.valueAt(out.end()));
                double time = out.start();
                double value = out.valueAt(out.start());
                Object[] temps = out.getTimeSet().toArray();
                double[] times = new double[temps.length];
                for (int i=0; i < times.length; ++i) times[i] = (double) temps[i];
                Arrays.sort(times);
                for (Double t : times) {
                    //System.out.println(t);
                    if (time < t) {
                        do {
                            local += (value > 0) ? 1 : 0;
                            time += 1;
                        } while (time < t);
                    }
                    Double temp = out.valueAt(t);
                    local += (temp > 0) ? 1 : 0;
                    time += 1;
                    value = temp;
                }
            //System.out.println(local);
            local /= l.size() - (l.end() - out.end()) - (out.start() - l.start());
            //System.out.println(local);
            metric += local;
           // }
        }
        return metric / this.signals.size();*/
    }

    @Override
    public BiFunction<TreeNode, double[], Double> getObjective() {
        return null;
    }
}
