package BuildingBlocks;

import Expressions.ExpressionsFactory;

import java.util.HashMap;


public class TrajectoryRecord {

    private static final HashMap<String, Integer> varNamesToIndex = new HashMap<>();
    static {
        int i = 0;
        for (String name : ExpressionsFactory.retrieveBooleanNames()) {
            varNamesToIndex.put(name, i);
            ++i;
        }
        i = 0;
        for (String name : ExpressionsFactory.retrieveNumericalNames()) {
            varNamesToIndex.put(name, i);
            ++i;
        }
    }
    private final boolean[] boolVariables;
    private final double[] doubleVariables;

    public TrajectoryRecord(boolean[] boolValues, double[] doubleValues) {
        this.boolVariables = new boolean[boolValues.length];
        this.doubleVariables = new double[doubleValues.length];
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
