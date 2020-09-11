package BuildingBlocks;

import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import java.util.Objects;
import java.util.function.Function;


public class TreeNode {
    // TODO: in reality, the doubles for the intervals could become integers or shorts
    private double start;
    private double end;
    private Function<Signal<TrajectoryRecord>, TemporalMonitor<TrajectoryRecord, Double>> func;
    private double necessaryLength;
    private TreeNode firstChild;
    private TreeNode secondChild;
    private String symbol;

    public TreeNode(String code) {
        this.firstChild = null;
        this.secondChild = null;
        this.start = -1.0;
        this.end = -1.0;
        this.necessaryLength = 0.0;
        this.symbol = null;
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
        this.start = s;
        this.end = e;
    }

    public void setNecessaryLength(double horizon) {
        this.necessaryLength = horizon;
    }

    public double getNecessaryLength() {
        return this.necessaryLength;
    }

    public void setSymbol(String s) {
        this.symbol = s;
        if (this.start != -1.0) {
            this.symbol += " I=[" + this.start + " " + this.end + "]";
        }
    }

    public String getSymbol() {
        return this.symbol;
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
