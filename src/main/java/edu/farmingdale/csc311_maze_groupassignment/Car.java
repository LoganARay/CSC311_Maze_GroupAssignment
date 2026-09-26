package edu.farmingdale.csc311_maze_groupassignment;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class Car extends Group {

    private final Rectangle body;
    private final Polygon roof;
    private final Circle frontWheel;
    private final Circle backWheel;

    public Car() {
        body = new Rectangle(4, 8, 20, 10);
        body.setFill(Color.RED);

        roof = new Polygon(
                8.0, 8.0,
                11.0, 3.0,
                18.0, 3.0,
                22.0, 8.0
        );
        roof.setFill(Color.RED);

        backWheel = new Circle(9, 19, 3);
        backWheel.setFill(Color.BLACK);

        frontWheel = new Circle(20, 19, 3);
        frontWheel.setFill(Color.BLACK);

        getChildren().addAll(body, roof, backWheel, frontWheel);
    }
}