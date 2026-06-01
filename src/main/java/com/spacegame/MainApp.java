package com.spacegame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        MenuScreen menu = new MenuScreen(stage);
        Scene scene = new Scene(menu.getRoot(), WIDTH, HEIGHT);
        stage.setTitle("Space Defender");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
