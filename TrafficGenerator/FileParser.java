package TrafficGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class FileParser {

    private BufferedReader reader;
    private Field cache = null;
    private long currentFrame = 1113433135300L;

    public FileParser() {
        try {
            Path path = Paths.get(".", "Next_Generation_Simulation__NGSIM__Vehicle_Trajectories_and_Supporting_Data6.csv");
            InputStream in = Files.newInputStream(path);
            reader = new BufferedReader(new InputStreamReader(in));
            reader.readLine();
        }
        catch (IOException e) {
            System.out.println("An IOException Has Occurred while Importing Data!");
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Field> fetchNextFrame() throws IOException {
        ArrayList<Field> inLines = new ArrayList<>();
        if (cache != null) {
            inLines.add(cache);
        }
        long frameID;
        Field line;
        while (true) {
            line = new Field(reader.readLine());
            frameID = line.getTime();
            if (frameID == currentFrame) {
                inLines.add(line);
            }
            else {
                cache = line;
                break;
            }
        }
        currentFrame += 100;
        return inLines;
    }

}
