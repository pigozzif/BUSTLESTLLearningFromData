package Expressions;


import Entities.TrajectoryRecord;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;

public interface Expression<T> {

    String EXPRESSION_STRING = "expr";

    String toString();

    T getValue();

    default TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        return null;
    }

}
