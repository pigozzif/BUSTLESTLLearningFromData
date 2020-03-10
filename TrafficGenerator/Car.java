package TrafficGenerator;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.function.Consumer;


public class Car extends Rectangle {

    private double xOffset = 0.0;
    private double yOffset = 0.0;
    private double maxSize = Main.maxSize;
    private Field field;
    private Label text;

    public Car(String id, Field firstField, CarPool cp) {
        super((firstField.getLocalY() - firstField.getLength()) / Main.resizeRate, (firstField.getLocalX() - firstField.getWidth() / 2) / Main.resizeRate,
                firstField.getLength() / Main.resizeRate, firstField.getWidth() / Main.resizeRate);
        //setFill(Color.color(Math.random(), Math.random(), Math.random()));
        setFill(Color.BLUE);
        setId(id);
        field = firstField;
        text = new Label();
        text.setTextFill(Color.WHITE);
        text.setLayoutX(getX());
        text.setLayoutY(getY());
        setOnMouseEntered((MouseEvent e) -> cp.setCarsColours(field.getNeighbours(), id, Color.RED));
        setOnMouseExited((MouseEvent e) -> cp.extinguishNeighbours(id, Color.BLUE));
        setOnMouseClicked((MouseEvent e) -> cp.updateTipLabel(this));
    }

    public Field getField() { return field; }

    public Label getText() { return text; }

    public void setText(String newText) { text.setText(newText); }

    public void updatePosition(Field newField, Consumer<Car> filter) {
        if (newField.getLocalY() > maxSize) {
            xOffset += Main.offset;
            yOffset = -maxSize / Main.resizeRate;
            maxSize += Main.maxSize;
        }
        setX(yOffset + (newField.getLocalY() / Main.resizeRate) - getWidth());
        setY(xOffset + (newField.getLocalX() / Main.resizeRate) - (getHeight() / 2.0));
        field = newField;
        filter.accept(this);
        text.setLayoutX(getX());
        text.setLayoutY(getY());
    }

    public String getInfo() {
        return "id: " + getId() + "\n" + "isChangingLane: " + field.getIsChangingLane() +
                "\n" + "LaneID: " + field.getLaneID();
    }

}
