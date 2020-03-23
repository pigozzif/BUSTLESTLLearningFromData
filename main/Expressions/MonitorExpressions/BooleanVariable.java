package Expressions.MonitorExpressions;

import BuildingBlocks.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import core.src.main.java.eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class BooleanVariable implements MonitorExpression {

    private final String string;

    public BooleanVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        return TemporalMonitor.atomicMonitor(x -> {if (x.getBool(this.string)) {
                                                return Double.POSITIVE_INFINITY;} else { return Double.NEGATIVE_INFINITY;}
                                                });
    }

}
