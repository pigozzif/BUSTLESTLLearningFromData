package TrafficGenerator;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


public class Interstate {

    public static void addLine(Pane root, double startX, double startY, double endX, double endY) {
        Line line = new Line(startX / GUIMain.resizeRate, startY / GUIMain.resizeRate,
                endX / GUIMain.resizeRate, endY / GUIMain.resizeRate);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(0.5);
        line.getStrokeDashArray().addAll(2d);
        root.getChildren().add(line);
    }

    public static void addGuardRail(Pane root, double startY, double endY, double endX) {
        Line line = new Line(0 / GUIMain.resizeRate, startY / GUIMain.resizeRate, endX / GUIMain.resizeRate, endY / GUIMain.resizeRate);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(1);
        root.getChildren().add(line);
    }

    public static void addFinishZone(Pane root, double startX, double startY, double width, double height) {
        Rectangle finishZone = new Rectangle(startX / GUIMain.resizeRate, startY / GUIMain.resizeRate, width / GUIMain.resizeRate, height / GUIMain.resizeRate);
        finishZone.setFill(Color.RED);
        finishZone.setOpacity(0.25);
        root.getChildren().add(finishZone);
    }

    public static void addOnRampSide(Pane root, double startX, double startY, double endX, double endY) {
        Line line = new Line(startY / GUIMain.resizeRate, startX / GUIMain.resizeRate,
                endY / GUIMain.resizeRate, endX / GUIMain.resizeRate);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(1);
        root.getChildren().add(line);
    }

}
