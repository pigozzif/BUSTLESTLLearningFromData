package BuildingBlocks;

import eu.quanticol.moonlight.signal.Signal;

import java.io.*;
import java.util.*;

// TODO: still missing to close all those readers
public class MaritimeSignalBuilder implements SignalBuilder<Signal<TrajectoryRecord>>{

    public double[] readVectorFromFile(String filePath) {
        BufferedReader reader;
        try {
            reader = this.createReaderFromFile(filePath);
        }
        catch (IOException e) {
            return new double[0];
        }
        String[] line;
        try {
            line = reader.readLine().split(",");
        }
        catch (NullPointerException | IOException e) {
            return new double[0];
        }
        double[] out = new double[line.length];
        for (int i=0; i < line.length; ++i) out[i] = Double.parseDouble(line[i]);
        return out;
    }

    public List<Signal<TrajectoryRecord>> parseSignals(String fileName, List<Integer> boolIndexes, List<Integer> doubleIndexes) {
        List<Signal<TrajectoryRecord>> signals = new ArrayList<>();
        double[] times;
        times = this.readVectorFromFile("/Users/federicopigozzi/Desktop/Data_Science_and_Scientific_Computing/Thesis/learningformulae/learning/src/resources/data/navalTimes");
        int n = times.length;
        int m = boolIndexes.size() + doubleIndexes.size();
        BufferedReader reader;
        try {
            reader = this.createReaderFromFile(fileName);
        }
        catch (IOException e) {
            return signals;
        }
        String[] line;
        double[] varsData = new double[m];
        boolean[] dummy = new boolean[0];
        while (true) {
            try {
                line = reader.readLine().split(",");
            }
            catch (NullPointerException | IOException e) {
                break;
            }
            Signal<TrajectoryRecord> currSignal = new Signal<>();
            int k = 0;
            for (int i=0; i < n; ++i) {
                for (int j=0; j < m; ++j) {
                    varsData[j] = Double.parseDouble(line[k]);
                }
                currSignal.add(times[k++], new TrajectoryRecord(dummy, varsData));
            }
            signals.add(currSignal);
        }
        return signals;
    }

}
