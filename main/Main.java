import BuildingBlocks.FitnessFunction;
import BuildingBlocks.ProblemClass;
import BuildingBlocks.TrajectoryRecord;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.operator.GeneticOperator;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeCrossover;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeMutation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void evolution() throws IOException {
            final GrammarBasedProblem<String, TemporalMonitor<TrajectoryRecord, Double>, Double> p = new ProblemClass();
            Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
            operators.put(new StandardTreeMutation<>(12, p.getGrammar()), 0.2d);
            operators.put(new StandardTreeCrossover<>(12), 0.8d);
        }
}
