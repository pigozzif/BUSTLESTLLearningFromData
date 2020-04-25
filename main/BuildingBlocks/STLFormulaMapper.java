package BuildingBlocks;

import Expressions.*;
import Expressions.MonitorExpressions.MonitorExpression;
import Expressions.ValueExpressions.ValueExpression;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.*;


public class STLFormulaMapper implements Function<Node<String>, TreeNode> {

    private static final List<ValueExpression<?>> valueExpressions = ExpressionsFactory.createValueExpressions();
    private static final List<MonitorExpression> monitorExpressions = ExpressionsFactory.createMonitorExpressions();

    @Override
    public TreeNode apply(Node<String> root, Listener listener) {
        root.propagateParentship();
        return parseSubTree(root, null);
    }

    public static TreeNode parseSubTree(Node<String> currentNode, TreeNode parent) {
        List<Node<String>> children = currentNode.getChildren();
        Node<String> testChild = children.get(0);
        Optional<MonitorExpression> expression = monitorExpressions.stream().filter(x -> x.toString().equals(testChild.getContent())).findAny();
        if (expression.isPresent()) {
            return expression.get().createMonitor(getSiblings(testChild), parent);
        }
        return parseSubTree(testChild, parent);
    }

    public static Optional<ValueExpression<?>> fromStringToValueExpression(Node<String> string) {
        if (Expression.singletonExpressions.contains(string.getContent())) {
            return fromStringToValueExpression(string.getChildren().get(0));
        }
        return valueExpressions.stream().filter(x -> x.toString().equals(string.getContent())).findAny();
    }

    private static List<Node<String>> getSiblings(Node<String> node) {
        if (node.getParent() == null) {
            return Collections.emptyList();
        }
        List<Node<String>> res = new ArrayList<>(node.getParent().getChildren());
        res.remove(node);
        if (res.isEmpty()) {
            return getSiblings(node.getParent());
        }
        return res;
    }

}
