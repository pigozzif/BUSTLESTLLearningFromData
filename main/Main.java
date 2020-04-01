import BuildingBlocks.FitnessFunction;
import BuildingBlocks.ProblemClass;
import BuildingBlocks.TrajectoryRecord;
import com.google.common.collect.Lists;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
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
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.grammarbased.cfggp.RampedHalfAndHalf;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeCrossover;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeMutation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class Main extends Worker {

    public static void main(String[] args) throws FileNotFoundException {
        new Main(args);
    }

    public Main(String[] args) throws FileNotFoundException {
        super(args);
        run();
            /*try {
                FitnessFunction func = new FitnessFunction("./data/Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data6.csv");
            }
            catch (IOException e) {
                System.out.println("An IOException has occured");
                System.out.println(e.getMessage());
            }*/
    }

    public void run() {
        try {
            evolution();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void evolution() throws IOException, ExecutionException, InterruptedException {
        final GrammarBasedProblem<String, TemporalMonitor<TrajectoryRecord, Double>, Double> p = new ProblemClass();
        Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new StandardTreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new StandardTreeCrossover<>(12), 0.8d);
        StandardEvolver<Node<String>, TemporalMonitor<TrajectoryRecord, Double>, Double> evolver = new StandardEvolver(
                    100,
                    new RampedHalfAndHalf<>(3, 12, p.getGrammar()),
                    new ComparableRanker<>(new FitnessComparator<>(Function.identity())),
                    p.getSolutionMapper(),
                    operators,
                    new Tournament<>(3),
                    new Worst<>(),
                    500,
                    true,
                    Lists.newArrayList(new FitnessEvaluations(1000), new PerfectFitness<>(p.getFitnessFunction())),
                    0,
                    false
        );
        Random r = new Random(1);
        evolver.solve(p, r, executorService, Listener.onExecutor(listener(new Basic(), new Population(),
                    new BestInfo<>("%6.4f"), new Diversity(), new BestPrinter<>("%s")), executorService));
    }

}
