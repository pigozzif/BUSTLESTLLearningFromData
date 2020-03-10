package Expressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class NumericalVariable implements MonitorExpression {

    private final String string;

    public NumericalVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        CompareSign firstSibling = (CompareSign) STLFormulaMapper.fromStringToValueExpression(siblings.get(0).getContent());
        return TemporalMonitor.atomicMonitor(x -> firstSibling.getValue().apply(x.getDouble(this.string),
                parseNumber(siblings)));
    }

    private double parseNumber(List<Node<String>> siblings) {
        Digit secondSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(siblings.get(1).getContent());
        Digit thirdSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(siblings.get(2).getContent());
        Digit fourthSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(siblings.get(3).getContent());
        return secondSibling.getValue() * thirdSibling.getValue() * (Math.pow(10, fourthSibling.getValue()));
    }

}
