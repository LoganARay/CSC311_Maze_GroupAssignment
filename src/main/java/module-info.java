module edu.farmingdale.csc311_maze_groupassignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens edu.farmingdale.csc311_maze_groupassignment to javafx.fxml;
    exports edu.farmingdale.csc311_maze_groupassignment;
}