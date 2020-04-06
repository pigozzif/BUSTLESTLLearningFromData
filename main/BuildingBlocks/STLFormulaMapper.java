package BuildingBlocks;

import Expressions.*;
import Expressions.MonitorExpressions.MonitorExpression;
import Expressions.ValueExpressions.ValueExpression;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class STLFormulaMapper implements Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> {

    private static final List<ValueExpression<?>> valueExpressions = ExpressionsFactory.createValueExpressions();
    private static final List<MonitorExpression> monitorExpressions = ExpressionsFactory.createMonitorExpressions();

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> apply(Node<String> root, Listener listener) {
        //System.out.println(root);
        root.propagateParentship();
        try {
            return parseSubTree(root);
        }
        catch (Exception e) {
            //System.out.println("#####");
            //root.prettyPrint(new PrintStream(System.out));
            //System.out.println(root);
            throw e;
        }
    }

    public static TemporalMonitor<TrajectoryRecord, Double> parseSubTree(Node<String> currentNode) {
        //if (!currentNode.getContent().equals(Expression.EXPRESSION_STRING)) {
        //    return null;
        //}
        List<Node<String>> children = currentNode.getChildren();
        try {
            Node<String> testChild = children.get(0);
            Optional<MonitorExpression> expression = fromStringToMonitorExpression(testChild);
            if (expression.isPresent()) {
                return expression.get().createMonitor(getSiblings(testChild));
            }
            return parseSubTree(testChild);
        }
        catch (Exception e) {
            //System.out.println(currentNode.getContent() + " and children: ");
            //currentNode.getChildren().forEach(x -> System.out.println(x.getContent()));
            throw e;
        }
    }

    public static Optional<MonitorExpression> fromStringToMonitorExpression(Node<String> string) {
        if (Expression.singletonExpressions.contains(string.getContent())) {
            try {
                return fromStringToMonitorExpression(string.getChildren().get(0));
            }
            catch (Exception e) {
                //System.out.println(string.getContent() + " and children: ");
                //string.getChildren().forEach(x -> System.out.println(x.getContent()));
                throw e;
            }
        }
        return monitorExpressions.stream().filter(x -> x.toString().equals(string.getContent())).findAny();
    }

    public static Optional<ValueExpression<?>> fromStringToValueExpression(Node<String> string) {
        if (Expression.singletonExpressions.contains(string.getContent())) {
            return fromStringToValueExpression(string.getChildren().get(0));
        }
        return valueExpressions.stream().filter(x -> x.toString().equals(string.getContent())).findAny();
    }

    private static List<Node<String>> getSiblings(Node<String> node) {
        List<Node<String>> res = new ArrayList<>(node.getParent().getChildren());
        res.remove(node);
        return res;
    }

}
