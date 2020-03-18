package BuildingBlocks;

import Expressions.*;
import Expressions.MonitorExpressions.MonitorExpression;
import Expressions.ValueExpressions.ValueExpression;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class STLFormulaMapper implements Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> {

    private static final List<ValueExpression<?>> valueExpressions = ExpressionsFactory.createValueExpressions();
    private static final List<MonitorExpression> monitorExpressions = ExpressionsFactory.createMonitorExpressions();

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> apply(Node<String> root, Listener listener) {
        return parseSubTree(root);
    }

    public static TemporalMonitor<TrajectoryRecord, Double> parseSubTree(Node<String> currentNode) {
        List<Node<String>> children = currentNode.getChildren();
        Node<String> testChild = (!children.get(0).getContent().equals(ValueExpression.EXPRESSION_STRING)) ? children.get(0) : children.get(1);
        MonitorExpression expression = fromStringToMonitorExpression(testChild.getContent());
        return expression.createMonitor(getSiblings(currentNode));
    }

    public static MonitorExpression fromStringToMonitorExpression(String string) {
        return monitorExpressions.stream().filter(x -> x.toString().equals(string)).findAny().get();
    }

    public static ValueExpression<?> fromStringToValueExpression(String string) {
        return valueExpressions.stream().filter(x -> x.toString().equals(string)).findAny().get();
    }

    private static List<Node<String>> getSiblings(Node<String> node) {
        List<Node<String>> res = node.getParent().getChildren();
        res.remove(node);
        return res;
    }

}
