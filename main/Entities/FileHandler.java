package Entities;

import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FileHandler {

    public static BufferedReader createReaderFromFile(String fileName) throws IOException {
        Path path = Paths.get(".", fileName);
        InputStream in = Files.newInputStream(path);
        return new BufferedReader(new InputStreamReader(in));
    }
    // TODO: fix Long Method
    public static List<Signal<TrajectoryRecord>> parseSignals(BufferedReader reader, String[] boolNames, String[] doubleNames, List<Integer> boolIndexes,
                                                                                             List<Integer> doubleIndexes) {
        List<Signal<TrajectoryRecord>> signals = new ArrayList<>();
        int vehicleIdx = 1;
        boolean isFinished = false;
        String[] line = new String[boolNames.length + doubleNames.length + 1];
        eu.quanticol.moonlight.signal.Signal<TrajectoryRecord> currSignal = new Signal<>();
        while (!isFinished) {
            do {
                try {
                    line = reader.readLine().split(",");
                } catch (IOException e) {
                    isFinished = true;
                    break;
                }
                boolean[] boolVars = new boolean[boolNames.length];
                double[] doubleVars = new double[doubleNames.length];
                for (Integer idx : boolIndexes) boolVars[idx] = Boolean.parseBoolean(line[boolIndexes.get(idx)]);
                for (Integer idx : doubleIndexes) doubleVars[idx] = Double.parseDouble(line[doubleIndexes.get(idx)]);
                currSignal.add(Double.parseDouble(line[22]), new TrajectoryRecord(boolNames, doubleNames, boolVars, doubleVars));
            } while (vehicleIdx == Integer.parseInt(line[21]));
            currSignal.endAt(Double.parseDouble(line[22]));
            signals.add(currSignal);
            currSignal = new Signal<>();
        }
        return signals;
    }

}
