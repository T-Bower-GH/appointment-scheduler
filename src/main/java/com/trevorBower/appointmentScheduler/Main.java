package com.trevorBower.appointmentScheduler;

import com.trevorBower.appointmentScheduler.helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;


public class Main extends Application {

    /**
     * Opens login form
     * @param primaryStage Stage for the login form
     * @throws Exception If login form file not found
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxmlPath = "/forms/Login.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("User Login");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    /**
     * Connects to SQL database, launches GUI and closes DB connection when GUI is closed
     * @param args Main
     * @throws SQLException If cannot connect to SQL database
     */
    public static void main(String[] args) throws SQLException {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
