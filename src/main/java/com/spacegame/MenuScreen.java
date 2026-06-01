package com.spacegame;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuScreen {

    private final VBox root;
    private final Stage stage;

    public MenuScreen(Stage stage) {
        this.stage = stage;

        root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #050512, #0b1038, #13051f);" +
                "-fx-padding: 40;");

        Label title = new Label("NEON SPACE DEFENDER");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 46));
        title.setTextFill(Color.CYAN);

        Label subtitle = new Label("Survis aux vagues, ramasse les power-ups et protege la Terre.");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.LIGHTGRAY);

        Label controls = new Label("Controle : Q/D ou Fleches pour bouger  |  Espace tirer  |  P pause");
        controls.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        controls.setTextFill(Color.web("#8FFBFF"));

        Button btnPlay = createButton("JOUER", "#00cc66");
        Button btnQuit = createButton("QUITTER", "#cc2244");

        btnPlay.setOnAction(e -> startGame(1));
        btnQuit.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, subtitle, controls, btnPlay, btnQuit);
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-padding: 12 48; -fx-background-radius: 18; -fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 255, 255, 0.35), 16, 0.4, 0, 0);");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.82));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    public void startGame(int level) {
        GameScreen game = new GameScreen(stage, level);
        Scene scene = new Scene(game.getRoot(), MainApp.WIDTH, MainApp.HEIGHT);
        game.setScene(scene);
        stage.setScene(scene);
        game.start();
    }

    public Parent getRoot() {
        return root;
    }
}
