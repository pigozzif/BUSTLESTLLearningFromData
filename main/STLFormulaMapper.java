import Expressions.BooleanConstant;
import Expressions.Operator;
import core.src.main.java.eu.quanticol.moonlight.formula.DoubleDomain;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class STLFormulaMapper implements Function<Node<String>, TemporalMonitor<Record, Double>> {

    @Override
    public TemporalMonitor<Record, Double> apply(Node<String> root, Listener listener) {
        return singleMap(root);
    }

    private TemporalMonitor<Record, Double> singleMap(Node<String> currentNode) {
        String string = currentNode.getContent();
        for (BooleanConstant constant : BooleanConstant.values()) {
            if (constant.toString().equals(string)) {
                return TemporalMonitor.atomicMonitor(x -> {if (x.getBool(getSiblings(currentNode).get(0).getContent()) == constant.getValue()) {
                                                                    return 1.0;} else { return 0.0;}
                                                            });
            }
        }
        List<Node<String>> children = currentNode.getChildren();
        for (Node<String> child : children) {
            if (child.getContent().equals(Operator.NOT.toString())) {
                return TemporalMonitor.notMonitor(singleMap(getSiblings(child).get(0)), new DoubleDomain());
            }
            else if (child.getContent().equals(Operator.OR.toString())) {
                List<Node<String>> siblings = getSiblings(child);
                return TemporalMonitor.orMonitor(singleMap(siblings.get(0)), new DoubleDomain(), singleMap(siblings.get(1)));
            }
        }
        return TemporalMonitor.atomicMonitor(x -> 0.0);
    }

    /*private Expression fromStringToExpression(String string) {
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
    }*/

    private List<Node<String>> getSiblings(Node<String> node) {
        List<Node<String>> res = node.getParent().getChildren();
        res.remove(node);
        return res;
    }

}
