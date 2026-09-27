package edu.farmingdale.csc311_maze_groupassignment;

import edu.farmingdale.csc311_maze_groupassignment.HelloApplication;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static edu.farmingdale.csc311_maze_groupassignment.HelloApplication.scene;

public class RobotController {
    @FXML
    public Circle c;
    @FXML
    public ImageView mazeView;
    @FXML
    public AnchorPane mainPane;
    @FXML
    public Image maze;
    @FXML
    public ImageView robotView;
    @FXML
    public Image robot;

    @FXML
    public void initialize() {
        mainPane.setFocusTraversable(true);
        javafx.application.Platform.runLater(mainPane::requestFocus);
        mainPane.setOnMouseClicked(event -> mainPane.requestFocus());
        mainPane.setOnKeyPressed(event -> {
                PixelReader m= maze.getPixelReader();
                if (event.getCode() == KeyCode.UP) {
                    if(m.getColor((int)robotView.getLayoutX(), (int)robotView.getLayoutY()-1).equals(m.getColor(0,0))
                            && (m.getColor((int)robotView.getLayoutX() + 25, (int)robotView.getLayoutY()-1).equals(m.getColor(0,0)))){
                        robotView.setLayoutY(robotView.getLayoutY()-1);
                    }
                }
                if (event.getCode() == KeyCode.DOWN) {
                    if(m.getColor((int)robotView.getLayoutX(), (int)robotView.getLayoutY()+26).equals(m.getColor(0,0))
                            && (m.getColor((int)robotView.getLayoutX() + 25, (int)robotView.getLayoutY()+26).equals(m.getColor(0,0)))){
                        robotView.setLayoutY(robotView.getLayoutY()+1);
                    }
                }
                if (event.getCode() == KeyCode.LEFT) {
                    if(m.getColor((int)robotView.getLayoutX()-1, (int)robotView.getLayoutY()).equals(m.getColor(0,0))
                            && (m.getColor((int)robotView.getLayoutX() -1, (int)robotView.getLayoutY()+25).equals(m.getColor(0,0)))){
                        robotView.setLayoutX(robotView.getLayoutX()-1);
                    }
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    if(m.getColor((int)robotView.getLayoutX() + 26, (int)robotView.getLayoutY()).equals(m.getColor(0,0))
                    && (m.getColor((int)robotView.getLayoutX() + 26, (int)robotView.getLayoutY()+25).equals(m.getColor(0,0)))){
                        robotView.setLayoutX(robotView.getLayoutX()+1);
                    }
                }
                if (event.getCode().isArrowKey()) {
                event.consume();
            }
        });
    }

    public void setStartPosition(double x, double y) {
        robotView.setLayoutX(x);
        robotView.setLayoutY(y);
    }

    public void setMaze(String mazeFile) {
        maze = new Image(
                getClass().getResource(mazeFile).toExternalForm()
        );

        mazeView.setImage(maze);
    }
}