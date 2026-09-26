package edu.farmingdale.csc311_maze_groupassignment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MazeTabsController {
    @FXML
    private StackPane maze1Content;

    @FXML
    private void showRobot() throws IOException {
        showVehicle("robot.fxml");
    }

    @FXML
    private void showCar() throws IOException {
        showVehicle("car.fxml");
    }

    private void showVehicle(String fileName) throws IOException {
        Parent view = FXMLLoader.load(getClass().getResource(fileName));
        maze1Content.getChildren().setAll(view);
    }
}