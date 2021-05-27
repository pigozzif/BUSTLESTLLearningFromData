package it.units.malelab.learningstl;

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
    private static String modality;
    private AbstractFitnessFunction f;

    public static void main(String[] args) throws IOException {
        String errorMessage = "notFound";
        String random = Args.a(args, "seed", errorMessage);
        if (random.equals(errorMessage)) {
            throw new IllegalArgumentException("Random Seed not Valid");
        }
        seed = Integer.parseInt(random);
        grammarPath = Args.a(args, "grammar", null);
        outputPath = Args.a(args, "output", null) + seed + ".csv";
        out = new PrintStream(new FileOutputStream(outputPath, true), true);
        inputPath = Args.a(args, "input", null);
        isLocalSearch = Boolean.parseBoolean(Args.a(args, "local_search", null));
        modality = Args.a(args, "mod", null);
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
            this.f = new SupervisedFitnessFunction(inputPath, isLocalSearch, r);
        }
        else if (modality.equals("anomaly")) {
            this.f = new AnomalyDetectionFitnessFunction(0.1, inputPath, isLocalSearch, r);
        }
        else {
            throw new IllegalArgumentException("Unknown modality: " + modality);
        }
        STLFormulaMapper m = new STLFormulaMapper();
        final ProblemClass p = new ProblemClass(grammarPath, this.f, m);
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
                r, this.executorService, Listener.onExecutor(new PrintStreamListener<>(out, false, 0,
                        ",", ",",  new Basic(), new Population(), new Diversity(), new BestInfo("%5.3f"), (DataCollector<Tree<String>, AbstractTreeNode, Double>) this::saveData), this.executorService));
        AbstractTreeNode bestFormula = solutions.iterator().next();
 //       if (modality.equals("supervised")) {
 //           ((SupervisedFitnessFunction) f).optimizeAndUpdateParams(bestFormula, 1);
 //       }
        //Files.write(Paths.get(outputPath), (bestFormula.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
        postProcess(bestFormula, this.f);
    }

    public static double[] postProcess(AbstractTreeNode bestFormula, AbstractFitnessFunction f) {
        double result;
        double count = 0.0;
        double robustness = 0.0;
        double[] out = new double[2];
        for (Signal<Map<String, Double>> signal : f.getPositiveTest()) {
            result = f.monitorSignal(signal, bestFormula, false);
            if (result <= 0.0) {
                ++count;
            }
            robustness += result;
        }
        out[0] = count / f.getPositiveTest().size();
        //Files.write(Paths.get(outputPath), ("Positive Test Misclassification Rate: " + count / f.getPositiveTest().size() + "\n").getBytes(), StandardOpenOption.APPEND);
        //Files.write(Paths.get(outputPath), ("Positive Test Mean Robustness: " + robustness / f.getPositiveTest().size() + "\n").getBytes(), StandardOpenOption.APPEND);
        count = 0.0;
        for (Signal<Map<String, Double>> signal : f.getNegativeTest()) {
            result = f.monitorSignal(signal, bestFormula, true);
            if (result <= 0.0) {
                ++count;
            }
            robustness += f.monitorSignal(signal, bestFormula, false);
        }
        out[1] = count / f.getNegativeTest().size();
        //Files.write(Paths.get(outputPath), ("Negative Test Misclassification Rate: " + count / f.getNegativeTest().size() + "\n").getBytes(), StandardOpenOption.APPEND);
        //Files.write(Paths.get(outputPath), ("Negative Test Mean Robustness: " + robustness / f.getNegativeTest().size() + "\n").getBytes(), StandardOpenOption.APPEND);
        return out;
    }

    private List<Item> saveData(Event<? extends Tree<String>, ? extends AbstractTreeNode, ? extends Double> event) {
        AbstractTreeNode best = event.getOrderedPopulation().firsts().iterator().next().getSolution();
        double[] perf = postProcess(best, this.f);
        return new ArrayList<>() {{ add(new Item("positive.miss", perf[0], "%1.2"));
        add(new Item("negative.miss", perf[1], "%1.2")); add(new Item("local", (isLocalSearch) ? 1 : 0, "%1d")); }};
    }

}
