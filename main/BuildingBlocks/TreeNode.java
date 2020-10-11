package BuildingBlocks;

import Expressions.ValueExpressions.CompareSign;
import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.core.util.Sized;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


public class TreeNode implements Sized {
    // TODO: in reality, the doubles for the intervals could become integers or shorts
    private double start;
    private double end;
    private Function<Signal<TrajectoryRecord>, TemporalMonitor<TrajectoryRecord, Double>> func;
    private int necessaryLength;
    private TreeNode firstChild;
    private TreeNode secondChild;
    private String symbol;
    private NodeType nodeType;

    public TreeNode() {
        this.firstChild = null;
        this.secondChild = null;
        this.start = -1.0;
        this.end = -1.0;
        this.necessaryLength = 0;
        this.symbol = null;
        this.nodeType = NodeType.NOT_OPTIMIZABLE;
    }

    public Function<Signal<TrajectoryRecord>, TemporalMonitor<TrajectoryRecord, Double>> getOperator() {
        return this.func;
    }

    public void setOperator(Function<Signal<TrajectoryRecord>, TemporalMonitor<TrajectoryRecord, Double>> func) {
        this.func = func;
    }

    public TreeNode getSecondChild() {
        return this.secondChild;
    }

    public TreeNode getFirstChild() {
        return this.firstChild;
    }

    public void setSecondChild(TreeNode child) {
        this.secondChild = child;
    }

    public void setFirstChild(TreeNode child) {
        this.firstChild = child;
    }

    public Interval createInterval() {
        return new Interval(this.start, this.end);
    }

    public void setInterval(double s, double e) {
        this.start = (int) s;
        this.end = (int) e;
    }

    public void setNecessaryLength(int horizon) {
        this.necessaryLength = horizon;
    }

    public int getNecessaryLength() {
        return this.necessaryLength;
    }

    public void setSymbol(String s) {
        this.symbol = s;
    }

    public String getSymbol() {
        if (this.start != -1.0) {
            return this.symbol + " I=[" + this.start + " " + this.end + "]";
        }
        return this.symbol;
    }

    public void setType(NodeType newType) {
        this.nodeType = newType;
    }

    public List<String[]> getVariables() {
        List<String[]> ans  = new ArrayList<>();
        this.getVariablesAux(ans);
        return ans;
    }

    public void getVariablesAux(List<String[]> temp) {
        if (this.nodeType == NodeType.NUMERIC_OPTIMIZABLE) {
            temp.add(this.symbol.split(" "));
        }
        if (this.firstChild != null) this.firstChild.getVariablesAux(temp);
        if (this.secondChild != null) this.secondChild.getVariablesAux(temp);
    }

    public int getNumBounds() {
        int ans = 0;
        if (this.nodeType == NodeType.TEMPORAL_OPTIMIZABLE) {
            ans += 2;
        }
        ans += (this.firstChild != null) ? this.firstChild.getNumBounds() : 0;
        ans += (this.secondChild != null) ? this.secondChild.getNumBounds() : 0;
        return ans;
    }

    public void propagateParameters(double[] parameters) {
        this.propagateParametersAux(parameters, new int[] {0, this.getNumBounds()});
    }

    public int[] propagateParametersAux(double[] parameters, int[] idxs) {
        if (idxs[1] >= parameters.length && idxs[0] >= this.getNumBounds()) return idxs;
        switch (this.nodeType) {
            case NUMERIC_OPTIMIZABLE:
                String[] test = this.symbol.split(" ");
                for (CompareSign cs : CompareSign.values()) {
                    if (cs.toString().equals(test[1])) {
                        double tempParam = parameters[idxs[1]];
                        this.func = x -> TemporalMonitor.atomicMonitor(y -> cs.getValue().apply(y.getDouble(test[0]), tempParam));
                        this.setSymbol(test[0] + " " + test[1] + " " + tempParam);
                        ++idxs[1];
                        break;
                    }
                }
                break;
            case TEMPORAL_OPTIMIZABLE:
                int start = (int) parameters[idxs[0]];
                int length = (int) parameters[idxs[0] + 1];
                this.setInterval(start, start + length);
                idxs[0] += 2;
                break;
        }
        if (this.firstChild != null) idxs = this.firstChild.propagateParametersAux(parameters, idxs);
        if (this.secondChild != null) idxs = this.secondChild.propagateParametersAux(parameters, idxs);
        return idxs;
    }

    @Override
    public int size() {
        int ans = 1;
        ans += (this.firstChild != null) ? this.firstChild.size() : 0;
        ans += (this.secondChild != null) ? this.secondChild.size() : 0;
        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        else if (o.getClass() != this.getClass()) {
            return false;
        }
        final TreeNode other = (TreeNode) o;
        if (!Objects.equals(this.symbol, other.getSymbol())) {
            return false;
        }
        else if (!Objects.equals(this.createInterval(), other.createInterval())) {
            return false;
        }
        else if (!Objects.equals(this.firstChild, other.getFirstChild())) {
            return false;
        }
        return Objects.equals(this.secondChild, other.getSecondChild());
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result + (this.symbol == null ? 0 : this.symbol.hashCode());
        result = 31 * result + (int) this.start;
        result = 31 * result + (int) this.end;
        result = 31 * result + (this.firstChild == null ? 0 : this.firstChild.hashCode());
        result = 31 * result + (this.secondChild == null ? 0 : this.secondChild.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return traversePreOrder(this);
    }

    private static String traversePreOrder(TreeNode node) {
        if (node == null) {
            return "\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(node.getSymbol());
        String pointerRight = "└──";
        boolean hasRightChild = node.getFirstChild() != null;
        String pointerLeft = (hasRightChild) ? "├──" : "└──";
        traverseNodes(sb, "", pointerLeft, node.getSecondChild(), hasRightChild);
        traverseNodes(sb, "", pointerRight, node.getFirstChild(), false);
        sb.append("\n");
        return sb.toString();
    }

    private static void traverseNodes(StringBuilder sb, String padding, String pointer, TreeNode node, boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.getSymbol());
            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            }
            else {
                paddingBuilder.append("   ");
            }
            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            boolean hasRightChild = node.getFirstChild() != null;
            String pointerLeft = (hasRightChild) ? "├──" : "└──";
            traverseNodes(sb, paddingForBoth, pointerLeft, node.getSecondChild(), hasRightChild);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getFirstChild(), false);
        }
    }

}
