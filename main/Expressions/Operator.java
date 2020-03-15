package Expressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import core.src.main.java.eu.quanticol.moonlight.formula.DoubleDomain;

import java.util.List;


public enum Operator implements MonitorExpression {

    NOT(".not"),
    OR(".or"),
    UNTIL(".until");

    private final String string;

    Operator(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        switch(this) {
            case NOT:
                return TemporalMonitor.notMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)), new DoubleDomain());
            case OR:
                return TemporalMonitor.orMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)), new DoubleDomain(),
                        STLFormulaMapper.parseSubTree(siblings.get(1)));
            default:
                return TemporalMonitor.untilMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)),
                        STLFormulaMapper.parseSubTree(siblings.get(1)), new DoubleDomain());
        }
    }

}
