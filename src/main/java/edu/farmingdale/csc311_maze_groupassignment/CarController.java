package edu.farmingdale.csc311_maze_groupassignment;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class CarController {

    /*
     * We use a 26 x 26 safe area for automatic pathfinding.
     *
     * This gives the car enough room even when it turns.
     */
    private static final int CAR_SAFE_SIZE = 26;

    /*
     * How fast automatic mode moves.
     *
     * Smaller number = faster animation.
     */
    private static final double ANIMATION_SPEED = 5;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Image maze;

    @FXML
    private ImageView mazeView;

    @FXML
    private Button automaticButton;

    @FXML
    private Button resetButton;

    private Car car;

    private static final double SPEED = 2;

    private boolean maze2 = false;

    /*
     * Remember where this maze begins so Reset knows
     * where to put the car.
     */
    private double startX;
    private double startY;

    /*
     * Stores the automatic animation while it is playing.
     */
    private Timeline automaticTimeline;


    @FXML
    public void initialize() {

        car = new Car();

        car.setLayoutX(10);
        car.setLayoutY(260);

        /*
         * Save the initial position immediately.
         *
         * This prevents the same first-reset problem we had
         * with the Robot.
         */
        startX = car.getLayoutX();
        startY = car.getLayoutY();

        mainPane.getChildren().add(car);

        mainPane.setFocusTraversable(true);

        javafx.application.Platform.runLater(
                mainPane::requestFocus
        );

        mainPane.setOnMouseClicked(event ->
                mainPane.requestFocus()
        );


        /*
         * Existing manual keyboard controls.
         */
        mainPane.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.UP) {

                moveCar(
                        0,
                        -SPEED,
                        270
                );
            }

            if (event.getCode() == KeyCode.DOWN) {

                moveCar(
                        0,
                        SPEED,
                        90
                );
            }

            if (event.getCode() == KeyCode.LEFT) {

                moveCar(
                        -SPEED,
                        0,
                        180
                );
            }

            if (event.getCode() == KeyCode.RIGHT) {

                moveCar(
                        SPEED,
                        0,
                        0
                );
            }

            if (event.getCode().isArrowKey()) {
                event.consume();
            }
        });
    }


    /*
     * Existing manual movement method.
     */
    private void moveCar(
            double dx,
            double dy,
            double rotation) {

        double oldRotation =
                car.getRotate();

        car.setRotate(rotation);

        if (canMove(dx, dy)) {

            car.setLayoutX(
                    car.getLayoutX() + dx
            );

            car.setLayoutY(
                    car.getLayoutY() + dy
            );

        } else {

            car.setRotate(oldRotation);
        }
    }


    /*
     * Existing collision detection used by manual mode.
     */
    private boolean canMove(
            double dx,
            double dy) {

        PixelReader pixelReader =
                maze.getPixelReader();

        Color pathColor =
                pixelReader.getColor(0, 0);

        Bounds bounds =
                car.getBoundsInParent();

        int left =
                (int) Math.floor(
                        bounds.getMinX() + dx
                );

        int right =
                (int) Math.ceil(
                        bounds.getMaxX() + dx
                );

        int top =
                (int) Math.floor(
                        bounds.getMinY() + dy
                );

        int bottom =
                (int) Math.ceil(
                        bounds.getMaxY() + dy
                );

        return isPath(
                pixelReader,
                pathColor,
                left,
                top)

                &&

                isPath(
                        pixelReader,
                        pathColor,
                        right,
                        top)

                &&

                isPath(
                        pixelReader,
                        pathColor,
                        left,
                        bottom)

                &&

                isPath(
                        pixelReader,
                        pathColor,
                        right,
                        bottom);
    }


    /*
     * Called when Automatic is pressed.
     */
    @FXML
    private void playAutomatic() {

        if (maze == null) {
            return;
        }

        /*
         * Stop an old animation if one happens to
         * still be running.
         */
        if (automaticTimeline != null) {
            automaticTimeline.stop();
        }

        /*
         * Find a valid route from the current car
         * position to the maze destination.
         */
        List<int[]> path =
                findAutomaticPath();

        if (path.isEmpty()) {

            System.out.println(
                    "No automatic path could be found."
            );

            return;
        }

        /*
         * Prevent Automatic from being pressed repeatedly
         * while the car is already moving.
         */
        automaticButton.setDisable(true);

        automaticTimeline =
                new Timeline();

        int frame = 0;


        /*
         * Create each animation step.
         *
         * We use every second path position so the animation
         * remains smooth without needing thousands of KeyFrames.
         */
        for (int i = 1;
             i < path.size();
             i += 2) {

            int[] previous =
                    path.get(
                            Math.max(0, i - 2)
                    );

            int[] position =
                    path.get(i);

            final int targetX =
                    position[0];

            final int targetY =
                    position[1];

            /*
             * Determine which direction the car is moving
             * so it visually turns around maze corners.
             */
            final double rotation =
                    getRotation(
                            previous,
                            position
                    );


            KeyFrame keyFrame =
                    new KeyFrame(

                            Duration.millis(
                                    frame * ANIMATION_SPEED
                            ),

                            event -> {

                                car.setRotate(rotation);

                                car.setLayoutX(
                                        targetX
                                );

                                car.setLayoutY(
                                        targetY
                                );
                            }
                    );

            automaticTimeline
                    .getKeyFrames()
                    .add(keyFrame);

            frame++;
        }


        /*
         * Finish at the exact final position.
         */
        int[] finalPosition =
                path.get(
                        path.size() - 1
                );

        int[] previousPosition =
                path.get(
                        Math.max(
                                0,
                                path.size() - 2
                        )
                );

        double finalRotation =
                getRotation(
                        previousPosition,
                        finalPosition
                );


        automaticTimeline
                .getKeyFrames()
                .add(

                        new KeyFrame(

                                Duration.millis(
                                        frame * ANIMATION_SPEED
                                ),

                                event -> {

                                    car.setRotate(
                                            finalRotation
                                    );

                                    car.setLayoutX(
                                            finalPosition[0]
                                    );

                                    car.setLayoutY(
                                            finalPosition[1]
                                    );
                                }
                        )
                );


        /*
         * Once finished, allow Automatic to be used again.
         */
        automaticTimeline.setOnFinished(event -> {

            automaticButton.setDisable(false);

            mainPane.requestFocus();
        });


        automaticTimeline.play();
    }


    /*
     * Returns the rotation needed for the direction
     * the car is currently moving.
     */
    private double getRotation(
            int[] previous,
            int[] current) {

        int dx =
                current[0] - previous[0];

        int dy =
                current[1] - previous[1];


        // Right
        if (dx > 0) {
            return 0;
        }


        // Left
        if (dx < 0) {
            return 180;
        }


        // Down
        if (dy > 0) {
            return 90;
        }


        // Up
        return 270;
    }


    /*
     * Uses Breadth-First Search (BFS) to find the
     * route through the maze.
     */
    private List<int[]> findAutomaticPath() {

        PixelReader pixelReader =
                maze.getPixelReader();

        int width =
                (int) maze.getWidth();

        int height =
                (int) maze.getHeight();


        /*
         * Start from wherever the car currently is.
         */
        int startCarX =
                (int) Math.round(
                        car.getLayoutX()
                );

        int startCarY =
                (int) Math.round(
                        car.getLayoutY()
                );


        int totalPositions =
                width * height;


        /*
         * -2 means the position has not been visited.
         *
         * -1 means it is the starting point.
         */
        int[] previous =
                new int[totalPositions];

        Arrays.fill(
                previous,
                -2
        );


        Queue<Integer> queue =
                new ArrayDeque<>();


        int startId =
                startCarY * width
                        + startCarX;


        previous[startId] = -1;

        queue.add(startId);


        int goalId = -1;


        /*
         * Four directions:
         *
         * right
         * left
         * down
         * up
         */
        int[][] directions = {

                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}

        };


        while (!queue.isEmpty()) {

            int currentId =
                    queue.remove();


            int x =
                    currentId % width;

            int y =
                    currentId / width;


            /*
             * Have we reached the destination?
             */
            if (isGoal(
                    pixelReader,
                    x,
                    y)) {

                goalId =
                        currentId;

                break;
            }


            /*
             * Try each neighboring position.
             */
            for (int[] direction : directions) {

                int nextX =
                        x + direction[0];

                int nextY =
                        y + direction[1];


                /*
                 * Keep the car completely inside the maze.
                 */
                if (nextX < 0
                        || nextY < 0
                        || nextX + CAR_SAFE_SIZE > width
                        || nextY + CAR_SAFE_SIZE > height) {

                    continue;
                }


                int nextId =
                        nextY * width
                                + nextX;


                /*
                 * Skip positions already searched.
                 */
                if (previous[nextId] != -2) {
                    continue;
                }


                /*
                 * Only continue if the car fits without
                 * touching a maze wall.
                 */
                if (!canCarStandAt(
                        pixelReader,
                        nextX,
                        nextY)) {

                    continue;
                }


                previous[nextId] =
                        currentId;

                queue.add(nextId);
            }
        }


        /*
         * No route found.
         */
        if (goalId == -1) {

            return Collections.emptyList();
        }


        /*
         * Reconstruct the path backwards.
         */
        List<int[]> path =
                new ArrayList<>();


        int current =
                goalId;


        while (current != -1) {

            int x =
                    current % width;

            int y =
                    current / width;


            path.add(
                    new int[]{
                            x,
                            y
                    }
            );


            current =
                    previous[current];
        }


        /*
         * Convert:
         *
         * goal -> start
         *
         * into:
         *
         * start -> goal
         */
        Collections.reverse(path);

        return path;
    }


    /*
     * Makes sure the car has enough room at a BFS position.
     *
     * We use a slightly larger safe square so the car
     * can also rotate without clipping into a wall.
     */
    private boolean canCarStandAt(
            PixelReader pixelReader,
            int x,
            int y) {

        Color pathColor =
                pixelReader.getColor(
                        0,
                        0
                );


        return isPath(
                pixelReader,
                pathColor,
                x,
                y)

                &&

                isPath(
                        pixelReader,
                        pathColor,
                        x + CAR_SAFE_SIZE - 1,
                        y)

                &&

                isPath(
                        pixelReader,
                        pathColor,
                        x,
                        y + CAR_SAFE_SIZE - 1)

                &&

                isPath(
                        pixelReader,
                        pathColor,
                        x + CAR_SAFE_SIZE - 1,
                        y + CAR_SAFE_SIZE - 1);
    }


    /*
     * Determines whether the car has reached
     * the destination of the current maze.
     */
    private boolean isGoal(
            PixelReader pixelReader,
            int x,
            int y) {

        /*
         * Maze 2 ends on the purple square.
         */
        if (maze2) {

            int centerX =
                    x + CAR_SAFE_SIZE / 2;

            int centerY =
                    y + CAR_SAFE_SIZE / 2;


            Color centerColor =
                    pixelReader.getColor(
                            centerX,
                            centerY
                    );


            return isPurple(
                    centerColor
            );
        }


        /*
         * Maze 1 exits on the right-hand side.
         */
        return x + CAR_SAFE_SIZE
                >= maze.getWidth() - 10;
    }


    /*
     * Identifies the purple destination in Maze 2.
     */
    private boolean isPurple(Color color) {

        return color.getBlue() > 0.55
                && color.getRed() > 0.25
                && color.getRed() < 0.75
                && color.getGreen() < 0.35;
    }


    /*
     * Reset button.
     */
    @FXML
    private void resetCar() {

        /*
         * Stop automatic movement if Reset is clicked
         * during the animation.
         */
        if (automaticTimeline != null) {
            automaticTimeline.stop();
        }


        /*
         * Put the car back at this maze's entrance.
         */
        car.setLayoutX(startX);

        car.setLayoutY(startY);


        /*
         * Start by facing right again.
         */
        car.setRotate(0);


        /*
         * Automatic can now be used again.
         */
        automaticButton.setDisable(false);


        /*
         * Restore arrow-key control.
         */
        mainPane.requestFocus();
    }


    /*
     * Existing path check.
     */
    private boolean isPath(
            PixelReader pixelReader,
            Color pathColor,
            int x,
            int y) {

        if (x < 0
                || y < 0
                || x >= maze.getWidth()
                || y >= maze.getHeight()) {

            return false;
        }


        Color pixelColor =
                pixelReader.getColor(
                        x,
                        y
                );


        /*
         * Maze 2:
         * anything except a blue wall is walkable.
         */
        if (maze2) {

            return !isBlueWall(
                    pixelColor
            );
        }


        /*
         * Maze 1:
         * follow the white path color.
         */
        return pixelColor.equals(
                pathColor
        );
    }


    /*
     * Existing blue-wall detection for Maze 2.
     */
    private boolean isBlueWall(
            Color color) {

        return color.getBlue() > 0.7
                && color.getRed() < 0.3
                && color.getGreen() < 0.5;
    }


    /*
     * MazeTabsController calls this after loading the car.
     */
    public void setStartPosition(
            double x,
            double y) {

        /*
         * Remember the maze's beginning for Reset.
         */
        startX = x;

        startY = y;


        car.setLayoutX(
                startX
        );

        car.setLayoutY(
                startY
        );


        /*
         * Face right whenever a new maze/car is loaded.
         */
        car.setRotate(0);
    }


    /*
     * MazeTabsController calls this when selecting
     * Maze 1 or Maze 2.
     */
    public void setMaze(
            String mazeFile) {

        maze = new Image(

                getClass()
                        .getResource(
                                mazeFile
                        )
                        .toExternalForm()
        );


        mazeView.setImage(
                maze
        );


        maze2 =
                mazeFile.equals(
                        "maze2.png"
                );


        /*
         * Keep both buttons underneath whichever maze
         * is currently displayed.
         */
        automaticButton.setLayoutY(
                maze.getHeight() + 10
        );

        resetButton.setLayoutY(
                maze.getHeight() + 10
        );


        /*
         * Give the pane enough vertical room for
         * both the maze and the buttons.
         */
        mainPane.setPrefHeight(
                maze.getHeight() + 55
        );
    }
}