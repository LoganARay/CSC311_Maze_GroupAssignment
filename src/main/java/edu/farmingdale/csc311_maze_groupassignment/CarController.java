package edu.farmingdale.csc311_maze_groupassignment;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


import static edu.farmingdale.csc311_maze_groupassignment.HelloApplication.scene;

public class CarController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Image maze;

    @FXML
    private ImageView mazeView;

    private Car car;
    private static final double SPEED = 2;

    @FXML
    public void initialize() {
        car = new Car();

        car.setLayoutX(10);
        car.setLayoutY(260);

        mainPane.getChildren().add(car);

        mainPane.setFocusTraversable(true);
        javafx.application.Platform.runLater(mainPane::requestFocus);
        mainPane.setOnMouseClicked(event -> mainPane.requestFocus());

        mainPane.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.UP) {
                    moveCar(0, -SPEED, 270);
                }

                if (event.getCode() == KeyCode.DOWN) {
                    moveCar(0, SPEED, 90);
                }

                if (event.getCode() == KeyCode.LEFT) {
                    moveCar(-SPEED, 0, 180);
                }

                if (event.getCode() == KeyCode.RIGHT) {
                    moveCar(SPEED, 0, 0);
                }
            if (event.getCode().isArrowKey()) {
                event.consume();
            }
        });
    }

    private void moveCar(double dx, double dy, double rotation) {
        double oldRotation = car.getRotate();
        car.setRotate(rotation);

        if (canMove(dx, dy)) {
            car.setLayoutX(car.getLayoutX() + dx);
            car.setLayoutY(car.getLayoutY() + dy);
        } else {
            car.setRotate(oldRotation);
        }
    }

    private boolean canMove(double dx, double dy) {
        PixelReader pixelReader = maze.getPixelReader();
        Color pathColor = pixelReader.getColor(0, 0);

        Bounds bounds = car.getBoundsInParent();

        int left = (int) Math.floor(bounds.getMinX() + dx);
        int right = (int) Math.ceil(bounds.getMaxX() + dx);
        int top = (int) Math.floor(bounds.getMinY() + dy);
        int bottom = (int) Math.ceil(bounds.getMaxY() + dy);

        return isPath(pixelReader, pathColor, left, top)
                && isPath(pixelReader, pathColor, right, top)
                && isPath(pixelReader, pathColor, left, bottom)
                && isPath(pixelReader, pathColor, right, bottom);
    }

    private boolean isPath(PixelReader pixelReader, Color pathColor, int x, int y) {
        if (x < 0 || y < 0 || x >= maze.getWidth() || y >= maze.getHeight()) {
            return false;
        }

        return pixelReader.getColor(x, y).equals(pathColor);
    }

    public void setMaze(String mazeFile) {
        maze = new Image(
                getClass().getResource(mazeFile).toExternalForm()
        );

        mazeView.setImage(maze);
    }
}