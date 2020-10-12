package BuildingBlocks;

import BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import TreeNodes.AbstractTreeNode;
import it.units.malelab.jgea.representation.grammar.Grammar;
import it.units.malelab.jgea.representation.grammar.GrammarBasedProblem;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;


public class ProblemClass<T> implements GrammarBasedProblem<String, AbstractTreeNode, Double> {

    private final Grammar<String> grammar;
    private Function<Tree<String>, AbstractTreeNode> solutionMapper;
    private AbstractFitnessFunction<T> fitnessFunction;
    private static String[] numericalNames;
    private static String[] booleanNames;
    public static boolean isLocalSearch;

    public ProblemClass(String grammarPath, boolean toOptimize) throws IOException {
        this.grammar = Grammar.fromFile(new File(grammarPath));
        try {
            booleanNames = this.grammar.getRules().get("<bool_var>").stream().flatMap(Collection::stream).toArray(String[]::new);
        }
        catch (NullPointerException e) {
            booleanNames = new String[0];
        }
        try {
            numericalNames = this.grammar.getRules().get("<num_var>").stream().flatMap(Collection::stream).toArray(String[]::new);
        }
        catch (NullPointerException e) {
            numericalNames = new String[0];
        }
        isLocalSearch = toOptimize;
    }

    public void setFitnessFunction(AbstractFitnessFunction<T> fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        this.setSolutionMapper();
    }

    public void setSolutionMapper() {
        this.solutionMapper = new STLFormulaMapper();
    }

    @Override
    public Grammar<String> getGrammar() {
        return this.grammar;
    }

    @Override
    public Function<Tree<String>, AbstractTreeNode> getSolutionMapper() {
        return this.solutionMapper;
    }

    @Override
    public AbstractFitnessFunction<T> getFitnessFunction() {
        return this.fitnessFunction;
    }

    public static String[] retrieveBooleanNames() {
        return booleanNames;
    }

    public static String[] retrieveNumericalNames() {
        return numericalNames;
    }

}
