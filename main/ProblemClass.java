import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;


public class ProblemClass implements GrammarBasedProblem<String, TemporalMonitor<Pair<Double, Double>, Double>, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, TemporalMonitor<Pair<Double, Double>, Double>> solutionMapper;
    private final Function<List<Node<Element>>, Double> fitnessFunction;

    public ProblemClass() throws IOException {
        grammar = Grammar.fromFile(new File("../grammar.bnf"));
    }

    @Override
    public Grammar<String> getGrammar() {
        return grammar;
    }

    @Override
    public Function<Node<String>, TemporalMonitor<Pair<Double, Double>, Double>> getSolutionMapper() {
        return solutionMapper;
    }

    @Override
    public Function<List<Node<Element>>, Double> getFitnessFunction() {
        return fitnessFunction;
    }

}
