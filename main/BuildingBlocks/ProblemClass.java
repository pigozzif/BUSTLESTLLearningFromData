package BuildingBlocks;

import BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import BuildingBlocks.FitnessFunctions.MaritimeFitnessFunction;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.representation.grammar.Grammar;
import it.units.malelab.jgea.representation.grammar.GrammarBasedProblem;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

// TODO: generify this class
public class ProblemClass implements GrammarBasedProblem<String, TreeNode, Double> {

    private final Grammar<String> grammar;
    private final Function<Tree<String>, TreeNode> solutionMapper;
    private final AbstractFitnessFunction<Signal<TrajectoryRecord>> fitnessFunction;

    public ProblemClass(String grammarPath, Random random) throws IOException {
        this.grammar = Grammar.fromFile(new File(grammarPath));
        this.solutionMapper = new STLFormulaMapper();
        this.fitnessFunction = new MaritimeFitnessFunction(random);
    }

    @Override
    public Grammar<String> getGrammar() {
        return this.grammar;
    }

    @Override
    public Function<Tree<String>, TreeNode> getSolutionMapper() {
        return this.solutionMapper;
    }

    @Override
    public AbstractFitnessFunction<Signal<TrajectoryRecord>> getFitnessFunction() {
        return this.fitnessFunction;
    }

}
