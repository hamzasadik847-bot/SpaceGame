package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bomb {

    private double x, y;
    private final double speed;
    private final double velocityX;
    private final double width = 12;
    private final double height = 20;
    private boolean active = true;

    public Bomb(double x, double y, double speed) {
        this(x, y, speed, 0);
    }

    public Bomb(double x, double y, double speed, double velocityX) {
        this.x = x - width / 2;
        this.y = y;
        this.speed = speed;
        this.velocityX = velocityX;
    }

    public void update(double delta) {
        x += velocityX * delta;
        y += speed * delta;
        if (y > MainApp.HEIGHT || x < -width || x > MainApp.WIDTH + width) active = false;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
        gc.setFill(Color.ORANGE);
        gc.fillOval(x + 2, y + 2, width - 4, height - 8);
    }

    public boolean intersects(double ox, double oy, double ow, double oh) {
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
    }

    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
