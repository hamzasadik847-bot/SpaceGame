package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Explosion {

    private double x, y;
    private double radius = 5;
    private double maxRadius;
    private double duration;
    private double elapsed = 0;
    private boolean finished = false;

    public Explosion(double x, double y, double maxRadius) {
        this.x = x;
        this.y = y;
        this.maxRadius = maxRadius;
        this.duration = 0.4;
    }

    public void update(double delta) {
        elapsed += delta;
        radius = (elapsed / duration) * maxRadius;
        if (elapsed >= duration) finished = true;
    }

    public void draw(GraphicsContext gc) {
        double alpha = 1.0 - (elapsed / duration);
        gc.setFill(Color.color(1.0, 0.5, 0.0, alpha));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setFill(Color.color(1.0, 1.0, 0.0, alpha * 0.7));
        gc.fillOval(x - radius * 0.6, y - radius * 0.6, radius * 1.2, radius * 1.2);
    }

    public boolean isFinished() { return finished; }
}
