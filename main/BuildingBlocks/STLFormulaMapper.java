package BuildingBlocks;

import TreeNodes.*;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class STLFormulaMapper implements Function<Tree<String>, AbstractTreeNode> {

    @Override
    public AbstractTreeNode apply(Tree<String> root) {
        return parseSubTree(root, new ArrayList<Tree<String>>() {{ add(null); }});
    }

    public static AbstractTreeNode parseSubTree(Tree<String> currentNode, List<Tree<String>> ancestors) {
        List<Tree<String>> children = currentNode.childStream().collect(Collectors.toList());
        Tree<String> testChild = children.get(0);
        for (MonitorExpressions op : MonitorExpressions.values()) {
            if (op.toString().equals(testChild.content())) {
                ancestors.add(currentNode);
                return createMonitor(op, getSiblings(testChild, ancestors), ancestors);//expression.get().createMonitor(getSiblings(testChild, ancestors), ancestors);
            }
        }
        ancestors.add(currentNode);
        return parseSubTree(testChild, ancestors);
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

    public static AbstractTreeNode createMonitor(MonitorExpressions op, List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        boolean optimize = ProblemClass.isLocalSearch;
        switch (op) {
            case PROP:
                return new NumericTreeNode(siblings, optimize);
            case NOT:
                return new NotTreeNode(siblings, ancestors);
            case AND:
                return new AndTreeNode(siblings, ancestors);
            case UNTIL : case SINCE:
                return new BinaryTemporalTreeNode(op, siblings, op.toString(), ancestors, optimize);
            default:
                return new UnaryTemporalTreeNode(op, siblings, op.toString(), ancestors, optimize);
        }
    }

}
