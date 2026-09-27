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
    private StackPane maze2Content;

    @FXML
    public void initialize() {
        try {
            showVehicle("robot.fxml", "maze2.png", maze2Content, 22, 21);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRobot() throws IOException {
        showVehicle("robot.fxml", "maze.png", maze1Content, 10, 260);
    }

    @FXML
    private void showCar() throws IOException {
        showVehicle("car.fxml", "maze.png", maze1Content, 10, 260);
    }

    @FXML
    private void showRobotMaze2() throws IOException {
        showVehicle("robot.fxml", "maze2.png", maze2Content, 22, 21);
    }

    @FXML
    private void showCarMaze2() throws IOException {
        showVehicle("car.fxml", "maze2.png", maze2Content, 22, 21);
    }

    private void showVehicle(String fileName, String mazeFile,
                             StackPane content, double startX, double startY) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
        Parent view = loader.load();

        Object controller = loader.getController();

        if (controller instanceof RobotController) {
            RobotController robotController = (RobotController) controller;
            robotController.setMaze(mazeFile);
            robotController.setStartPosition(startX, startY);
        }

        if (controller instanceof CarController) {
            CarController carController = (CarController) controller;
            carController.setMaze(mazeFile);
            carController.setStartPosition(startX, startY);
        }

        content.getChildren().setAll(view);
    }
}