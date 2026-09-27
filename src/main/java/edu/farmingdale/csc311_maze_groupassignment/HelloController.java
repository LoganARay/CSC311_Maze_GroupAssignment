package edu.farmingdale.csc311_maze_groupassignment;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void robot(ActionEvent actionEvent) throws Exception{
        HelloApplication.setRoot("robot.fxml");
    }
    @FXML
    private void exit() {
       Platform.exit();
    }
    @FXML
    protected void car(ActionEvent actionEvent) throws Exception{
        HelloApplication.setRoot("car.fxml");
    }

    @FXML
    protected void openMazeTabs(ActionEvent actionEvent) throws Exception {
        HelloApplication.setRoot("mazeTabs.fxml");
    }

    @FXML
    public void initialize() {
        welcomeText.setFont(new Font("Arial", 30));
    }
}