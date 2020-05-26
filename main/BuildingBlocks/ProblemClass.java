package BuildingBlocks;

import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.NonDeterministicFunction;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.core.function.Function;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.io.File;
import java.io.IOException;


public class ProblemClass implements GrammarBasedProblem<String, TreeNode, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, TreeNode> solutionMapper;
    private final FitnessFunction fitnessFunction;

    public ProblemClass() throws IOException {
        this.grammar = Grammar.fromFile(new File("./grammar.bnf"));
        this.solutionMapper = new STLFormulaMapper();
        this.fitnessFunction = new FitnessFunction("./data/Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data9.csv");
    }

    @Override
    public Grammar<String> getGrammar() {
        return this.grammar;
    }

    @Override
    public Function<Node<String>, TreeNode> getSolutionMapper() {
        return this.solutionMapper;
    }

    @Override
    public NonDeterministicFunction<TreeNode, Double> getFitnessFunction() {
        return this.fitnessFunction;
    }

}
