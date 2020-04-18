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
    private double necessaryLength;
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

    public double clip(Signal<TrajectoryRecord> signal) throws ExceptionInInitializerError {
        if (this.firstChild == null) {
            return 0.0;
        }
        else if (this.start == null) {
            double max = this.firstChild.clip(signal);
            if (this.secondChild != null) {
                double temp = this.secondChild.clip(signal);
                if (temp > max) {
                    max = temp;
                }
            }
            return max;
        }
        this.tempStart = this.start;
        double tempNecessaryLength = this.firstChild.clip(signal); // does not work with until
        this.tempEnd = Math.min(this.end, signal.size() - 1 - tempNecessaryLength);
        if (this.tempEnd <= this.tempStart) {
            this.tempStart = Math.max(0.0, this.tempStart - this.end + this.tempEnd);
        }
        if (this.tempEnd <= 0 && this.tempStart <= 0) {
            throw new ExceptionInInitializerError();
        }
        return this.tempEnd + tempNecessaryLength;
        /*double newStart = this.start;
        double newEnd = Math.min(this.end, signal.size() - 1 - this.necessaryLength + this.end);
        if (newEnd <= start) {
            newStart = Math.max(0.0, newStart - this.end + newEnd);
        }
        if (signal.size() <= this.necessaryLength - Math.abs(this.end - newEnd)) {
            throw new ExceptionInInitializerError();
        }
        System.out.println("clipped interval: " + newStart + " " + newEnd + " with temporal horizon: " + this.necessaryLength + " and " + signal + " of size: " + signal.size());
        return new Interval(newStart, newEnd);*/
    }

    public Interval createInterval() {
        return new Interval(this.tempStart, this.tempEnd);
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

}
