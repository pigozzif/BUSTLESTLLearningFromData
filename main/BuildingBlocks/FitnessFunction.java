package BuildingBlocks;

import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


public class FitnessFunction implements Function<TreeNode, Double> {

    private final List<Signal<TrajectoryRecord>[]> signals;
    private final int numFragments;
//    private int num = 1;
    private final static double PENALTY_VALUE = 1.0;

    public FitnessFunction(String fileName) throws IOException {
        I80SignalBuilder signalBuilder = new I80SignalBuilder();
        BufferedReader reader = signalBuilder.createReaderFromFile(fileName);
        reader.readLine();
        List<Integer> boolIndexes = new ArrayList<Integer>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<Integer>() {{add(1); add(2); add(3); add(4); add(6); add(7); add(8); add(9); add(10); add(11);
            add(12); add(13);/* add(12); add(13); add(14); add(15); add(16); add(17); add(18); add(19);*/ }};
        this.signals = signalBuilder.parseSignals(reader, boolIndexes, doubleIndexes);
        this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
        reader.close();
    }

    @Override
    public Double apply(TreeNode monitor, Listener listener) {
//        System.out.println("INDIVIDUAL: " + this.num);
        double count = 0.0;
//        int failedCount = 0;
//        int discardedCount = 0;
//        System.out.println(monitor);
        //System.out.println(monitor.equals(monitor.getFirstChild()));
//        cache.forEach(x -> {if (x.equals(monitor)) System.out.println(x);});
        for (Signal<TrajectoryRecord>[] l : this.signals) {
            for (Signal<TrajectoryRecord> s : l) {
 //               try {
                    if (s.size() <= monitor.getNecessaryLength()) {
//                        discardedCount += 1;
                        count += PENALTY_VALUE;
                    }
                    else {
                        count +=  Math.abs(monitor.getOperator().apply(s).monitor(s).valueAt(s.end()));
                    }
//                } catch (Exception e) {
//                    System.out.println(monitor);
//                    System.out.println(s);
//                    failedCount += 1;
 //                   throw e;
 //               }
            }
        }
//        System.out.println("FAILED " + failedCount + " over a total of: " + this.numFragments);
//        System.out.println("DISCARDED " + discardedCount + " over a total of: " + this.numFragments + "\n");
//        ++this.num;
        System.out.println("Fitness: " + count / this.signals.size() + "\n");
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

}
