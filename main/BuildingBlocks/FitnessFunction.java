package BuildingBlocks;

import Expressions.MonitorExpressions.BooleanVariable;
import it.units.malelab.jgea.core.function.NonDeterministicFunction;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


public class FitnessFunction implements NonDeterministicFunction<TreeNode, Double> {

    private final List<Signal<TrajectoryRecord>[]> signals;
    private final int numFragments;
    private int num = 1;

    public FitnessFunction(String fileName) throws IOException {
        BufferedReader reader = SignalBuilder.createReaderFromFile(fileName);
        reader.readLine();
        List<Integer> boolIndexes = new ArrayList<Integer>() {{ add(1); add(2); add(3); }};
        List<Integer> doubleIndexes = new ArrayList<Integer>() {{ add(4); add(5); add(6); add(7); add(8); add(9); add(10); add(11);
            add(12); add(13);/* add(12); add(13); add(14); add(15); add(16); add(17); add(18); add(19);*/ }};
        this.signals = SignalBuilder.parseSignals(reader, boolIndexes, doubleIndexes);
        this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
        reader.close();
    }

    @Override
    public Double apply(TreeNode monitor, Random random, Listener listener) {
        System.out.println("INDIVIDUAL: " + this.num);
        double count = 0.0;
//        int failedCount = 0;
//        int discardedCount = 0;
        System.out.println(monitor);
        for (Signal<TrajectoryRecord>[] l : this.signals) {
            double localCount = 0;
            for (Signal<TrajectoryRecord> s : l) {
//                try {
                    if (s.size() <= monitor.getNecessaryLength()) {
//                        discardedCount += 1;
                        localCount -= BooleanVariable.VALUE;
                    }
                    else {
                        localCount += monitor.getOperator().apply(s).monitor(s).valueAt(s.end());
                    }
//                } catch (Exception e) {
//                    failedCount += 1;
//                    throw e;
//                }
            }
            count += localCount;
        }
//        System.out.println("FAILED " + failedCount + " over a total of: " + this.numFragments);
//        System.out.println("DISCARDED " + discardedCount + " over a total of: " + this.numFragments + "\n");
        ++this.num;
        System.out.println("Fitness: " + count / this.signals.size() + "\n");
        return count / this.signals.size();
    }

}
