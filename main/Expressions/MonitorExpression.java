package Expressions;

import Entities.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public interface MonitorExpression {

    String toString();

    TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings);

}
