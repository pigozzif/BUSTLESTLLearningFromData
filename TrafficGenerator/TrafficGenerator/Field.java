package TrafficGenerator;

import java.util.Arrays;


public class Field {

    String[] fields;

    public Field(String reading) {
        fields = reading.split(",");
    }

    public int getNumChars() {
        return Arrays.stream(fields).reduce(0, (partialSum, element) -> partialSum + element.length(), Integer::sum);
    }

    public String getId() { return fields[1]; }

    public String getFrame() { return fields[2]; }

    public long getTime() { return Long.parseLong(fields[3]); }

    public double getLocalX() { return Double.parseDouble(fields[4]); }

    public double getLocalY() { return Double.parseDouble(fields[5]); }

    public double getLength() { return Double.parseDouble(fields[6]); }

    public double getWidth() { return Double.parseDouble(fields[7]); }

    public String getVehicleClass() { return fields[8]; }

    public String getVelocity() { return fields[9]; }

    public String getAcceleration() { return fields[10]; }

    public char getLaneID() { return fields[11].charAt(0); }

    public String[] getNeighbours() {
        return new String[]{fields[16], fields[17], fields[18], fields[19],
                fields[20], fields[21], fields[22], fields[23]};
    }

    public char getIsChangingLane() { return fields[15].charAt(0); }

    public char getApproachingOnramp() { return fields[40].charAt(0); }

}
