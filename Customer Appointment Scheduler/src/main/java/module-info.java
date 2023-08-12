module com.example.software2project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.software2project to javafx.fxml;
    exports com.example.software2project.controller;
}