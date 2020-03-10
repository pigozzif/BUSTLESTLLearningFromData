package Expressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

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
        BooleanConstant firstSibling = (BooleanConstant) STLFormulaMapper.fromStringToValueExpression(siblings.get(0).getContent());
        return TemporalMonitor.atomicMonitor(x -> {if (x.getBool(this.string) == firstSibling.getValue()) {
            return 1.0;} else { return 0.0;}
        });
    }

}
