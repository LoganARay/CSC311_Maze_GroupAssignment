package edu.farmingdale.csc311_maze_groupassignment;

import edu.farmingdale.csc311_maze_groupassignment.HelloApplication;
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
    protected void car(ActionEvent actionEvent) throws Exception{
        HelloApplication.setRoot("car.fxml");
    }

    @FXML
    public void initialize() {
        welcomeText.setFont(new Font("Arial", 30));
    }
}