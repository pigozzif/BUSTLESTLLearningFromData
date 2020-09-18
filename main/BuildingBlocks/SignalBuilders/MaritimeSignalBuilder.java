package BuildingBlocks.SignalBuilders;

import BuildingBlocks.TrajectoryRecord;
import eu.quanticol.moonlight.signal.Signal;

import java.io.*;
import java.util.*;


public class MaritimeSignalBuilder implements SignalBuilder<Signal<TrajectoryRecord>> {

    public double[] readVectorFromFile(String filePath) throws IOException {
        BufferedReader reader = this.createReaderFromFile(filePath);
        String[] line;
        try {
            line = reader.readLine().split(",");
        }
        catch (NullPointerException | IOException e) {  // TODO: might pretend that NullPointerException is not there
            return new double[0];
        }
        double[] out = new double[line.length];
        for (int i=0; i < line.length; ++i) out[i] = Double.parseDouble(line[i]);
        reader.close();
        return out;
    }

    public List<Signal<TrajectoryRecord>> parseSignals(String fileName, List<Integer> boolIndexes, List<Integer> doubleIndexes) throws IOException {
        List<Signal<TrajectoryRecord>> signals = new ArrayList<>();
        double[] times = this.readVectorFromFile("./data/navalTimes.csv");
        for (int i=0; i < times.length; ++i) {
            times[i] = i;
        }
        int n = times.length;
        BufferedReader reader = this.createReaderFromFile(fileName);
        reader.readLine();
        double[] varsData;
        boolean[] dummy = new boolean[0];
        int i = 0;
        Signal<TrajectoryRecord> currSignal = new Signal<>();
        while (true) {
            try {
                varsData = Arrays.stream(reader.readLine().split(",")).mapToDouble(Double::parseDouble).toArray();
            }
            catch (NullPointerException | IOException e) {
                break;
            }
            currSignal.add(times[i], new TrajectoryRecord(dummy, varsData));
            ++i;
            if (i == n) {
                signals.add(currSignal);
                currSignal = new Signal<>();
                i = 0;
            }
        }
        reader.close();
        return signals;
    }

}
