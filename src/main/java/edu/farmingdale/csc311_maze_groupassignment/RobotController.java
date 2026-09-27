package edu.farmingdale.csc311_maze_groupassignment;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class RobotController {

    // The robot is approximately 26 x 26 pixels.
    private static final int ROBOT_SIZE = 26;

    // Robot manual movement speed.
    private static final int SPEED = 4;

    /*
     * How quickly the robot moves during automatic mode.
     *
     * Smaller number = faster animation.
     */
    private static final double ANIMATION_SPEED = 5;

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
    private Button automaticButton;

    @FXML
    private Button resetButton;

    private double startX;
    private double startY;

    // Used so the controller knows which maze is currently loaded.
    private boolean maze2 = false;

    // Stores the currently running automatic animation.
    private Timeline automaticTimeline;


    @FXML
    public void initialize() {

        // Save the robot's initial position from robot.fxml.
        // This makes Reset work even before the Robot button is pressed.
        startX = robotView.getLayoutX();
        startY = robotView.getLayoutY();

        /*
         * Keep the existing manual controls working.
         */
        mainPane.setFocusTraversable(true);

        javafx.application.Platform.runLater(mainPane::requestFocus);

        mainPane.setOnMouseClicked(event ->
                mainPane.requestFocus()
        );

        mainPane.setOnKeyPressed(event -> {

            PixelReader m = maze.getPixelReader();

            // Move up
            if (event.getCode() == KeyCode.UP) {

                if (isPath(
                        m,
                        (int) robotView.getLayoutX(),
                        (int) robotView.getLayoutY() - SPEED)

                        &&

                        isPath(
                                m,
                                (int) robotView.getLayoutX() + 25,
                                (int) robotView.getLayoutY() - SPEED)) {

                    robotView.setLayoutY(
                            robotView.getLayoutY() - SPEED
                    );
                }
            }

            // Move down
            if (event.getCode() == KeyCode.DOWN) {

                if (isPath(
                        m,
                        (int) robotView.getLayoutX(),
                        (int) robotView.getLayoutY() + 25 + SPEED)

                        &&

                        isPath(
                                m,
                                (int) robotView.getLayoutX() + 25,
                                (int) robotView.getLayoutY() + 25 + SPEED)) {

                    robotView.setLayoutY(
                            robotView.getLayoutY() + SPEED
                    );
                }
            }

            // Move left
            if (event.getCode() == KeyCode.LEFT) {

                if (isPath(
                        m,
                        (int) robotView.getLayoutX() - SPEED,
                        (int) robotView.getLayoutY())

                        &&

                        isPath(
                                m,
                                (int) robotView.getLayoutX() - SPEED,
                                (int) robotView.getLayoutY() + 25)) {

                    robotView.setLayoutX(
                            robotView.getLayoutX() - SPEED
                    );
                }
            }

            // Move right
            if (event.getCode() == KeyCode.RIGHT) {

                if (isPath(
                        m,
                        (int) robotView.getLayoutX() + 25 + SPEED,
                        (int) robotView.getLayoutY())

                        &&

                        isPath(
                                m,
                                (int) robotView.getLayoutX() + 25 + SPEED,
                                (int) robotView.getLayoutY() + 25)) {

                    robotView.setLayoutX(
                            robotView.getLayoutX() + SPEED
                    );
                }
            }

            if (event.getCode().isArrowKey()) {
                event.consume();
            }
        });
    }


    /*
     * Called when the green Automatic button is clicked.
     */
    @FXML
    private void playAutomatic() {

        // Do nothing if a maze has not been loaded yet.
        if (maze == null) {
            return;
        }

        /*
         * Stop the previous animation if the button somehow gets
         * pressed again while another animation is running.
         */
        if (automaticTimeline != null) {
            automaticTimeline.stop();
        }

        /*
         * Find a path from the robot's current position
         * to the end of the maze.
         */
        List<int[]> path = findAutomaticPath();

        if (path.isEmpty()) {
            System.out.println("No automatic path could be found.");
            return;
        }

        /*
         * Disable the button while the robot is moving.
         */
        automaticButton.setDisable(true);

        automaticTimeline = new Timeline();

        /*
         * The path contains every individual pixel position.
         *
         * We skip every other point when creating animation frames.
         * This keeps the movement smooth without creating an
         * unnecessarily huge number of KeyFrames.
         */
        int frame = 0;

        for (int i = 1; i < path.size(); i += 2) {

            int[] position = path.get(i);

            int x = position[0];
            int y = position[1];

            /*
             * Make local copies for this particular KeyFrame.
             */
            final int targetX = x;
            final int targetY = y;

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(frame * ANIMATION_SPEED),

                    event -> {
                        robotView.setLayoutX(targetX);
                        robotView.setLayoutY(targetY);
                    }
            );

            automaticTimeline.getKeyFrames().add(keyFrame);

            frame++;
        }

        /*
         * Make sure the robot finishes at the exact final position,
         * even though we skipped some points above.
         */
        int[] finalPosition =
                path.get(path.size() - 1);

        automaticTimeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.millis(frame * ANIMATION_SPEED),

                        event -> {
                            robotView.setLayoutX(finalPosition[0]);
                            robotView.setLayoutY(finalPosition[1]);
                        }
                )
        );

        /*
         * Re-enable the Automatic button when the animation ends.
         */
        automaticTimeline.setOnFinished(event -> {

            automaticButton.setDisable(false);

            mainPane.requestFocus();
        });

        automaticTimeline.play();
    }


    /*
     * Puts the robot back at the original starting position.
     */
    @FXML
    private void resetRobot() {

        // Stop the automatic animation if it is currently running.
        if (automaticTimeline != null) {
            automaticTimeline.stop();
        }

        // Return the robot to the beginning of the maze.
        robotView.setLayoutX(startX);
        robotView.setLayoutY(startY);

        // Make sure Automatic can be pressed again.
        automaticButton.setDisable(false);

        // Give keyboard control back to the maze.
        mainPane.requestFocus();
    }


    /*
     * Finds a valid route through the maze.
     *
     * This uses Breadth-First Search (BFS).
     *
     * BFS tries nearby positions until it finds the destination.
     * Because movement only happens through positions that pass
     * canRobotStandAt(), it will not intentionally travel through
     * a maze wall.
     */
    private List<int[]> findAutomaticPath() {

        PixelReader pixelReader =
                maze.getPixelReader();

        int width =
                (int) maze.getWidth();

        int height =
                (int) maze.getHeight();

        /*
         * Current robot position becomes the BFS starting point.
         */
        int startX =
                (int) Math.round(robotView.getLayoutX());

        int startY =
                (int) Math.round(robotView.getLayoutY());

        /*
         * Each possible top-left robot position can be represented
         * by one integer:
         *
         * ID = y * width + x
         */
        int totalPositions =
                width * height;

        /*
         * previous[position] remembers which position was visited
         * immediately before this one.
         *
         * -2 = not visited yet
         * -1 = starting position
         */
        int[] previous =
                new int[totalPositions];

        Arrays.fill(previous, -2);

        Queue<Integer> queue =
                new ArrayDeque<>();

        int startId =
                startY * width + startX;

        previous[startId] = -1;

        queue.add(startId);

        int goalId = -1;

        /*
         * Possible movement directions:
         *
         * right, left, down, up
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
             * Check whether this position has reached the maze goal.
             */
            if (isGoal(pixelReader, x, y)) {

                goalId = currentId;

                break;
            }

            /*
             * Try moving one pixel in each direction.
             */
            for (int[] direction : directions) {

                int nextX =
                        x + direction[0];

                int nextY =
                        y + direction[1];

                /*
                 * The robot's full rectangle must remain
                 * inside the image.
                 */
                if (nextX < 0
                        || nextY < 0
                        || nextX + ROBOT_SIZE > width
                        || nextY + ROBOT_SIZE > height) {

                    continue;
                }

                int nextId =
                        nextY * width + nextX;

                /*
                 * Skip positions we already checked.
                 */
                if (previous[nextId] != -2) {
                    continue;
                }

                /*
                 * Only use this position if the entire robot
                 * can fit there without hitting a wall.
                 */
                if (!canRobotStandAt(
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
         * No route was found.
         */
        if (goalId == -1) {
            return Collections.emptyList();
        }


        /*
         * Work backward from the goal to reconstruct the path.
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
                    new int[]{x, y}
            );

            current =
                    previous[current];
        }

        /*
         * We reconstructed it backward:
         *
         * end -> start
         *
         * Reverse it so that it becomes:
         *
         * start -> end
         */
        Collections.reverse(path);

        return path;
    }


    /*
     * Checks whether the robot's entire 26 x 26 area
     * is allowed at a position.
     *
     * We test all four corners.
     */
    private boolean canRobotStandAt(
            PixelReader pixelReader,
            int x,
            int y) {

        return isPath(
                pixelReader,
                x,
                y)

                &&

                isPath(
                        pixelReader,
                        x + ROBOT_SIZE - 1,
                        y)

                &&

                isPath(
                        pixelReader,
                        x,
                        y + ROBOT_SIZE - 1)

                &&

                isPath(
                        pixelReader,
                        x + ROBOT_SIZE - 1,
                        y + ROBOT_SIZE - 1);
    }


    /*
     * Determines whether a BFS position is the destination.
     */
    private boolean isGoal(
            PixelReader pixelReader,
            int x,
            int y) {

        /*
         * MAZE 2:
         *
         * The destination is the purple square.
         */
        if (maze2) {

            int centerX =
                    x + ROBOT_SIZE / 2;

            int centerY =
                    y + ROBOT_SIZE / 2;

            Color centerColor =
                    pixelReader.getColor(
                            centerX,
                            centerY
                    );

            return isPurple(centerColor);
        }


        /*
         * MAZE 1:
         *
         * Maze 1 begins at the opening on the left
         * and exits on the right.
         *
         * Once the robot reaches very close to the
         * right side, it has reached the exit.
         */
        return x + ROBOT_SIZE
                >= maze.getWidth() - 10;
    }


    /*
     * Identifies the purple goal square in Maze 2.
     */
    private boolean isPurple(Color color) {

        return color.getBlue() > 0.55
                && color.getRed() > 0.25
                && color.getRed() < 0.75
                && color.getGreen() < 0.35;
    }


    /*
     * Existing method used by MazeTabsController
     * to place the robot at the correct entrance.
     */
    public void setStartPosition(
            double x,
            double y) {

        // Save the beginning of this maze.
        startX = x;
        startY = y;

        // Put the robot at the beginning.
        robotView.setLayoutX(startX);
        robotView.setLayoutY(startY);
    }


    /*
     * Existing method used to change between Maze 1 and Maze 2.
     */
    public void setMaze(String mazeFile) {

        maze = new Image(
                getClass()
                        .getResource(mazeFile)
                        .toExternalForm()
        );

        mazeView.setImage(maze);

        maze2 =
                mazeFile.equals("maze2.png");


        /*
         * Keep the buttons directly underneath whichever
         * maze image is currently being displayed.
         */
        automaticButton.setLayoutY(
                maze.getHeight() + 10
        );

        resetButton.setLayoutY(
                maze.getHeight() + 10
        );

        /*
         * Make enough room in the AnchorPane for the maze
         * plus the buttons.
         */
        mainPane.setPrefHeight(
                maze.getHeight() + 55
        );
    }


    /*
     * Existing path-detection method.
     */
    private boolean isPath(
            PixelReader pixelReader,
            int x,
            int y) {

        if (x < 0
                || y < 0
                || x >= maze.getWidth()
                || y >= maze.getHeight()) {

            return false;
        }

        Color pixelColor =
                pixelReader.getColor(x, y);

        /*
         * Maze 2 considers anything that is not a blue
         * wall to be a legal space.
         */
        if (maze2) {
            return !isBlueWall(pixelColor);
        }

        /*
         * Maze 1 uses the white background/path color.
         */
        return pixelColor.equals(
                pixelReader.getColor(0, 0)
        );
    }


    /*
     * Existing Maze 2 blue-wall test.
     */
    private boolean isBlueWall(Color color) {

        return color.getBlue() > 0.7
                && color.getRed() < 0.3
                && color.getGreen() < 0.5;
    }
}