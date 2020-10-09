package Expressions.MonitorExpressions;

import BuildingBlocks.TreeNode;
import Expressions.Expression;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;


public interface MonitorExpression extends Expression {

    TreeNode createMonitor(List<Tree<String>> siblings, List<Tree<String>> ancestors);

}
