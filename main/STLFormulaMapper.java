import Expressions.BooleanConstant;
import Expressions.Expression;
import Expressions.Operator;
import Expressions.Variable;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


public class STLFormulaMapper implements Function<Node<String>, TemporalMonitor<Pair<Double, Double>, Double>> {

    @Override
    public TemporalMonitor<Pair<Double, Double>, Double> apply(Node<String> root, Listener listener) {
        /*List<Node<Expression>> leaves = new ArrayList<>();
        if (root.getContent().equals(Expression.EXPRESSION_STRING)) {
            for (Node<String> child : root.getChildren()) {
                leaves.add(singleMap(child));
            }
        }
        else {
            leaves = Collections.singletonList(singleMap(root));
        }*/

    }

    private TemporalMonitor<Pair<Double, Double>, Double> singleMap(Node<String> currentNode) {
        String string = currentNode.getContent();
        for (BooleanConstant constant : BooleanConstant.values()) {
            if (constant.toString().equals(string)) {
                return ;
            }
        }
        List<Node<String>> children = currentNode.getChildren();
        for (Node<String> child : children) {
            if (child.getContent().equals(Operator.NOT.toString())) {
                return ;
            }
            else if (child.getContent().equals(Operator.OR.toString())) {
                return ;
            }
        }
        /*if (currentNode.getChildren().isEmpty()) {
            return new Node<>(fromStringToExpression(currentNode.getContent()));
        }
        if (currentNode.getChildren().size() == 1) {
            return singleMap(currentNode.getChildren().get(0));
        }
        Node<Expression> node = singleMap(currentNode.getChildren().get(0));
        for (int i = 1; i < node.getChildren().size(); ++i) {
            node.getChildren().add(singleMap(currentNode.getChildren().get(i)));
        }
        return node;*/
    }

    private Expression fromStringToExpression(String string) {
        for (Operator operator : Operator.values()) {
            if (operator.toString().equals(string)) {
                return operator;
            }
        }
        for (BooleanConstant constant : BooleanConstant.values()) {
            if (constant.toString().equals(string)) {
                return constant;
            }
        }
        //if (string.matches("[a-zA-Z]+[0-9.]+")) {
        return new Variable(string);
        //}
    }

    private List<Node<String>> getSiblings(Node<String> node) {
        List<Node<String>> res = node.getParent().getChildren();
        res.remove(node);
        return res;
    }

}
