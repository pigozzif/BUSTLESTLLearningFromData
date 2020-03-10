package BuildingBlocks;

import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.NonDeterministicFunction;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.core.function.Function;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.io.File;
import java.io.IOException;


public class ProblemClass implements GrammarBasedProblem<String, TemporalMonitor<TrajectoryRecord, Double>, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> solutionMapper;
    private final FitnessFunction fitnessFunction;

    public ProblemClass() throws IOException {
        grammar = Grammar.fromFile(new File("../grammar.bnf"));
        solutionMapper = new STLFormulaMapper();
        fitnessFunction = new FitnessFunction("Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data6.csv");
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
    public NonDeterministicFunction<TemporalMonitor<TrajectoryRecord, Double>, Double> getFitnessFunction() {
        return fitnessFunction;
    }

}
