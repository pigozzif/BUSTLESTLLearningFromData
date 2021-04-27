package BuildingBlocks.FitnessFunctions;

import TreeNodes.AbstractTreeNode;
import BuildingBlocks.SignalBuilders.UnsupervisedSignalBuilder;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import LocalSearch.LocalSearch;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class UnsupervisedFitnessFunction extends AbstractFitnessFunction<Signal<Map<String, Double>>[]> {

    private final List<Signal<Map<String, Double>>[]> signals;
    private final int numFragments;
    private int num = 1;

    public UnsupervisedFitnessFunction(boolean localSearch) throws IOException {
        super(localSearch);
        this.signalBuilder = new UnsupervisedSignalBuilder();
        List<Integer> boolIndexes = new ArrayList<>() {{ }};
        List<Integer> doubleIndexes = new ArrayList<>() {{add(4); add(6); add(7); add(8); add(9); add(10); add(11);
            add(12); add(13);/* add(12); add(13); add(14); add(15); add(16); add(17); add(18); add(19);*/ }};
        this.signals = this.signalBuilder.parseSignals("./data/Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data9.csv", boolIndexes, doubleIndexes);
        this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
//        System.out.println("INDIVIDUAL: " + this.num);
        double count = 0.0;
//        int failedCount = 0;
//        int discardedCount = 0;
//        System.out.println(monitor);
        //System.out.println(monitor.equals(monitor.getFirstChild()));
//        cache.forEach(x -> {if (x.equals(monitor)) System.out.println(x);});
//        try {
            if (this.isLocalSearch) {
//                if (this.num == 6) {
//                    boolean temp = true;
//                }
                double[] newParams = LocalSearch.optimize(monitor, this, 15);
                monitor.propagateParameters(newParams);
            }
//        }
//        catch (Exception e) {
//            System.out.println(monitor);
//            throw e;
//        }
        for (Signal<Map<String, Double>>[] l : this.signals) {
            for (Signal<Map<String, Double>> s : l) {
//                try {
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
//                    throw e;
//                }
            }
        }
//        System.out.println("FAILED " + failedCount + " over a total of: " + this.numFragments);
//        System.out.println("DISCARDED " + discardedCount + " over a total of: " + this.numFragments + "\n");
        ++this.num;
//        System.out.println("Fitness: " + count / this.numFragments + "\n");
        return count / this.numFragments;
    }

    public double percSatisfaction(TemporalMonitor<Map<String, Double>, Double> monitor) {
        return 0.0;/*
        double metric = 0;
        for (Signal<Map<String, Double>> l : this.signals) {
            //for (Signal<Map<String, Double>> s : l) {
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
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            double count = 0.0;
            for (Signal<Map<String, Double>>[] l : this.signals) {
                for (Signal<Map<String, Double>> s : l) {
                    if (s.size() <= node.getNecessaryLength()) {
                        count += PENALTY_VALUE;
                    } else {
                        count += Math.abs(node.getOperator().apply(s).monitor(s).valueAt(s.end()));
                    }
                }
            }
            return count / this.numFragments;};
    }

}
