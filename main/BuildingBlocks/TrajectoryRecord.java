package BuildingBlocks;

import java.util.HashMap;
import java.util.Map;


public class TrajectoryRecord {

    private static final Map<String, Integer> varNamesToIndex = new HashMap<>();
    private final boolean[] boolVariables;
    private final double[] doubleVariables;

    public TrajectoryRecord(boolean[] boolValues, String[] booleanNames, double[] doubleValues, String[] doubleNames) {
        this.boolVariables = new boolean[boolValues.length];
        this.doubleVariables = new double[doubleValues.length];
        int i = 0;
        for (String name : booleanNames) {
            varNamesToIndex.put(name, i);
            ++i;
        }
        i = 0;
        for (String name : doubleNames) {
            varNamesToIndex.put(name, i);
            ++i;
        }
        System.arraycopy(boolValues, 0, this.boolVariables, 0, boolValues.length);
        System.arraycopy(doubleValues, 0, this.doubleVariables, 0, doubleValues.length);
    }

    public Boolean getBool(String var) {
        return this.boolVariables[varNamesToIndex.get(var)];
    }

    public Double getDouble(String var) {
        return this.doubleVariables[varNamesToIndex.get(var)];
    }

}
