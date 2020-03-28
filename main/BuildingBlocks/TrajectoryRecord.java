package BuildingBlocks;

import java.util.HashMap;


public class TrajectoryRecord {

    private static final HashMap<String, Integer> varNamesToIndex = new HashMap<>() {{ put("isChangingLane", 0); put("isApproachingOnramp", 1);
        put("isByGuardrail", 2); put("V_vel", 1); put("Lane_ID", 2); put("NE_dist", 4); put("N_dist", 5); put("NW_dist", 6);
        put("W_dist", 7); put("SW_dist", 8); put("S_dist", 9); put("SE_dist", 10); put("E_dist", 11); put("Vehicle_ID", 22);
        put("Global_Time", 23); }};
    private final boolean[] boolVariables;
    private final double[] doubleVariables;

    public TrajectoryRecord(boolean[] boolValues, double[] doubleValues) {
        boolVariables = boolValues;
        doubleVariables = doubleValues;
    }

    public Boolean getBool(String var) {
        return this.boolVariables[varNamesToIndex.get(var)];
    }

    public Double getDouble(String var) {
        return this.doubleVariables[varNamesToIndex.get(var)];
    }

}
