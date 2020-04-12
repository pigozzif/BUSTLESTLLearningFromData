package Expressions.MonitorExpressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TrajectoryRecord;
import Expressions.ValueExpressions.Perc;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.formula.Interval;

import java.util.List;


public enum Operator implements MonitorExpression {

    NOT(".not"),
    OR(".or"),
    UNTIL(".until"),
    GLOBALLY(".globally"),
    EVENTUALLY(".eventually");

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
            case UNTIL:
                Perc startPerc = new Perc(siblings.get(2).getChildren());
                Perc length = new Perc(siblings.get(3).getChildren());  // TODO: think about density of values over [0.0, 100.0]
                Double start = startPerc.getValue();
                System.out.println("UNTIL INTERVAL: " + start + " " + (start + length.getValue()));
                return TemporalMonitor.untilMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)),
                        null, STLFormulaMapper.parseSubTree(siblings.get(1)),
                        //new Interval(start, start + length.getValue()), STLFormulaMapper.parseSubTree(siblings.get(1)),
                        //new Interval(0.0, 100.0), STLFormulaMapper.parseSubTree(siblings.get(1)),
                        new DoubleDomain());
            case GLOBALLY:
                Perc startInterval = new Perc(siblings.get(1).getChildren());
                Perc lengthInterval = new Perc(siblings.get(2).getChildren());
                Double s = startInterval.getValue();
                System.out.println("GLOBALLY INTERVAL: " + s + " " + (s + lengthInterval.getValue()));
                return TemporalMonitor.globallyMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)),
                        new DoubleDomain(),
                        new Interval(0.0, 100.0));
                        //new Interval(s, s + lengthInterval.getValue()));
                        //null);
            default:
                Perc startInter = new Perc(siblings.get(1).getChildren());
                Perc lengthInter = new Perc(siblings.get(2).getChildren());
                Double beginning = startInter.getValue();
                System.out.println("EVENTUALLY INTERVAL: " + beginning + " " + (beginning + lengthInter.getValue()));
                return TemporalMonitor.eventuallyMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)),
                        new DoubleDomain(),
                        new Interval(0.0, 100.0));
                        //new Interval(beginning, beginning + lengthInter.getValue()));
                        //null);
        }
    }

}
