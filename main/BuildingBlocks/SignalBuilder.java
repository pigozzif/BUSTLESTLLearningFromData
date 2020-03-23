package BuildingBlocks;

import core.src.main.java.eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class SignalBuilder {

    public static BufferedReader createReaderFromFile(String fileName) throws IOException {
        Path path = Paths.get(".", fileName);
        InputStream in = Files.newInputStream(path);
        return new BufferedReader(new InputStreamReader(in));
    }
    // TODO: maybe fix Long Method
    public static List<Signal<TrajectoryRecord>> parseSignals(BufferedReader reader, String[] boolNames,
                                                              String[] doubleNames, List<Integer> boolIndexes,
                                                              List<Integer> doubleIndexes) {
        List<Signal<TrajectoryRecord>> signals = new ArrayList<>();
        int vehicleIdx = 1;
        boolean isFinished = false;
        String[] line = new String[boolNames.length + doubleNames.length + 1];
        List<TrajectoryRecord> trajectory = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        boolean[] boolVars = new boolean[boolNames.length];
        double[] doubleVars = new double[doubleNames.length];
        while (!isFinished) {
            while (true) {
                try {
                    line = reader.readLine().split(",");
                } catch (IOException e) {
                    isFinished = true;
                    break;
                }
                for (int idx=0; idx < boolIndexes.size(); ++idx) boolVars[idx] = Boolean.parseBoolean(line[boolIndexes.get(idx)]);
                for (int idx=0; idx < doubleIndexes.size(); ++idx) doubleVars[idx] = Double.parseDouble(line[doubleIndexes.get(idx)]);
                if (vehicleIdx != Integer.parseInt(line[21])) {
                    break;
                }
                trajectory.add(new TrajectoryRecord(boolNames, doubleNames, boolVars, doubleVars));
                times.add(Double.parseDouble(line[22]));
            }
            createSignalAndUpdate(trajectory, times, signals);
            vehicleIdx = Integer.parseInt(line[21]);
            trajectory.add(new TrajectoryRecord(boolNames, doubleNames, boolVars, doubleVars));
            times.add(Double.parseDouble(line[22]));
        }
        return signals;
    }

    private static void createSignalAndUpdate(List<TrajectoryRecord> trajectory, List<Double> times,
                                              List<Signal<TrajectoryRecord>> signals) {
        Signal<TrajectoryRecord> currSignal = new Signal<>();
        double start = times.get(0);
        int length = times.size();
        for (int i=0; i < length; ++i) {
            currSignal.add((times.get(i) - start) / length, trajectory.get(i));
        }
        currSignal.endAt(times.get(length - 1));
        signals.add(currSignal);
        trajectory.clear();
        times.clear();
    }

}
