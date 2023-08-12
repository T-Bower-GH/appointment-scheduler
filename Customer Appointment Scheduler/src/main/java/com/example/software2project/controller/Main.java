package com.example.software2project.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Loads and displays Login form
     * @param loginStage Login form
     * @throws Exception Login form unable to load
     */
    @Override
    public void start(Stage loginStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/software2project/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1077, 378);
        loginStage.setTitle(" ");
        loginStage.setScene(scene);
        loginStage.show();
    }
}
