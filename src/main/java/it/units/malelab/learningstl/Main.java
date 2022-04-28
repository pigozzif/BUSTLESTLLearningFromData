package it.units.malelab.learningstl;

import it.units.malelab.jgea.core.evolver.Evolver;
import it.units.malelab.jgea.core.listener.Event;
import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.AnomalyDetectionFitnessFunction;
import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.SupervisedFitnessFunction;
import it.units.malelab.learningstl.BuildingBlocks.ProblemClass;
import it.units.malelab.learningstl.BuildingBlocks.STLFormulaMapper;
import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
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
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main extends Worker {

    private static int seed;
    private static PrintStream out;
    private static String grammarPath;
    private static String inputPath;
    private static boolean isLocalSearch;
    private static String modality;
    private static AbstractFitnessFunction f;
    private static double alpha;
    private static final List<Double> eps = new ArrayList<>() {{ add(0.001); add(0.0025); add(0.005); add(0.01); add(0.02); add(0.03); add(0.04); add(0.05); add(0.1); add(0.15); add(0.2); add(0.25); add(0.3); add(0.35); add(0.4); add(0.45); add(0.5); add(0.55); add(0.6); add(0.65); add(0.7); add(0.75); add(0.8); add(0.85); add(0.9); add(0.95); add(1.0); }};

    public static void main(String[] args) throws IOException {
        String errorMessage = "notFound";
        String random = Args.a(args, "seed", errorMessage);
        if (random.equals(errorMessage)) {
            throw new IllegalArgumentException("Random Seed Not Valid");
        }
        seed = Integer.parseInt(random);
        String dataset = Args.a(args, "dataset", null);
        inputPath = String.join("/", "data", dataset);
        isLocalSearch = Boolean.parseBoolean(Args.a(args, "local", null));
        modality = Args.a(args, "mod", null);
        grammarPath = "./grammars/grammar";
        if (dataset.equals("maritime")) {
            grammarPath += "_maritime";
        }
        if (isLocalSearch) {
            grammarPath += "_local_search";
        }
        grammarPath += ".bnf";
        if (modality.equals("anomaly")) {
            alpha = Args.d(Args.a(args, "alpha", null).replace(",", "."));
        }
        String outputPath = Args.a(args, "output", null) + seed + ".csv";
        out = new PrintStream(new FileOutputStream(outputPath, true), true);
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
        if (modality.equals("supervised")) {
            f = new SupervisedFitnessFunction(inputPath, isLocalSearch, r);
        }
        else if (modality.equals("anomaly")) {
            f = new AnomalyDetectionFitnessFunction(alpha, inputPath, isLocalSearch, r);
        }
        else {
            throw new IllegalArgumentException("Unknown modality: " + modality);
        }
        STLFormulaMapper m = new STLFormulaMapper();
        final ProblemClass p = new ProblemClass(grammarPath, f, m);
        Map<GeneticOperator<Tree<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new GrammarBasedSubtreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new SameRootSubtreeCrossover<>(12), 0.8d);
        Evolver<Tree<String>, AbstractTreeNode, Double> evolver = new StandardWithEnforcedDiversityEvolver<>(
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
                r, this.executorService, Listener.onExecutor(new PrintStreamListener<>(out, false, 0,
                        ";", ";",  new Basic(), new Population(), new Diversity(), new BestInfo("%5.3f"), (DataCollector<Tree<String>, AbstractTreeNode, Double>) this::saveData), this.executorService));
    }

    public static double[] postProcess(AbstractTreeNode bestFormula, AbstractFitnessFunction f, double epsilon) {
        double result;
        double count = 0.0;
        double robustness = 0.0;
        double[] out = new double[4];
        for (Signal<Map<String, Double>> signal : f.getPositiveTest()) {
            result = f.monitorSignal(signal, bestFormula, false);
            if (result < - epsilon) {
                ++count;
            }
            robustness += result;
        }
        out[0] = count / f.getPositiveTest().size();
        out[1] = robustness / f.getPositiveTest().size();
        count = 0.0;
        for (Signal<Map<String, Double>> signal : f.getNegativeTest()) {
            result = f.monitorSignal(signal, bestFormula, false);
            if (result >= - epsilon) {
                ++count;
            }
            robustness += f.monitorSignal(signal, bestFormula, false);
        }
        out[2] = count / f.getNegativeTest().size();
        out[3] = robustness / f.getNegativeTest().size();
        return out;
    }

    private List<Item> saveData(Event<? extends Tree<String>, ? extends AbstractTreeNode, ? extends Double> event) {
        AbstractTreeNode best = event.getOrderedPopulation().firsts().iterator().next().getSolution();
        List<Item> out = new ArrayList<>();
        out.add(new Item("alpha", alpha, "%1d"));
        out.add(new Item("local", (isLocalSearch) ? 1 : 0, "%1d"));
        for (double epsilon : eps) {
            double[] perf = postProcess(best, f, epsilon);
            out.add(new Item(epsilon + ".positive.miss", perf[0], "%1.2"));
            out.add(new Item(epsilon + ".negative.miss", perf[2], "%1.2"));
            out.add(new Item(epsilon + ".positive.rob", perf[1], "%4.8f"));
            out.add(new Item(epsilon + ".negative.rob", perf[3], "%4.8f"));
            epsilon = - epsilon;
            perf = postProcess(best, f, epsilon);
            out.add(new Item(epsilon + ".positive.miss", perf[0], "%1.2"));
            out.add(new Item(epsilon + ".negative.miss", perf[2], "%1.2"));
            out.add(new Item(epsilon + ".positive.rob", perf[1], "%4.8f"));
            out.add(new Item(epsilon + ".negative.rob", perf[3], "%4.8f"));
        }
        out.add(new Item("serialized", best.toString(), "%s"));
        return out;
    }

}
