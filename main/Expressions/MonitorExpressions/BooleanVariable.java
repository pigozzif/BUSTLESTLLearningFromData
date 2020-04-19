package Expressions.MonitorExpressions;

import BuildingBlocks.TrajectoryRecord;
import BuildingBlocks.TreeNode;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class BooleanVariable implements MonitorExpression {

    private final String string;

    public BooleanVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TreeNode createMonitor(List<Node<String>> siblings, TreeNode parent) {
        TreeNode newNode = new TreeNode(parent);
        newNode.setSymbol(this.string.toCharArray());
        newNode.setOperator(x -> TemporalMonitor.atomicMonitor(y -> {if (y.getBool(this.string)) {
                                                return Double.POSITIVE_INFINITY;} else { return Double.NEGATIVE_INFINITY;}
                                                }));
        return newNode;
    }

}
