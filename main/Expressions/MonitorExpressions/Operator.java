package Expressions.MonitorExpressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.Perc;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.formula.DoubleDomain;

import java.util.List;


public enum Operator implements MonitorExpression {

    NOT(".not"),
    OR(".or"),
    UNTIL(".since"),
    GLOBALLY(".historically"),
    EVENTUALLY(".once");

    private final String string;

    Operator(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TreeNode createMonitor(List<Node<String>> siblings, TreeNode parent) {
        TreeNode newNode = new TreeNode(parent);
        switch(this) {
            case NOT:
                TreeNode phi = STLFormulaMapper.parseSubTree(siblings.get(0), newNode);
                newNode.setFirstChild(phi);
                newNode.setNecessaryLength(phi.getNecessaryLength());
                //newNode.setSymbol("\u00AC");
                newNode.setSymbol("NOT");
                newNode.setOperator(x -> TemporalMonitor.notMonitor(phi.getOperator().apply(x), new DoubleDomain()));
                return newNode;
            case OR:
                TreeNode leftPhi = STLFormulaMapper.parseSubTree(siblings.get(0), newNode);
                TreeNode rightPhi = STLFormulaMapper.parseSubTree(siblings.get(1), newNode);
                newNode.setFirstChild(leftPhi);
                newNode.setSecondChild(rightPhi);
                newNode.setNecessaryLength(Math.max(leftPhi.getNecessaryLength(), rightPhi.getNecessaryLength()));
                //newNode.setSymbol("\u2228");
                newNode.setSymbol("OR");
                newNode.setOperator(x -> TemporalMonitor.orMonitor(leftPhi.getOperator().apply(x), new DoubleDomain(),
                        rightPhi.getOperator().apply(x)));
                return newNode;
            case UNTIL:
                Perc startPerc = new Perc(siblings.get(2).getChildren());
                Perc length = new Perc(siblings.get(3).getChildren());
                Double start = startPerc.getValue();
                Double width = Math.max(1.0, length.getValue());
                TreeNode firstPhi = STLFormulaMapper.parseSubTree(siblings.get(0), newNode);
                TreeNode secondPhi = STLFormulaMapper.parseSubTree(siblings.get(1), newNode);
                newNode.setFirstChild(firstPhi);
                newNode.setSecondChild(secondPhi);
                newNode.setInterval(start, start + width);
                newNode.setNecessaryLength(Math.max(firstPhi.getNecessaryLength(), secondPhi.getNecessaryLength()) +
                        start + width);
                //newNode.setSymbol("\u0053");
                newNode.setSymbol("UNTIL");
                newNode.setOperator(x -> TemporalMonitor.sinceMonitor(firstPhi.getOperator().apply(x),
                        newNode.createInterval(), secondPhi.getOperator().apply(x),
                        new DoubleDomain()));
                return newNode;
            case GLOBALLY:
                Perc startInterval = new Perc(siblings.get(1).getChildren());
                Perc lengthInterval = new Perc(siblings.get(2).getChildren());
                Double s = startInterval.getValue();
                Double l = Math.max(1.0, lengthInterval.getValue());
                TreeNode globallyPhi = STLFormulaMapper.parseSubTree(siblings.get(0), newNode);
                newNode.setFirstChild(globallyPhi);
                newNode.setInterval(s, s + l);
                newNode.setNecessaryLength(globallyPhi.getNecessaryLength() + s + l);
                //newNode.setSymbol("\u27CF");
                newNode.setSymbol("HISTORICALLY");
                newNode.setOperator(x -> TemporalMonitor.historicallyMonitor(globallyPhi.getOperator().apply(x),
                        new DoubleDomain(),
                        newNode.createInterval()));
                return newNode;
            default:
                Perc startInter = new Perc(siblings.get(1).getChildren());
                Perc lengthInter = new Perc(siblings.get(2).getChildren());
                Double beginning = startInter.getValue();
                Double len = Math.max(1.0, lengthInter.getValue());
                TreeNode eventuallyPhi = STLFormulaMapper.parseSubTree(siblings.get(0), newNode);
                newNode.setFirstChild(eventuallyPhi);
                newNode.setInterval(beginning, beginning + len);
                newNode.setNecessaryLength(eventuallyPhi.getNecessaryLength() + beginning + len);
                //newNode.setSymbol("\u20DF");
                newNode.setSymbol("ONCE");
                newNode.setOperator(x -> TemporalMonitor.onceMonitor(eventuallyPhi.getOperator().apply(x),
                        new DoubleDomain(),
                        newNode.createInterval()));
                return newNode;
        }
    }

}
