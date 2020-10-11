package Expressions.MonitorExpressions;

import BuildingBlocks.NodeType;
import BuildingBlocks.ProblemClass;
import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.IntervalSymbol;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.formula.DoubleDomain;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.stream.Collectors;

// TODO: overall, this enum smells like a dirty cat
public enum Operator implements MonitorExpression {

    NOT(".not"),
    OR(".or"),
    SINCE(".since"),
    HISTORICALLY(".historically"),
    ONCE(".once"),
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
    public TreeNode createMonitor(List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        TreeNode newNode = new TreeNode();
        switch (this) {
            case NOT:
                TreeNode phi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                newNode.setFirstChild(phi);
                newNode.setNecessaryLength(phi.getNecessaryLength());
                newNode.setSymbol("NOT");
                newNode.setOperator(x -> TemporalMonitor.notMonitor(phi.getOperator().apply(x), new DoubleDomain()));
                return newNode;
            case OR:
                TreeNode leftPhi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                TreeNode rightPhi = STLFormulaMapper.parseSubTree(siblings.get(1), ancestors);
                newNode.setFirstChild(leftPhi);
                newNode.setSecondChild(rightPhi);
                newNode.setNecessaryLength(Math.max(leftPhi.getNecessaryLength(), rightPhi.getNecessaryLength()));
                newNode.setSymbol("AND");
                newNode.setOperator(x -> TemporalMonitor.andMonitor(leftPhi.getOperator().apply(x), new DoubleDomain(),
                        rightPhi.getOperator().apply(x)));
                return newNode;
                default:
                    int end = this.equipTemporalOperator(newNode, siblings, this.string.toUpperCase().replace(".", ""));
                    newNode.setType(NodeType.TEMPORAL_OPTIMIZABLE);
                    switch (this) {
                        case SINCE:
                            TreeNode firstPhi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                            TreeNode secondPhi = STLFormulaMapper.parseSubTree(siblings.get(3), ancestors);
                            newNode.setFirstChild(firstPhi);
                            newNode.setSecondChild(secondPhi);
                            newNode.setNecessaryLength(Math.max(firstPhi.getNecessaryLength(), secondPhi.getNecessaryLength()) + end);
                            newNode.setOperator(x -> TemporalMonitor.sinceMonitor(firstPhi.getOperator().apply(x),
                                    newNode.createInterval(), secondPhi.getOperator().apply(x),
                                    new DoubleDomain()));
                            return newNode;
                        case HISTORICALLY:
                            TreeNode historicallyPhi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                            newNode.setFirstChild(historicallyPhi);
                            newNode.setNecessaryLength(historicallyPhi.getNecessaryLength() + end);
                            newNode.setOperator(x -> TemporalMonitor.historicallyMonitor(historicallyPhi.getOperator().apply(x),
                                    new DoubleDomain(),
                                    newNode.createInterval()));
                            return newNode;
                        case ONCE:
                            TreeNode oncePhi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                            newNode.setFirstChild(oncePhi);
                            newNode.setNecessaryLength(oncePhi.getNecessaryLength() + end);
                            newNode.setOperator(x -> TemporalMonitor.onceMonitor(oncePhi.getOperator().apply(x),
                                    new DoubleDomain(),
                                    newNode.createInterval()));
                            return newNode;
                        case UNTIL:
                            TreeNode firstP = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                            TreeNode secondP = STLFormulaMapper.parseSubTree(siblings.get(3), ancestors);
                            newNode.setFirstChild(firstP);
                            newNode.setSecondChild(secondP);
                            newNode.setNecessaryLength(Math.max(firstP.getNecessaryLength(), secondP.getNecessaryLength()) + end);
                            newNode.setOperator(x -> TemporalMonitor.untilMonitor(firstP.getOperator().apply(x),
                                    newNode.createInterval(), secondP.getOperator().apply(x),
                                    new DoubleDomain()));
                            return newNode;
                        case GLOBALLY:
                            TreeNode globallyPhi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                            newNode.setFirstChild(globallyPhi);
                            newNode.setNecessaryLength(globallyPhi.getNecessaryLength() + end);
                            newNode.setOperator(x -> TemporalMonitor.globallyMonitor(globallyPhi.getOperator().apply(x),
                                    new DoubleDomain(),
                                    newNode.createInterval()));
                            return newNode;
                        default:
                            TreeNode eventuallyPhi = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
                            newNode.setFirstChild(eventuallyPhi);
                            newNode.setNecessaryLength(eventuallyPhi.getNecessaryLength() + end);
                            newNode.setOperator(x -> TemporalMonitor.eventuallyMonitor(eventuallyPhi.getOperator().apply(x),
                                    new DoubleDomain(),
                                    newNode.createInterval()));
                            return newNode;
                    }
        }
    }

    private int equipTemporalOperator(TreeNode node, List<Tree<String>> siblings, String message) {
        int start;
        int end;
        if (!ProblemClass.isLocalSearch) {
            IntervalSymbol startInterval = new IntervalSymbol(siblings.get(1).childStream().collect(Collectors.toList()));
            IntervalSymbol endInterval = new IntervalSymbol(siblings.get(2).childStream().collect(Collectors.toList()));
            start = (int) startInterval.getValue().doubleValue();
            end = (int) Math.max(1.0, endInterval.getValue()) + start;
        }
        else {
            start = 0;
            end = 0;
        }
        node.setInterval(start, end);
        node.setSymbol(message);
        return end;
    }

}
