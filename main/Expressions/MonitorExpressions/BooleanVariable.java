package Expressions.MonitorExpressions;

import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.BooleanConstant;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.stream.Collectors;


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
    public TreeNode createMonitor(List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        TreeNode newNode = new TreeNode();
        BooleanConstant sibling = new BooleanConstant(siblings.get(0).childStream().collect(Collectors.toList()).get(0));
        newNode.setSymbol(this.string + " is " + sibling.toString());
        newNode.setOperator(x -> TemporalMonitor.atomicMonitor(y -> {if (y.getBool(this.string) == sibling.getValue()) {
                                                return VALUE;} else { return -VALUE;} }));
        return newNode;
    }

}
