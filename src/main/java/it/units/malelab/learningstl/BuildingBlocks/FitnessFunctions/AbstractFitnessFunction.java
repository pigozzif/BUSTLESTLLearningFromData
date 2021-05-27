package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SignalBuilder;
import eu.quanticol.moonlight.signal.Signal;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

// TODO: generics no longer necessary
public abstract class AbstractFitnessFunction implements Function<AbstractTreeNode, Double> {

    public final static double PENALTY_VALUE = 1.0;
    protected SignalBuilder<Signal<Map<String, Double>>> signalBuilder;
    protected final boolean isLocalSearch;
    protected final List<Signal<Map<String, Double>>> positiveTraining = new ArrayList<>();
    protected final List<Signal<Map<String, Double>>> positiveTest = new ArrayList<>();
    protected final List<Signal<Map<String, Double>>> negativeTraining = new ArrayList<>();
    protected final List<Signal<Map<String, Double>>> negativeTest = new ArrayList<>();
    protected double[] labels;

    public AbstractFitnessFunction(boolean localSearch) {
        this.isLocalSearch = localSearch;
    }

    public SignalBuilder<Signal<Map<String, Double>>> getSignalBuilder() {return this.signalBuilder;}

    public abstract BiFunction<AbstractTreeNode, double[], Double> getObjective();

    public List<Signal<Map<String, Double>>> getPositiveTraining() {
        return this.positiveTraining;
    }

    public List<Signal<Map<String, Double>>> getNegativeTraining() {
        return this.negativeTraining;
    }

    public List<Signal<Map<String, Double>>> getPositiveTest() {
        return this.positiveTest;
    }

    public List<Signal<Map<String, Double>>> getNegativeTest() {
        return this.negativeTest;
    }

    public double monitorSignal(Signal<Map<String, Double>> signal, AbstractTreeNode solution, boolean isNegative) {
        if (signal.size() <= solution.getNecessaryLength()) {
            return - PENALTY_VALUE;
        }
        double temp = solution.getOperator().apply(signal).monitor(signal).valueAt(signal.start());
        return (isNegative) ? - temp : temp;
    }

    protected void splitSignals(List<Signal<Map<String, Double>>> signals, double fold, Random random) {
        List<Integer> positiveIndexes = new ArrayList<>();
        List<Integer> negativeIndexes = new ArrayList<>();
        for (int i=0; i < this.labels.length; ++i) {
            if (this.labels[i] < 0) {
                negativeIndexes.add(i);
            } else {
                positiveIndexes.add(i);
            }
        }
        Collections.shuffle(positiveIndexes, random);
        Collections.shuffle(negativeIndexes, random);
        int posFold = (int) (positiveIndexes.size() * fold);
        int negFold = (int) (negativeIndexes.size() * fold);
        for (int i=0; i < posFold; ++i) {
            this.positiveTraining.add(signals.get(positiveIndexes.get(i)));
        }
        for (int i=posFold; i < positiveIndexes.size(); ++i) {
            this.positiveTest.add(signals.get(positiveIndexes.get(i)));
        }
        for (int i=0; i < negFold; ++i) {
            this.negativeTraining.add(signals.get(negativeIndexes.get(i)));
        }
        for (int i=negFold; i < negativeIndexes.size(); ++i) {
            this.negativeTest.add(signals.get(negativeIndexes.get(i)));
        }
    }

}
