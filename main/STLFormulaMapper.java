import Expressions.Expression;
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
        List<Node<String>> leaves = new ArrayList<>();
        if (root.getContent().equals(Expression.EXPRESSION_STRING)) {
            for (Node<String> child : root.getChildren()) {
                leaves.add(singleMap(child));
            }
        }
        else {
            leaves = Collections.singletonList(singleMap(root));
        }
    }

    private Node<String> singleMap(Node<String> currentNode) {
        if (currentNode.getChildren().isEmpty()) {
            return new Node<>(currentNode.getContent());
        }
        if (currentNode.getChildren().size() == 1) {
            return singleMap(currentNode.getChildren().get(0));
        }
        Node<String> node = singleMap(currentNode.getChildren().get(0));
        for (int i = 1; i < node.getChildren().size(); ++i) {
            node.getChildren().add(singleMap(currentNode.getChildren().get(i)));
        }
        return node;
    }

}
