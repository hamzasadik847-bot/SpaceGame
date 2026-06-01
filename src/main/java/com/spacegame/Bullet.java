package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {

    private double x, y;
    private final double speed = 400;
    private final double width = 6;
    private final double height = 16;
    private boolean active = true;

    public Bullet(double x, double y) {
        this.x = x - width / 2;
        this.y = y;
    }

    public void update(double delta) {
        y -= speed * delta;
        if (y + height < 0) active = false;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.LIME);
        gc.fillRoundRect(x, y, width, height, 4, 4);
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 1, y, width - 2, 6);
    }

    public boolean intersects(double ox, double oy, double ow, double oh) {
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
    }

    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
    public double getX() { return x; }
    public double getY() { return y; }
}
