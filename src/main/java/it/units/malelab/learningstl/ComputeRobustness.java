package it.units.malelab.learningstl;


import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SupervisedSignalBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ComputeRobustness {

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./robustness.csv"));
        TemporalMonitor<Map<String, Double>, Double> monitor = TemporalMonitor.andMonitor(TemporalMonitor.andMonitor(TemporalMonitor.atomicMonitor(y -> y.get("x1") - 0.32),
                new DoubleDomain(), TemporalMonitor.atomicMonitor(y -> 0.6 - y.get("x1"))), new DoubleDomain(),
                TemporalMonitor.globallyMonitor(TemporalMonitor.atomicMonitor(y -> y.get("x1") - 0.48), new DoubleDomain(), new Interval(12, 14)));
        //TemporalMonitor<Map<String, Double>, Double> monitor = TemporalMonitor.andMonitor(TemporalMonitor.eventuallyMonitor(TemporalMonitor.atomicMonitor(y -> y.get("x1") - 0.38),
        //        new DoubleDomain(), new Interval(22, 40)), new DoubleDomain(),
        //        TemporalMonitor.eventuallyMonitor(TemporalMonitor.andMonitor(TemporalMonitor.atomicMonitor(y -> y.get("x1") - 0.24), new DoubleDomain(), TemporalMonitor.atomicMonitor(y -> 0.43 - y.get("x1"))),
        //                new DoubleDomain(), new Interval(46, 49)));
        List<Signal<Map<String, Double>>> signals = (new SupervisedSignalBuilder()).parseSignals("./data/linear/");
        writer.write("robustness\n");
        for (Signal<Map<String, Double>> signal : signals) {
            writer.write(monitor.monitor(signal).valueAt(signal.start()) + "\n");
        }
        writer.close();
    }

}
