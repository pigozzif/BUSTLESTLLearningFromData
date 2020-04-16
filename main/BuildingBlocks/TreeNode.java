package BuildingBlocks;

import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import java.util.function.Function;


public class TreeNode {

    private Double start;
    private Double end;
    private Function<Signal<TrajectoryRecord>, TemporalMonitor<TrajectoryRecord, Double>> func;
    private double temporalHorizon;
    private TreeNode parent;
    private TreeNode firstChild;
    private TreeNode secondChild;

    public TreeNode(TreeNode parent) {
        this.parent = parent;
        this.firstChild = null;
        this.secondChild = null;
        this.start = null;
        this.end = null;
        this.temporalHorizon = 0.0;
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

    public Interval clip(Signal<TrajectoryRecord> signal) {
        double newEnd = Math.min(this.end, signal.size() - 1);
        this.temporalHorizon = newEnd;
        System.out.println("clipped interval: " + this.start + " " + newEnd);
        return new Interval(this.start, newEnd);
    }

    public void setInterval(double s, double e) {
        this.start = s;
        this.end = e;
    }

    public void setTemporalHorizon(double horizon) {
        this.temporalHorizon = horizon;
    }

    public double getTemporalHorizon() {
        return this.temporalHorizon;
    }

}
