package Entities;

import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.core.function.Function;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.io.File;
import java.io.IOException;


public class ProblemClass implements GrammarBasedProblem<String, TemporalMonitor<TrajectoryRecord, Double>, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> solutionMapper;
    private final Function<TemporalMonitor<TrajectoryRecord, Double>, Double> fitnessFunction;

    public ProblemClass() throws IOException {
        grammar = Grammar.fromFile(new File("../grammar.bnf"));
        solutionMapper = new STLFormulaMapper();
    }

    @Override
    public Grammar<String> getGrammar() {
        return grammar;
    }

    @Override
    public Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> getSolutionMapper() {
        return solutionMapper;
    }

    @Override
    public Function<TemporalMonitor<TrajectoryRecord, Double>, Double> getFitnessFunction() {
        return fitnessFunction;
    }

}
