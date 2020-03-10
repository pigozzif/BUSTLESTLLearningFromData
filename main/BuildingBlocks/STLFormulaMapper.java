package BuildingBlocks;

import Expressions.*;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class STLFormulaMapper implements Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> {

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
        for (Operator operator : Operator.values()) {
            if (operator.toString().equals(string)) {
                return operator;
            }
        }
        if (string.endsWith("_bool_")) {
            return new BooleanVariable(string);
        }
        return new NumericalVariable(string);
    }

    public static ValueExpression<?> fromStringToValueExpression(String string) {
        for (BooleanConstant constant : BooleanConstant.values()) {
            if (constant.toString().equals(string)) {
                return constant;
            }
        }
        for (CompareSign comp : CompareSign.values()) {
            if (comp.toString().equals(string)) {
                return comp;
            }
        }
        return new Digit(string);
    }

    private static List<Node<String>> getSiblings(Node<String> node) {
        List<Node<String>> res = node.getParent().getChildren();
        res.remove(node);
        return res;
    }

}
