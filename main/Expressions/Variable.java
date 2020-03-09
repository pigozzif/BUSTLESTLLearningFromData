package Expressions;

import Entities.STLFormulaMapper;
import Entities.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class Variable implements Expression<String> {

    private final String string;

    public Variable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        if (siblings.size() == 1) {
            BooleanConstant firstSibling = (BooleanConstant) STLFormulaMapper.fromStringToExpression(siblings.get(0).getContent());
            return TemporalMonitor.atomicMonitor(x -> {if (x.getBool(this.string) == firstSibling.getValue()) {
                return 1.0;} else { return 0.0;}
            });
        }
        else {
            CompareSign firstSibling = (CompareSign) STLFormulaMapper.fromStringToExpression(siblings.get(0).getContent());
            Digit secondSibling = (Digit) STLFormulaMapper.fromStringToExpression(siblings.get(1).getContent());
            return TemporalMonitor.atomicMonitor(x -> firstSibling.getValue().apply(x.getDouble(this.string),
                    Double.valueOf(secondSibling.getValue())));
        }
    }

    @Override
    public String getValue() {
        return this.string;
    }

}
