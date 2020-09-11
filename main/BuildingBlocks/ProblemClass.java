package BuildingBlocks;

import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;

import java.io.File;
import java.io.IOException;


public class ProblemClass implements GrammarBasedProblem<String, TreeNode, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, TreeNode> solutionMapper;
    private final FitnessFunction fitnessFunction;

    public ProblemClass(String grammarPath, String dataPath) throws IOException {
        this.grammar = Grammar.fromFile(new File(grammarPath));
        this.solutionMapper = new STLFormulaMapper();
        this.fitnessFunction = new FitnessFunction(dataPath);
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
    public Function<TreeNode, Double> getFitnessFunction() {
        return this.fitnessFunction;
    }

}
