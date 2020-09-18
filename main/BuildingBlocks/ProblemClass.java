package BuildingBlocks;

import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.representation.grammar.Grammar;
import it.units.malelab.jgea.representation.grammar.GrammarBasedProblem;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.function.Function;


public class ProblemClass implements GrammarBasedProblem<String, TreeNode, Double> {

    private final Grammar<String> grammar;
    private final Function<Tree<String>, TreeNode> solutionMapper;
    private final AbstractFitnessFunction<Signal<TrajectoryRecord>> fitnessFunction;
    // private static String[] booleanNames = new String[]{};
    // private static String[] numericNames = new String[]{};

    public ProblemClass(String grammarPath, String[] bool, String[] numeric, Random random) throws IOException {
        this.grammar = Grammar.fromFile(new File(grammarPath));
        this.solutionMapper = new STLFormulaMapper();
        this.fitnessFunction = new MaritimeFitnessFunction(random);
        /*if (bool.length != 0) {
            booleanNames = new String[bool.length];
            System.arraycopy(bool, 0, booleanNames, 0, bool.length);
        }
        if (numeric.length != 0) {
            numericNames = new String[numeric.length];
            System.arraycopy(numeric, 0, numericNames, 0, numeric.length);
        }*/
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
    public Function<TreeNode, Double> getFitnessFunction() {
        return this.fitnessFunction;
    }

    public static String[] retrieveBooleanNames() {
        return new String[]{};
    }

    public static String[] retrieveNumericalNames() {
        return new String[]{"x1", "x2"};
    }

    public void postProcess(Collection<TreeNode> solutions) {
        TreeNode bestFormula = null;
        double best = - Double.MIN_VALUE;
        double result;
        for (TreeNode solution : solutions) {
            result = 0.0;
            for (Signal<TrajectoryRecord> signal : this.fitnessFunction.getPositiveTraining()) {
                result += this.fitnessFunction.monitorSignal(signal, solution);
            }
            for (Signal<TrajectoryRecord> signal : this.fitnessFunction.getNegativeTraining()) {
                result += this.fitnessFunction.monitorSignal(signal, solution);
            }
            if (result > best) {
                best = result;
                bestFormula = solution;
            }
        }
        double count = 0.0;
        for (Signal<TrajectoryRecord> signal : this.fitnessFunction.getPositiveTest()) {
            result = this.fitnessFunction.monitorSignal(signal, bestFormula);
            if (result > 0.0) {
                ++count;
            }
        }
        System.out.println("Positive Test Misclassification Rate: " + (1.0 - count / this.fitnessFunction.getPositiveTest().size()));
        count = 0.0;
        for (Signal<TrajectoryRecord> signal : this.fitnessFunction.getNegativeTest()) {
            result = this.fitnessFunction.monitorSignal(signal, bestFormula);
            if (result < 0.0) {
                ++count;
            }
        }
        System.out.println("Negative Test Misclassification Rate: " + (1.0 - count / this.fitnessFunction.getNegativeTest().size()));
    }

}
