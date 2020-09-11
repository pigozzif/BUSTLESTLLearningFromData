package Expressions.MonitorExpressions;

import BuildingBlocks.TreeNode;
import Expressions.Expression;
import it.units.malelab.jgea.core.Node;

import java.util.List;


public interface MonitorExpression extends Expression {

    TreeNode createMonitor(List<Node<String>> siblings, String content);

}
