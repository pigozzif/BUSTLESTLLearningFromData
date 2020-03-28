package TrafficGenerator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.text.SimpleDateFormat;


public class GUIMain extends Application {

    private double frameDuration = 0.1;
    public static double resizeRate = 1.0;
    public static int size = 1790;
    public static int maxSize = 1790 / 2;
    public static int offset = 150;
    private Pane root;
    private CarPool carPool;
    private Timeline timeline;
    private FrameHandler frameHandler;
    private Label timeDisplay = new Label();
    private Label tipLabel = new Label();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public void setCurrentTime(long unixTime) {
        //Date date = new Date(unixTime);
        //String formattedDate = sdf.format(date);
        timeDisplay.setText(Long.toString(unixTime));
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        root.setStyle("-fx-background-color: black");

        carPool = new CarPool(root);
        frameHandler = new FrameHandler(this, carPool);
        timeline = new Timeline(new KeyFrame(Duration.seconds(frameDuration), frameHandler));
        timeline.setCycleCount(Timeline.INDEFINITE);
        createAndSetStartAndStopButtons();
        createAndSetForwardButtons();

        setTimeLabel();
        installTipLabel();
        initInterstate();

        primaryStage.setTitle("Traffic Simulator");
        primaryStage.setScene(new Scene(root, maxSize / resizeRate, 500 / resizeRate));
        primaryStage.show();
    }

    private void createAndSetForwardButtons() {
        Button forwardButton = new Button("Fast-Forward (x2) \u23E9");
        Button backwardButton = new Button("Fast-Backward (x2) \u23EA");
        forwardButton.setLayoutY(400);
        backwardButton.setLayoutY(450);
        forwardButton.setLayoutX(25);
        backwardButton.setLayoutX(25);
        forwardButton.setOnAction((ActionEvent e) -> { frameDuration /= 2.0;
                                                        resetTimeline();});
        backwardButton.setOnAction((ActionEvent e) -> { frameDuration = Math.min(0.1, frameDuration * 2.0);
                                                        resetTimeline();});
        root.getChildren().add(forwardButton);
        root.getChildren().add(backwardButton);
    }

    private void resetTimeline() {
        timeline.stop();
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(frameDuration), frameHandler));
        timeline.play();
    }

    private void createAndSetStartAndStopButtons() {
        Button stopButton = new Button("Pause \u23F8");
        Button startButton = new Button("Play \u25B6");
        stopButton.setLayoutY(350);
        startButton.setLayoutY(300);
        stopButton.setLayoutX(25);
        startButton.setLayoutX(25);
        stopButton.setOnAction((ActionEvent e) -> timeline.stop());
        startButton.setOnAction((ActionEvent e) -> {frameDuration = 0.1; resetTimeline();});
        root.getChildren().add(stopButton);
        root.getChildren().add(startButton);
    }

    private void setTimeLabel() {
        timeDisplay.setTextFill(Color.BLACK);
        timeDisplay.setFont(new Font(40));
        timeDisplay.setLayoutX(600);
        timeDisplay.setLayoutY(450);
        root.getChildren().add(timeDisplay);
    }

    private void installTipLabel() {
        tipLabel.setTextFill(Color.WHITE);
        tipLabel.setFont(new Font(25));
        tipLabel.setLayoutX(337.5);
        tipLabel.setLayoutY(300);
        root.getChildren().add(tipLabel);
    }

    private void initInterstate() {
        addFirstLevel();
        addSecondLevel();
        Interstate.addFinishZone(root, 1639 - maxSize, offset, 128, 78);
        addGuardRails();
        addFinishZones();
    }

    private void addFirstLevel() {
        Interstate.addLine(root, 0, 11.970656239550111, size, 12.234536308717434);
        Interstate.addLine(root, 0, 23.758719020228323, size, 24.269472721796284);
        Interstate.addLine(root, 0, 35.78743515412722, size, 35.941307278737);
        Interstate.addLine(root, 0, 48.02729084588756, size, 48.05431764406775);
        Interstate.addLine(root, 0, 60.22450004706112, size, 60.100793957410055);
    }

    private void addSecondLevel() {
        Interstate.addLine(root, 0, 11.970656239550111 + offset, 1639 - maxSize + 128, 12.234536308717434 + offset);
        Interstate.addLine(root, 0, 23.758719020228323 + offset, 1639 - maxSize + 128, 24.269472721796284 + offset);
        Interstate.addLine(root, 0, 35.78743515412722 + offset, 1639 - maxSize + 128, 35.941307278737 + offset);
        Interstate.addLine(root, 0, 48.02729084588756 + offset, 1639 - maxSize + 128, 48.05431764406775 + offset);
        Interstate.addLine(root, 0, 60.22450004706112 + offset, 1639 - maxSize + 128, 60.100793957410055 + offset);
    }

    private void addGuardRails() {
        Interstate.addGuardRail(root, offset, offset, 1639 - maxSize + 128);
        Interstate.addGuardRail(root, 78, 78, maxSize);
        Interstate.addGuardRail(root, 78 + offset, 78 + offset, 1639 - maxSize + 128);
    }

    private void addFinishZones() {
        Interstate.addOnRampSide(root, 78, 816.5442538417742, 92.33, 419.454);
        Interstate.addOnRampSide(root, 78, 345.11510956663903, 96.462, 132.149);
    }

    @Override
    public void stop() { Platform.exit(); }

    public static void main(String[] args) { launch(args); }

}
