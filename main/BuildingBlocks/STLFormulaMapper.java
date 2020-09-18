package BuildingBlocks;

import Expressions.*;
import Expressions.MonitorExpressions.MonitorExpression;
import Expressions.ValueExpressions.ValueExpression;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class STLFormulaMapper implements Function<Tree<String>, TreeNode> {

    private static final List<ValueExpression<?>> valueExpressions = ExpressionsFactory.createValueExpressions();
    private static final List<MonitorExpression> monitorExpressions = ExpressionsFactory.createMonitorExpressions();

    @Override
    public TreeNode apply(Tree<String> root) {
        return parseSubTree(root, new ArrayList<Tree<String>>() {{ add(null); }}, root);
    }

    public static TreeNode parseSubTree(Tree<String> currentNode, List<Tree<String>> ancestors, Tree<String> root) {
        List<Tree<String>> children = currentNode.childStream().collect(Collectors.toList());
        Tree<String> testChild = children.get(0);
        Optional<MonitorExpression> expression = monitorExpressions.stream().filter(x -> x.toString().equals(testChild.content())).findAny();
        if (expression.isPresent()) {
            ancestors.add(currentNode);
            return expression.get().createMonitor(getSiblings(testChild, ancestors), ancestors, root);
        }
        ancestors.add(currentNode);
        return parseSubTree(testChild, ancestors, root);
    }

    public static Optional<ValueExpression<?>> fromStringToValueExpression(Tree<String> string) {
        if (Expression.singletonExpressions.contains(string.content())) {
            return fromStringToValueExpression(string.childStream().collect(Collectors.toList()).get(0));
        }
        return valueExpressions.stream().filter(x -> x.toString().equals(string.content())).findAny();
    }

    private static List<Tree<String>> getSiblings(Tree<String> node, List<Tree<String>> ancestors) {
        Tree<String> parent = ancestors.get(ancestors.size() - 1);
        if (parent == null) {
            return Collections.emptyList();
        }
        List<Tree<String>> res = parent.childStream().collect(Collectors.toList());
        res.remove(node);
        if (res.isEmpty()) {
            return getSiblings(parent, ancestors.stream().filter(x -> x != parent).collect(Collectors.toList()));
        }
        return res;
    }

}
