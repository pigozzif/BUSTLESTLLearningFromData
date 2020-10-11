package BuildingBlocks.SignalBuilders;

import BuildingBlocks.TrajectoryRecord;
import eu.quanticol.moonlight.signal.Signal;

import java.io.*;
import java.util.*;


public class MaritimeSignalBuilder implements SignalBuilder<Signal<TrajectoryRecord>> {

    private HashMap<String, double[]> varsBounds = new HashMap<>();
    private double[] temporalBounds;

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
        this.temporalBounds = new double[]{times[0], times[times.length - 1] / 2};
        int n = times.length;
        BufferedReader reader = this.createReaderFromFile(fileName);
        String[] header = reader.readLine().split(",");
        for (String var : header) {
            this.varsBounds.put(var, new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
        }
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
            for (int k=0; k < varsData.length; ++k) {
                double[] temp = this.varsBounds.get(header[k]);
                temp[0] = Math.min(varsData[k], temp[0]);
                temp[1] = Math.max(varsData[k], temp[1]);
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

    public HashMap<String, double[]> getVarsBounds() {
        return this.varsBounds;
    }

    public double[] getTemporalBounds() {
        return this.temporalBounds;
    }

}
