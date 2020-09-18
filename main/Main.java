
import BuildingBlocks.ProblemClass;
import BuildingBlocks.TreeNode;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main extends Worker {

    private static int seed;
    private static PrintStream out;
    private final static String grammarPath = "./grammar_maritime.bnf";
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
        Random r = new Random(seed);
        final ProblemClass p = new ProblemClass(grammarPath, new String[]{}, new String[]{"x1", "x2"}, r); /*"V_vel", "NE_dist", "N_dist", "NW_dist", "W_dist", "SW_dist", "S_dist", "SE_dist", "E_dist", "Vehicle_ID", "Global_Time"};*/ /* "angle", "torque", "speed",*/
        Map<GeneticOperator<Tree<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new GrammarBasedSubtreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new SameRootSubtreeCrossover<>(12), 0.8d);
        StandardWithEnforcedDiversityEvolver<Tree<String>, TreeNode, Double> evolver = new StandardWithEnforcedDiversityEvolver<>(
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
        Collection<TreeNode> solutions = evolver.solve(Misc.cached(p.getFitnessFunction(), 10000), new Iterations(100),
                r, this.executorService, Listener.onExecutor(new PrintStreamListener<>(out, false, 10,
                        ",", ",",  new Basic(), new Population(), new Diversity(), new BestInfo("%5.3f")), this.executorService));
        p.postProcess(solutions);
    }

}
