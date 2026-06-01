package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ScorePopup {
    private final double x;
    private double y;
    private final String text;
    private double life = 0.75;

    public ScorePopup(double x, double y, int score) {
        this(x, y, "+" + score);
    }

    public ScorePopup(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public void update(double delta) {
        life -= delta;
        y -= 34 * delta;
    }

    public void draw(GraphicsContext gc) {
        double alpha = Math.max(0, life / 0.75);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        gc.setFill(Color.color(1.0, 0.92, 0.35, alpha));
        gc.fillText(text, x, y);
    }

    public boolean isFinished() {
        return life <= 0;
    }
}
