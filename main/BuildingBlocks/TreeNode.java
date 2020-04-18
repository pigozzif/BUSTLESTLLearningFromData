package BuildingBlocks;

import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import java.util.function.Function;


public class TreeNode {

    private Double start;
    private Double end;
    private double tempStart;
    private double tempEnd;
    private Function<Signal<TrajectoryRecord>, TemporalMonitor<TrajectoryRecord, Double>> func;
    private double necessaryLength;  // TODO: probably not necessary, only for debugging
    private TreeNode parent;
    private TreeNode firstChild;
    private TreeNode secondChild;

    public TreeNode(TreeNode parent) {
        this.parent = parent;
        this.firstChild = null;
        this.secondChild = null;
        this.start = null;
        this.end = null;
        this.tempStart = 0.0;
        this.tempEnd = 0.0;
        this.necessaryLength = 0.0;
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

    public TreeNode getParent() {
        return this.parent;
    }

    public static double clip(TreeNode node, Signal<TrajectoryRecord> signal) throws ExceptionInInitializerError {
        if (node == null) {
            return 0.0;
        }
        else if (node.getStart() == null) {
            return Math.max(TreeNode.clip(node.getFirstChild(), signal), TreeNode.clip(node.getSecondChild(), signal));
        }
        double tempStart = node.getStart();
        double end = node.getEnd();
        double tempNecessaryLength = Math.max(TreeNode.clip(node.getFirstChild(), signal), TreeNode.clip(node.getSecondChild(), signal)); // TODO: does not work with until
        double tempEnd = Math.min(end, signal.size() - 1 - tempNecessaryLength);
        if (tempEnd <= tempStart) {
            tempStart = Math.max(0.0, tempStart - end + tempEnd);
        }
        if (tempEnd <= 0 && tempStart <= 0) {
            throw new ExceptionInInitializerError();
        }
        node.setTempInterval(tempStart, tempEnd);
        return tempEnd + tempNecessaryLength;
    }

    public Double getStart() {
        return this.start;
    }

    public Double getEnd() {
        return this.end;
    }

    public Interval createInterval() {
        return new Interval(this.tempStart, this.tempEnd);
    }

    public void setInterval(double s, double e) {
        this.start = s;
        this.end = e;
    }

    public void setTempInterval(double s, double e) {
        this.tempStart = s;
        this.tempEnd = e;
    }

    public void setNecessaryLength(double horizon) {
        this.necessaryLength = horizon;
    }

    public double getNecessaryLength() {
        return this.necessaryLength;
    }

}
