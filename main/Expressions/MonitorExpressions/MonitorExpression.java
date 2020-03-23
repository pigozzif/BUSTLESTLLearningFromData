package Expressions.MonitorExpressions;

import BuildingBlocks.TrajectoryRecord;
import Expressions.Expression;
import it.units.malelab.jgea.core.Node;
import core.src.main.java.eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public interface MonitorExpression extends Expression {

    //String toString();

    TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings);

}
