import java.util.HashMap;


public class Record {

    private final HashMap<String, Boolean> boolVariables;
    private final HashMap<String, Double> doubleVariables;

    public Record(String[] boolNames, String[] doubleNames, boolean[] boolValues, double[] doubleValues) {
        boolVariables = new HashMap<>();
        doubleVariables = new HashMap<>();
        for (int i=0; i < boolNames.length; ++i) {
            boolVariables.put(boolNames[i], boolValues[i]);
        }
        for (int i=0; i < doubleNames.length; ++i) {
            doubleVariables.put(doubleNames[i], doubleValues[i]);
        }
    }

    public Boolean getBool(String var) {
        return boolVariables.get(var);
    }

    public Double getDouble(String var) {
        return doubleVariables.get(var);
    }

}
