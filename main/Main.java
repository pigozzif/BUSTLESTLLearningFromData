
import BuildingBlocks.ProblemClass;
import BuildingBlocks.TreeNode;
import com.google.common.collect.Lists;
import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.evolver.StandardEvolver;
import it.units.malelab.jgea.core.evolver.stopcondition.FitnessEvaluations;
import it.units.malelab.jgea.core.evolver.stopcondition.PerfectFitness;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
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
import java.util.concurrent.RejectedExecutionException;


public class Main extends Worker {

    private static int seed = 0;

    public static void main(String[] args) throws FileNotFoundException {
        String errorMessage = "notFound";
        String random = Args.a(args, "random", errorMessage);
        if (random.equals(errorMessage)) {
            throw new IllegalArgumentException();
        }
        seed = Integer.parseInt(random);
        PrintStream out = new PrintStream(new FileOutputStream(args[2] + ".csv", true), true);
        System.setOut(out);
        new Main(args);
    }

    public Main(String[] args) throws FileNotFoundException {
        super(args);
    }

    public void run() {
        try {
            evolution();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        catch (RejectedExecutionException e) {
            System.out.println("Evolution Terminated With Usual RejectedExecutionException");
        }
    }

    private void evolution() throws IOException, ExecutionException, InterruptedException {
        final GrammarBasedProblem<String, TreeNode, Double> p = new ProblemClass();
        Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new StandardTreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new StandardTreeCrossover<>(12), 0.8d);
        StandardEvolver<Node<String>, TreeNode, Double> evolver = new StandardEvolver(
                    20,
                    new RampedHalfAndHalf<>(0, 12, p.getGrammar()),
                    new ComparableRanker<>(new FitnessComparator<>(Function.identity())),
                    p.getSolutionMapper(),
                    operators,
                    new Tournament<>(5),
                    new Worst<>(),
                    10,
                    true,
                    Lists.newArrayList(new FitnessEvaluations(20), new PerfectFitness<>(p.getFitnessFunction())),
                    1000,
                    false
        );
        Random r = new Random(seed);
        Collection<TreeNode> solutions = evolver.solve(p, r, this.executorService, Listener.onExecutor(listener(new Basic(), new Population(),
                    new BestInfo<>("%6.4f"), new Diversity(), new BestPrinter<>("%s")), this.executorService));
    }

}
