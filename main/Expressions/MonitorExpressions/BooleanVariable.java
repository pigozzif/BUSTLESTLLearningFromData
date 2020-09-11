package Expressions.MonitorExpressions;

import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.BooleanConstant;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class BooleanVariable implements MonitorExpression {

    private final String string;
    public static final double VALUE = 1.0;

    public BooleanVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TreeNode createMonitor(List<Node<String>> siblings, String content) {
        TreeNode newNode = new TreeNode(content);
        BooleanConstant sibling = new BooleanConstant(siblings.get(0).getChildren().get(0));
        newNode.setSymbol(this.string + " is " + sibling.toString());
        newNode.setOperator(x -> TemporalMonitor.atomicMonitor(y -> {if (y.getBool(this.string) == sibling.getValue()) {
                                                return VALUE;} else { return -VALUE;} }));
        return newNode;
    }

}
