package Expressions;

import Entities.STLFormulaMapper;
import Entities.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class Variable implements MonitorExpression {

    private final String string;

    public Variable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        if (siblings.size() == 1) {
            return createMonitorForBoolean(siblings);
        }
        else {
            return createMonitorForDouble(siblings);
        }
    }

    private TemporalMonitor<TrajectoryRecord, Double> createMonitorForBoolean(List<Node<String>> siblings) {
        BooleanConstant firstSibling = (BooleanConstant) STLFormulaMapper.fromStringToExpression(siblings.get(0).getContent());
        return TemporalMonitor.atomicMonitor(x -> {if (x.getBool(this.string) == firstSibling.getValue()) {
            return 1.0;} else { return 0.0;}
        });
    }

    private TemporalMonitor<TrajectoryRecord, Double> createMonitorForDouble(List<Node<String>> siblings) {
        CompareSign firstSibling = (CompareSign) STLFormulaMapper.fromStringToExpression(siblings.get(0).getContent());
        Digit secondSibling = (Digit) STLFormulaMapper.fromStringToExpression(siblings.get(1).getContent());
        return TemporalMonitor.atomicMonitor(x -> firstSibling.getValue().apply(x.getDouble(this.string),
                Double.valueOf(secondSibling.getValue())));
    }

}
