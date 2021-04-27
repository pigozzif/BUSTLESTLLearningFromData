
import BuildingBlocks.STLFormulaMapper;
import TreeNodes.AbstractTreeNode;
import BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import BuildingBlocks.FitnessFunctions.SupervisedFitnessFunction;
import BuildingBlocks.ProblemClass;
//import BuildingBlocks.Map<String, Double>;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Individual;
import it.units.malelab.jgea.core.evolver.StandardWithEnforcedDiversityEvolver;
import it.units.malelab.jgea.core.evolver.stopcondition.Iterations;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.listener.PrintStreamListener;
import it.units.malelab.jgea.core.listener.collector.*;
import it.units.malelab.jgea.core.operator.GeneticOperator;
import it.units.malelab.jgea.core.order.PartialComparator;
import it.units.malelab.jgea.core.selector.Tournament;
import it.units.malelab.jgea.core.selector.Worst;
import it.units.malelab.jgea.core.util.Args;
import it.units.malelab.jgea.core.util.Misc;
import it.units.malelab.jgea.representation.grammar.cfggp.GrammarBasedSubtreeMutation;
import it.units.malelab.jgea.representation.grammar.cfggp.GrammarRampedHalfAndHalf;
import it.units.malelab.jgea.representation.tree.SameRootSubtreeCrossover;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main extends Worker {

    private static int seed;
    private static PrintStream out;
    private static String grammarPath;
    private static String outputPath;
    private static String inputPath;
    private static boolean isLocalSearch;

    public static void main(String[] args) throws IOException {
        String errorMessage = "notFound";
        String random = Args.a(args, "seed", errorMessage);
        if (random.equals(errorMessage)) {
            throw new IllegalArgumentException("Random Seed not Valid");
        }
        seed = Integer.parseInt(random);
        grammarPath = Args.a(args, "grammar", null);
        outputPath = Args.a(args, "output", null) + ".csv";
        out = new PrintStream(new FileOutputStream(outputPath, true), true);
        inputPath = Args.a(args, "input", null);
        isLocalSearch = Boolean.parseBoolean(Args.a(args, "local_search", null));
        new Main(args);
    }

    public Main(String[] args) {
        super(args);
    }

    @Override
    public void run() {
        try {
            evolution();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void evolution() throws IOException, ExecutionException, InterruptedException {
        Random r = new Random(seed);
        AbstractFitnessFunction<Signal<Map<String, Double>>> f = new /*I80FitnessFunction();*/SupervisedFitnessFunction(inputPath, isLocalSearch, r);
        final ProblemClass<Signal<Map<String, Double>>> p = new ProblemClass<>(grammarPath, f, new STLFormulaMapper(isLocalSearch));
        Map<GeneticOperator<Tree<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new GrammarBasedSubtreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new SameRootSubtreeCrossover<>(12), 0.8d);
        StandardWithEnforcedDiversityEvolver<Tree<String>, AbstractTreeNode, Double> evolver = new StandardWithEnforcedDiversityEvolver<>(
                    p.getSolutionMapper(),
                    new GrammarRampedHalfAndHalf<>(0, 12, p.getGrammar()),
                    PartialComparator.from(Double.class).comparing(Individual::getFitness),
                    500,
                    operators,
                    new Tournament(5),
                    new Worst(),
                 500,
                    true,
                100
        );
        Collection<AbstractTreeNode> solutions = evolver.solve(Misc.cached(p.getFitnessFunction(), 10), new Iterations(50),
                r, this.executorService, Listener.onExecutor(new PrintStreamListener<>(out, false, 10,
                        ",", ",",  new Basic(), new Population(), new Diversity(), new BestInfo("%5.3f")), this.executorService));
        AbstractTreeNode bestFormula = solutions.iterator().next();
        Files.write(Paths.get(outputPath), (bestFormula.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
        this.postProcess(solutions, p.getFitnessFunction());
    }
    // grammar only const terminal for optimizable variable, we evaluate the fitness of such a tree, we evaluate an expression template the best best fitness for that template,
    // fitness evaluates the template and internally tries the previous fitness, something that takes a tree, data, and gives a number, internally invokes the other
    // with the optimized values, Darwinian not Lamrckian because values are not inherited, for us stateless, might not be bad starting from previous knowledge, base case, everytime
    // I get a template I can try to copy from similar templates and restart optimization, if looking for optima complicated because of context
    public void postProcess(Collection<AbstractTreeNode> solutions, AbstractFitnessFunction<Signal<Map<String, Double>>> f) throws IOException {
        AbstractTreeNode bestFormula = solutions.iterator().next();
        double result;
        double count = 0.0;
        for (Signal<Map<String, Double>> signal : f.getPositiveTest()) {
            result = f.monitorSignal(signal, bestFormula, false);
            if (result > 0.0) {
                ++count;
            }
        }
        Files.write(Paths.get(outputPath), ("Positive Test Misclassification Rate: " + (1.0 - count / f.getPositiveTest().size()) + "\n").getBytes(), StandardOpenOption.APPEND);
        count = 0.0;
        for (Signal<Map<String, Double>> signal : f.getNegativeTest()) {
            result = f.monitorSignal(signal, bestFormula, true);
            if (result > 0.0) {
                ++count;
            }
        }
        Files.write(Paths.get(outputPath), ("Negative Test Misclassification Rate: " + (1.0 - count / f.getNegativeTest().size()) + "\n").getBytes(), StandardOpenOption.APPEND);
    }

}
