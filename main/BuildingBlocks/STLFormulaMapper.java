package BuildingBlocks;

import TreeNodes.*;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class STLFormulaMapper implements Function<Tree<String>, AbstractTreeNode> {

    private boolean toOptimize;

    public STLFormulaMapper(boolean localSearch) {
        this.toOptimize = localSearch;
    }

    public boolean getOptimizability() {
        return this.toOptimize;
    }

    public void setOptimizability(boolean opt) { this.toOptimize = opt; }

    @Override
    public AbstractTreeNode apply(Tree<String> root) {
        return parseSubTree(root, new ArrayList<>() {{ add(null); }});
    }

    public AbstractTreeNode parseSubTree(Tree<String> currentNode, List<Tree<String>> ancestors) {
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

    private List<Tree<String>> getSiblings(Tree<String> node, List<Tree<String>> ancestors) {
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

    private AbstractTreeNode createMonitor(MonitorExpressions op, List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        return switch (op) {
            case PROP -> new NumericTreeNode(this, siblings);
            case NOT -> new NotTreeNode(this, siblings, ancestors);
            case AND -> new AndTreeNode(this, siblings, ancestors);
            case UNTIL, SINCE -> new BinaryTemporalTreeNode(this, op, siblings, op.toString(), ancestors);
            default -> new UnaryTemporalTreeNode(this, op, siblings, op.toString(), ancestors);
        };
    }

}
