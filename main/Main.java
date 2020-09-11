
import BuildingBlocks.MaritimeSignalBuilder;
import BuildingBlocks.ProblemClass;
import BuildingBlocks.TreeNode;
import com.google.common.collect.Lists;
import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.evolver.StandardEvolver;
import it.units.malelab.jgea.core.evolver.StandardWithEnforcedDiversity;
import it.units.malelab.jgea.core.evolver.stopcondition.FitnessEvaluations;
import it.units.malelab.jgea.core.evolver.stopcondition.PerfectFitness;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.listener.PrintStreamListener;
import it.units.malelab.jgea.core.listener.collector.*;
import it.units.malelab.jgea.core.operator.GeneticOperator;
import it.units.malelab.jgea.core.ranker.ComparableRanker;
import it.units.malelab.jgea.core.ranker.FitnessComparator;
import it.units.malelab.jgea.core.ranker.selector.Tournament;
import it.units.malelab.jgea.core.ranker.selector.Worst;
import it.units.malelab.jgea.core.util.Args;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.grammarbased.cfggp.RampedHalfAndHalf;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeCrossover;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeMutation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main extends Worker {

    private static int seed;
    private static PrintStream out;
    private final static String grammarPath = "./grammar.bnf";
    private final static String dataPath = "./data/Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data9.csv"; /*"../../Desktop/Data_Science_and_Scientific_Computing/Thesis/TeLEX/tests/udacityData/steering2p.csv");*/
    private final static String outputPath = "output/";

    public static void main(String[] args) throws IOException {
        String errorMessage = "notFound";
        String random = Args.a(args, "random", errorMessage);
        if (random.equals(errorMessage)) {
            throw new IllegalArgumentException("Random Seed not Valid");
        }
        seed = Integer.parseInt(random);
        out = new PrintStream(new FileOutputStream(outputPath + Args.a(args, "output_name", "output")
                + ".csv", true), true);
        new Main(args);
    }

    public Main(String[] args) throws FileNotFoundException {
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
        final GrammarBasedProblem<String, TreeNode, Double> p = new ProblemClass(grammarPath, dataPath);
        Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new StandardTreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new StandardTreeCrossover<>(12), 0.8d);
        StandardWithEnforcedDiversity<Node<String>, TreeNode, Double> evolver = new StandardWithEnforcedDiversity(
        //StandardEvolver<Node<String>, TreeNode, Double> evolver = new StandardEvolver(
                100,
                    500,
                    new RampedHalfAndHalf<>(0, 12, p.getGrammar()),
                    new ComparableRanker<>(new FitnessComparator<>(Function.identity())),
                    p.getSolutionMapper(),
                    operators,
                    new Tournament<>(5),
                    new Worst<>(),
                    500,
                    true,
                    Lists.newArrayList(new FitnessEvaluations(50000), new PerfectFitness<>(p.getFitnessFunction())),
                    0//,
                //false
        );
        Random r = new Random(seed);
        Collection<TreeNode> solutions = evolver.solve(p, r, this.executorService, Listener.onExecutor(
                    new PrintStreamListener(out, false, 0, ",", ",",  new Basic(), new Population(),
                    new BestInfo<>("%6.4f"), new Diversity(), new BestPrinter<>("%s")), this.executorService));
        solutions.forEach(System.out::println);
    }

}
