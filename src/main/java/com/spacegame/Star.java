package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class Star {

    private double x, y, speed, size;
    private static final Random rand = new Random();

    public Star() {
        reset(rand.nextDouble() * MainApp.HEIGHT);
    }

    private void reset(double startY) {
        x = rand.nextDouble() * MainApp.WIDTH;
        y = startY;
        speed = 30 + rand.nextDouble() * 60;
        size = 1 + rand.nextDouble() * 2;
    }

    public void update(double delta) {
        y += speed * delta;
        if (y > MainApp.HEIGHT) reset(0);
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, size, size);
    }
}
