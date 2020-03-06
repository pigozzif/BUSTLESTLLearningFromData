import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.grammarbased.Grammar;


public class ProblemClass implements GrammarBasedProblem<String, List<Node<Element>>, Double> {

    private final Grammar<String> grammar;
    private final Function<Node<String>, List<Node<Element>>> solutionMapper;
    private final Function<List<Node<Element>>, Double> fitnessFunction;

    public ProblemClass() {}

    @Override
    public Grammar<String> getGrammar() {
        return grammar;
    }

    @Override
    public Function<Node<String>, List<Node<Element>>> getSolutionMapper() {
        return solutionMapper;
    }

    @Override
    public Function<List<Node<Element>>, Double> getFitnessFunction() {
        return fitnessFunction;
    }

}
