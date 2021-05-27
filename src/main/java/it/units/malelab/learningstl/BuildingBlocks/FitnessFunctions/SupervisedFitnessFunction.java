package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SupervisedSignalBuilder;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.learningstl.LocalSearch.LocalSearch;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class SupervisedFitnessFunction extends AbstractFitnessFunction {

    private final BiFunction<double[], double[], Double> function = (x, y) -> (x[0] - y[0]) / (Math.abs(x[1] + y[1]));
    private final long numPositive;
    private final long numNegative;

    public SupervisedFitnessFunction(String path, boolean localSearch, Random random) throws IOException {
        super(localSearch);
        this.signalBuilder = new SupervisedSignalBuilder();
        List<Signal<Map<String, Double>>> signals = this.signalBuilder.parseSignals(path);
        this.labels = this.signalBuilder.readVectorFromFile(path + "/labels.csv");
        this.splitSignals(signals, 0.8, random);
        this.numPositive = Arrays.stream(this.labels).filter(x -> x > 0).count();
        this.numNegative = Arrays.stream(this.labels).filter(x -> x < 0).count();
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
        if (this.isLocalSearch) {
            this.optimizeAndUpdateParams(monitor, 1);
        }
        double[] positiveResult = this.computeRobustness(monitor, this.positiveTraining, this.numPositive, false);
        double[] negativeResult = this.computeRobustness(monitor, this.negativeTraining, this.numNegative, false);
        return - this.function.apply(positiveResult, negativeResult);
    }

    public double[] computeRobustness(AbstractTreeNode monitor, List<Signal<Map<String, Double>>> data, long num, boolean isNegative) {
        double[] result = new double[3];
        double robustness;
        for (Signal<Map<String, Double>> signal : data) {
            robustness = this.monitorSignal(signal, monitor, isNegative);
            result[0] += robustness;
            result[1] += robustness * robustness;
        }
        result[1] = this.standardDeviation(result[1], result[0], num);
        result[0] /= num;
        return result;
    }
    // TODO: they use plain old variance
    private double standardDeviation(double partialSumSquared, double partialSum, long num) {
        double mean = partialSum / (num - 1);
        return Math.sqrt((partialSumSquared / (num - 1)) - (mean * mean));
    }

    public void optimizeAndUpdateParams(AbstractTreeNode monitor, int maxIterations) {
        double[] newParams = LocalSearch.optimize(monitor, this, maxIterations);
        monitor.propagateParameters(newParams);
        double[] p1u1 = this.computeRobustness(monitor, this.positiveTraining, numPositive, false);
        double[] p2u2 = this.computeRobustness(monitor, this.negativeTraining, numNegative, false);
        double value;
        if (p1u1[0] > p2u2[0]) {
            value = ((p1u1[0] - p1u1[1]) + (p2u2[0] + p2u2[1])) / 2;
        } else {
            value = ((p2u2[0] - p2u2[1]) + (p1u1[0] + p1u1[1])) / 2;
        }
        int numBounds = monitor.getNumBounds();
        List<String[]> variables = monitor.getVariables();
        for (int i = numBounds; i < newParams.length; i++) {
            if (variables.get(i - numBounds)[1].equals(">")) {
                newParams[i] = Math.max(newParams[i] + value, 0);
            } else {
                newParams[i] = Math.max(newParams[i] - value, 0);
            }
        }
        //double[] score = LocalSearch.score(this, monitor, newParams);
        //System.out.println("score: " + score[0] + " " + score[1]);
        monitor.propagateParameters(newParams);
    }

    @Override
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            double[] value1 = this.computeRobustness(node, this.positiveTraining, this.numPositive, false);
            double[] value2 = this.computeRobustness(node, this.negativeTraining, this.numNegative, false);
            return this.function.apply(value1, value2);};
    }

}
