package TrafficGenerator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class FrameHandler implements EventHandler<ActionEvent> {

    private Main gui;
    private FileParser parser;
    private CarPool carPool;

    public FrameHandler(Main g, CarPool cp) {
        gui = g;
        parser = new FileParser();
        carPool = cp;
    }

    @Override
    public void handle(ActionEvent e) {
        ArrayList<Field> frameBatch = new ArrayList<>();
        HashSet<String> totalCars = new HashSet<>();
        try {
            frameBatch = parser.fetchNextFrame();
        }
        catch (IOException ex) { gui.stop(); }
        frameBatch.forEach(field -> { String id = field.getId();
                                   totalCars.add(id);
                                   carPool.checkAndSetCar(id, field);
                                   });
        carPool.checkAbsentCars(totalCars);
        gui.setCurrentTime(frameBatch.get(0).getTime());
        e.consume();
    }

}
