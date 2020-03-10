import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.HashSet;


public class CarPool {

    private Pane guiPane;
    private HashMap<String, Car> currentCars;
    private HashMap<String, String[]> carToNeighbours;
    private String[] quadrants = new String[]{"NE", "N", "NW", "W", "SW", "S", "SE", "E"};

    public CarPool(Pane pane) {
        guiPane = pane;
        currentCars = new HashMap<>();
        carToNeighbours = new HashMap<>();
    }

    public void setCarsColours(String[] ids, String id, Color color) {
        int quadrantIndex = 0;
        for (String car : ids) {
            if (!car.equals("-1")) {
                currentCars.get(car).setFill(color);
                currentCars.get(car).setText(quadrants[quadrantIndex]);
            }
            ++quadrantIndex;
        }
        carToNeighbours.put(id, ids);
    }

    public void extinguishNeighbours(String id, Color color) {
        for (String car : carToNeighbours.get(id)) {
            if (!car.equals("-1")) {
                currentCars.get(car).setFill(color);
                currentCars.get(car).setText("");
            }
        }
        carToNeighbours.remove(id);
    }

    public void checkAndSetCar(String currID, Field field) {
        if (currentCars.containsKey(currID)) {
            currentCars.get(currID).updatePosition(field, FilterFactory.createByApproachingOnramp());
        }
        else {
            Car newCar = new Car(currID, field, this);
            currentCars.put(currID, newCar);
            guiPane.getChildren().add(newCar);
            guiPane.getChildren().add(newCar.getText());
        }
    }

    public void checkAbsentCars(HashSet<String> totalCars) {
        currentCars.forEach((k, v) -> {
                if (!totalCars.contains(k)) {
                    v.setVisible(false);
                    guiPane.getChildren().remove(v);
                }
            });
    }

    public void updateTipLabel(Car car) {
        Label label = (Label) guiPane.getChildren().get(5);
        label.setText(car.getInfo());
    }

}
