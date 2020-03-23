import BuildingBlocks.FitnessFunction;
import it.units.malelab.jgea.Worker;

import java.io.FileNotFoundException;
import java.io.IOException;


public class Main extends Worker {

    public static void main(String[] args) throws FileNotFoundException {
            new Main(args);
        }

        public Main(String[] args) throws FileNotFoundException {
            super(args);
            try {
                FitnessFunction func = new FitnessFunction("./data/Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data6.csv");
            }
            catch (IOException e) {
                System.out.println("An IOException has occured");
                System.out.println(e.getMessage());
            }
        }

        public void run() { }
}
