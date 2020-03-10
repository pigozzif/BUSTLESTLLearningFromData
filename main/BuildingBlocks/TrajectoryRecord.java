package BuildingBlocks;

import java.util.HashMap;


public class TrajectoryRecord {

    private final HashMap<String, Boolean> boolVariables;
    private final HashMap<String, Double> doubleVariables;

    public TrajectoryRecord(String[] boolNames, String[] doubleNames, boolean[] boolValues, double[] doubleValues) {
        this.boolVariables = new HashMap<>();
        this.doubleVariables = new HashMap<>();
        for (int i=0; i < boolNames.length; ++i) {
            this.boolVariables.put(boolNames[i], boolValues[i]);
        }
        for (int i=0; i < doubleNames.length; ++i) {
            this.doubleVariables.put(doubleNames[i], doubleValues[i]);
        }
    }

    public Boolean getBool(String var) {
        return this.boolVariables.get(var);
    }

    public Double getDouble(String var) {
        return this.doubleVariables.get(var);
    }

}
