package Expressions;

import Entities.STLFormulaMapper;
import Entities.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import core.src.main.java.eu.quanticol.moonlight.formula.DoubleDomain;

import java.util.List;


public enum Operator implements Expression<String> {

    NOT(".not"),
    OR(".or");

    private final String string;

    Operator(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        return (this == NOT) ? TemporalMonitor.notMonitor(STLFormulaMapper.singleMap(siblings.get(0)), new DoubleDomain()) :
                TemporalMonitor.orMonitor(STLFormulaMapper.singleMap(siblings.get(0)), new DoubleDomain(), STLFormulaMapper.singleMap(siblings.get(1)));
    }

    @Override
    public String getValue() {
        return this.string;
    }

}
