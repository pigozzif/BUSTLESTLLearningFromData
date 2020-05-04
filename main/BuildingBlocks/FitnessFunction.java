package BuildingBlocks;

import it.units.malelab.jgea.core.function.NonDeterministicFunction;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FitnessFunction implements NonDeterministicFunction<TreeNode, Double> {

    private final List<Signal<TrajectoryRecord>[]> signals;
    private final int numFragments;
    private int num = 0;

    public FitnessFunction(String fileName) throws IOException {
        BufferedReader reader = SignalBuilder.createReaderFromFile(fileName);
        reader.readLine();
        List<Integer> boolIndexes = new ArrayList<>() {{ add(1); add(2); add(3); }};
        List<Integer> doubleIndexes = new ArrayList<>() {{ add(4); add(5); add(6); add(7); add(8); add(9); add(10); add(11);
            add(12); add(13);/* add(12); add(13); add(14); add(15); add(16); add(17); add(18); add(19);*/ }};
        this.signals = SignalBuilder.parseSignals(reader, boolIndexes, doubleIndexes);
        this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
        reader.close();
    }

    @Override
    public Double apply(TreeNode monitor, Random random, Listener listener) {
        System.out.println("INDIVIDUAL: " + num);
        double count = 0.0;
        int failedCount = 0;
        int discardedCount = 0;
        //monitor.print(System.out);
        for (Signal<TrajectoryRecord>[] l : this.signals) {
            double localCount = 0;
            for (Signal<TrajectoryRecord> s : l) {
                try {
                    if (s.size() <= monitor.getNecessaryLength()) {
                        discardedCount += 1;
                        localCount += Double.NEGATIVE_INFINITY;
                    } else {
                        localCount += monitor.getOperator().apply(s).monitor(s).valueAt(s.end());
                    }
                } catch (Exception e) {
                    monitor.print(System.out);
                    System.out.println(s);
                    throw e;
                }
            }
            count += localCount;
        }
        System.out.println("FAILED " + failedCount + " over a total of: " + this.numFragments);
        System.out.println("DISCARDED " + discardedCount + " over a total of: " + this.numFragments + "\n");
        ++num;
        return count / this.signals.size();
        //return this.signals.stream().mapToDouble(x -> monitor.monitor(x).valueAt(0.0)).average().orElse(Double.MIN_VALUE);
    }

}
