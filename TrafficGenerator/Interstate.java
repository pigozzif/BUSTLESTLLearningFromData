package TrafficGenerator;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


public class Interstate {

    public static void addLine(Pane root, double startX, double startY, double endX, double endY) {
        Line line = new Line(startX / Main.resizeRate, startY / Main.resizeRate,
                endX / Main.resizeRate, endY / Main.resizeRate);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(0.5);
        line.getStrokeDashArray().addAll(2d);
        root.getChildren().add(line);
    }

    public static void addGuardRail(Pane root, double startY, double endY, double endX) {
        Line line = new Line(0 / Main.resizeRate, startY / Main.resizeRate, endX / Main.resizeRate, endY / Main.resizeRate);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(1);
        root.getChildren().add(line);
    }

    public static void addFinishZone(Pane root, double startX, double startY, double width, double height) {
        Rectangle finishZone = new Rectangle(startX / Main.resizeRate, startY / Main.resizeRate, width / Main.resizeRate, height / Main.resizeRate);
        finishZone.setFill(Color.RED);
        finishZone.setOpacity(0.25);
        root.getChildren().add(finishZone);
    }

    public static void addOnRampSide(Pane root, double startX, double startY, double endX, double endY) {
        Line line = new Line(startY / Main.resizeRate, startX / Main.resizeRate,
                endY / Main.resizeRate, endX / Main.resizeRate);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(1);
        root.getChildren().add(line);
    }

}
