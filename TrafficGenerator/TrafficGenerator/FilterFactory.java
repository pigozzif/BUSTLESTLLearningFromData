package TrafficGenerator;

import javafx.scene.paint.Color;
import java.util.function.Consumer;


public class FilterFactory {

    public static Consumer<Car> createNullLambda() {
        return (Car car) -> {};
    }

    public static Consumer<Car> createByChangingLane() {
        return (Car car) -> {
            if (car.getField().getIsChangingLane() == '1') {
                car.setFill(Color.RED);
            }
            else {
                car.setFill(Color.BLUE);
            }};
    }

    public static Consumer<Car> createByApproachingOnramp() {
        return (Car car) -> {
            if (car.getField().getApproachingOnramp() == '1') {
                car.setFill(Color.RED);
            } else {
                car.setFill(Color.BLUE);
            }};
    }

}
