package edu.farmingdale.csc311_maze_groupassignment;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CarController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    public void initialize() {
        Car car = new Car();

        car.setLayoutX(10);
        car.setLayoutY(260);

        mainPane.getChildren().add(car);
    }
}