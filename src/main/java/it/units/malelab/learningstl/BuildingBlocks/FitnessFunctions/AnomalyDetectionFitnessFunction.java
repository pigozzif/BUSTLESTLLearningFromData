package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SupervisedSignalBuilder;
import it.units.malelab.learningstl.LocalSearch.LocalSearch;
import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class AnomalyDetectionFitnessFunction extends AbstractFitnessFunction {

    private final double alpha;

    public AnomalyDetectionFitnessFunction(double a, String path, boolean localSearch, Random random) throws IOException {
        super(localSearch);
        this.alpha = a;
        this.signalBuilder = new SupervisedSignalBuilder();
        List<Signal<Map<String, Double>>> signals = this.signalBuilder.parseSignals(path);
        this.labels = this.signalBuilder.readVectorFromFile(path + "/labels.csv");
        this.splitSignals(signals, 0.8, random);
        this.negativeTest.addAll(this.negativeTraining);
        this.negativeTraining.clear();
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
        if (this.isLocalSearch) {
            double[] newParams = LocalSearch.optimize(monitor, this, 15);
            monitor.propagateParameters(newParams);
        }
        return - this.computeFitness(monitor);
    }

    private double computeFitness(AbstractTreeNode monitor) {
        double robustness = 0.0;
        double average = 0.0;
        for (Signal<Map<String, Double>> s : this.positiveTraining) {
            double result = this.monitorSignal(s, monitor, false);
            if (result <= 0.0) {
                ++average;
            }
            robustness += Math.abs(result);
        }
        average /= this.positiveTraining.size();
        robustness /= this.positiveTraining.size();
        return - (robustness + this.alpha * average);
    }

    @Override
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            return this.computeFitness(node);};
    }

    @Override
    public List<Signal<Map<String, Double>>> getPositiveTest() {
        return this.positiveTest;
    }

    @Override
    public List<Signal<Map<String, Double>>> getNegativeTest() {
        return this.negativeTest;
    }

}
