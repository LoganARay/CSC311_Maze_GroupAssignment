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
    private boolean maze2 = false;

    @FXML
    public void initialize() {
        mainPane.setFocusTraversable(true);
        javafx.application.Platform.runLater(mainPane::requestFocus);
        mainPane.setOnMouseClicked(event -> mainPane.requestFocus());
        mainPane.setOnKeyPressed(event -> {
                PixelReader m= maze.getPixelReader();
                if (event.getCode() == KeyCode.UP) {
                    if(isPath(m, (int)robotView.getLayoutX(), (int)robotView.getLayoutY()-1)
                            && isPath(m, (int)robotView.getLayoutX() + 25, (int)robotView.getLayoutY()-1)){
                        robotView.setLayoutY(robotView.getLayoutY()-1);
                    }
                }
                if (event.getCode() == KeyCode.DOWN) {
                    if(isPath(m, (int)robotView.getLayoutX(), (int)robotView.getLayoutY()+26)
                            && isPath(m, (int)robotView.getLayoutX() + 25, (int)robotView.getLayoutY()+26)){
                        robotView.setLayoutY(robotView.getLayoutY()+1);
                    }
                }
                if (event.getCode() == KeyCode.LEFT) {
                    if(isPath(m, (int)robotView.getLayoutX()-1, (int)robotView.getLayoutY())
                            && isPath(m, (int)robotView.getLayoutX()-1, (int)robotView.getLayoutY()+25)){
                        robotView.setLayoutX(robotView.getLayoutX()-1);
                    }
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    if(isPath(m, (int)robotView.getLayoutX()+26, (int)robotView.getLayoutY())
                            && isPath(m, (int)robotView.getLayoutX()+26, (int)robotView.getLayoutY()+25)){
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

        maze2 = mazeFile.equals("maze2.png");
    }

    private boolean isPath(PixelReader pixelReader, int x, int y) {

        if (x < 0 || y < 0 || x >= maze.getWidth() || y >= maze.getHeight()) {
            return false;
        }

        Color pixelColor = pixelReader.getColor(x, y);

        if (maze2) {
            return !isBlueWall(pixelColor);
        }

        return pixelColor.equals(pixelReader.getColor(0, 0));
    }

    private boolean isBlueWall(Color color) {
        return color.getBlue() > 0.7
                && color.getRed() < 0.3
                && color.getGreen() < 0.5;
    }
}