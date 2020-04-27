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
    //private static double min = Double.MAX_VALUE;

    public static BufferedReader createReaderFromFile(String fileName) throws IOException {
        Path path = Paths.get(".", fileName);
        InputStream in = Files.newInputStream(path);
        return new BufferedReader(new InputStreamReader(in));
    }
    // TODO: maybe fix Long Method
    public static List<Signal<TrajectoryRecord>> parseSignals(BufferedReader reader, List<Integer> boolIndexes,
                                                              List<Integer> doubleIndexes) {
        List<Signal<TrajectoryRecord>> signals = new ArrayList<>();
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
        //System.out.println(min);
        return signals;
    }

    private static void createSignalAndUpdate(List<TrajectoryRecord> trajectory, List<Long> times,
                                              List<Signal<TrajectoryRecord>> signals) {
        if (times.size() == 0) { // TODO: maybe more complete check
            return;
        }
        Signal<TrajectoryRecord> currSignal = new Signal<>();
        //double start = times.get(0);
        int length = times.size();
        //double end = times.get(times.size() - 1);
        long start = times.get(0);//1113433135300L;
        //long end = 1113438734000L;
        for (int i=0; i < length; ++i) {
            //System.out.println((double)(times.get(i) - start));
            //currSignal.add((times.get(i) - start) / (double)(end - start), trajectory.get(i));
            currSignal.add((times.get(i) - start) / 100.0, trajectory.get(i));
        }
        //currSignal.endAt((times.get(length - 1) - start) / (double)(end - start));
        currSignal.endAt((times.get(length - 1) - start) / 100.0);
        //if (currSignal.end() < min) {
        //    min = currSignal.end();
        //}
        signals.add(currSignal);
        trajectory.clear();
        times.clear();
        //System.out.println(currSignal.start() / 100.0 + " " + currSignal.end() / 100.0);
    }

}
