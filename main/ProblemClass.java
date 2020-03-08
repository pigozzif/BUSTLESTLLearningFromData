import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.core.function.Function;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.io.File;
import java.io.IOException;
//import java.util.function.Function;


public class ProblemClass implements GrammarBasedProblem<String, TemporalMonitor<Record, Double>, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, TemporalMonitor<Record, Double>> solutionMapper;
    private final Function<, Double> fitnessFunction;

    public ProblemClass() throws IOException {
        grammar = Grammar.fromFile(new File("../grammar.bnf"));
        solutionMapper = new STLFormulaMapper();
    }

    @Override
    public Grammar<String> getGrammar() {
        return grammar;
    }

    @Override
    public Function<Node<String>, TemporalMonitor<Record, Double>> getSolutionMapper() {
        return solutionMapper;
    }

    @Override
    public Function<, Double> getFitnessFunction() {
        return fitnessFunction;
    }

}
