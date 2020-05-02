package BuildingBlocks;

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


public class SignalBuilder {

    private final static int windowSize = 200;

    public static BufferedReader createReaderFromFile(String fileName) throws IOException {
        Path path = Paths.get(".", fileName);
        InputStream in = Files.newInputStream(path);
        return new BufferedReader(new InputStreamReader(in));
    }
    // TODO: maybe fix Long Method
    public static List<Signal<TrajectoryRecord>[]> parseSignals(BufferedReader reader, List<Integer> boolIndexes,
                                                              List<Integer> doubleIndexes) {
        List<Signal<TrajectoryRecord>[]> signals = new ArrayList<>();
        int vehicleIdx = 1;
        boolean isFinished = false;
        String[] line = new String[boolIndexes.size() + doubleIndexes.size() + 1];
        List<TrajectoryRecord> trajectory = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        boolean[] boolVars = new boolean[boolIndexes.size()];
        double[] doubleVars = new double[doubleIndexes.size()];
        while (!isFinished) {
            while (true) {
                try {
                    line = reader.readLine().split(",");
                }
                catch (NullPointerException | IOException e) {
                    isFinished = true;
                    break;
                }
                for (int idx=0; idx < boolIndexes.size(); ++idx) {
                    boolVars[idx] = Boolean.parseBoolean(line[boolIndexes.get(idx)]);
                }
                for (int idx=0; idx < doubleIndexes.size(); ++idx) {
                    doubleVars[idx] = Double.parseDouble(line[doubleIndexes.get(idx)]);
                }
                if (vehicleIdx != Integer.parseInt(line[14])) {
                    break;
                }
                trajectory.add(new TrajectoryRecord(boolVars, doubleVars));
                times.add(Long.parseLong(line[15]));
            }
            //if (line[14].equals("3")) {
            //    break;
            //}
            createSignalAndUpdate(trajectory, times, signals);
            vehicleIdx = Integer.parseInt(line[14]);
            trajectory.add(new TrajectoryRecord(boolVars, doubleVars));
            times.add(Long.parseLong(line[15]));
        }
        return signals;
    }

    private static void createSignalAndUpdate(List<TrajectoryRecord> trajectory, List<Long> times,
                                              List<Signal<TrajectoryRecord>[]> signals) {
        if (times.size() == 0) { // TODO: maybe more complete check
            return;
        }
        Signal<?>[] innerSignal = new Signal<?>[(trajectory.size() * 2) / windowSize];
        int length = times.size();
        long start = times.get(0);
        int j = 0;
        int i;
        int t = 0;
        while (j < length) {
            Signal<TrajectoryRecord> currSignal = new Signal<>();
            for (i = 0; i < windowSize && j < length; ++i, ++j) {
                currSignal.add((times.get(j) - start) / 100.0, trajectory.get(j));
            }
            currSignal.endAt((times.get(j - 1) - start) / 100.0);
            innerSignal[t] = currSignal;
            j -= windowSize / 2;
            if (currSignal.size() != windowSize) {
                break;
            }
            ++t;
        }
        signals.add((Signal<TrajectoryRecord>[]) innerSignal);
        trajectory.clear();
        times.clear();
    }

}
