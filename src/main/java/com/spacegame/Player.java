package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {

    private double x, y;
    private final double width = 50;
    private final double height = 60;

    private static final int MAX_ENERGY = 100;
    private int energy;
    private double shieldTime = 0;
    private double doubleShotTime = 0;
    private double flamePulse = 0;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.energy = MAX_ENERGY;
    }

    public void update(double delta) {
        flamePulse += delta * 8;
        if (shieldTime > 0) shieldTime -= delta;
        if (doubleShotTime > 0) doubleShotTime -= delta;
    }

    public void draw(GraphicsContext gc) {
        if (hasShield()) {
            gc.setStroke(Color.color(0.2, 0.9, 1.0, 0.75));
            gc.setLineWidth(3);
            gc.strokeOval(x - 10, y - 8, width + 20, height + 22);
        }

        // Ailes
        gc.setFill(Color.rgb(0, 180, 255));
        gc.fillPolygon(
                new double[]{x + 6, x - 10, x + 16},
                new double[]{y + 36, y + 55, y + 54},
                3
        );
        gc.fillPolygon(
                new double[]{x + width - 6, x + width + 10, x + width - 16},
                new double[]{y + 36, y + 55, y + 54},
                3
        );

        // Corps de la fusee
        gc.setFill(Color.WHITE);
        double[] xPoints = {x + width / 2, x, x + width};
        double[] yPoints = {y, y + height, y + height};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Nez
        gc.setFill(Color.RED);
        gc.fillPolygon(
                new double[]{x + width / 2, x + 17, x + width - 17},
                new double[]{y, y + 22, y + 22},
                3
        );

        // Hublot
        gc.setFill(Color.CYAN);
        gc.fillOval(x + width / 2 - 8, y + height / 2 - 5, 16, 16);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x + width / 2 - 8, y + height / 2 - 5, 16, 16);

        // Flamme moteur animee
        double pulse = 6 + Math.sin(flamePulse) * 3;
        gc.setFill(Color.ORANGE);
        gc.fillOval(x + width / 2 - 12, y + height - 4, 24, 15 + pulse);
        gc.setFill(Color.YELLOW);
        gc.fillOval(x + width / 2 - 7, y + height, 14, 10 + pulse);
    }

    public void moveLeft(double speed) {
        x = Math.max(0, x - speed);
    }

    public void moveRight(double speed) {
        x = Math.min(MainApp.WIDTH - width, x + speed);
    }

    public boolean intersects(double ox, double oy, double ow, double oh) {
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
    }

    public boolean takeDamage(int amount) {
        if (hasShield()) {
            shieldTime = 0;
            return false;
        }
        energy = Math.max(0, energy - amount);
        return true;
    }

    public void heal(int amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    public void activateShield(double seconds) {
        shieldTime = Math.max(shieldTime, seconds);
    }

    public void activateDoubleShot(double seconds) {
        doubleShotTime = Math.max(doubleShotTime, seconds);
    }

    public boolean hasShield() { return shieldTime > 0; }
    public boolean hasDoubleShot() { return doubleShotTime > 0; }
    public boolean isDead() { return energy <= 0; }

    public int getEnergy() { return energy; }
    public double getEnergyPercent() { return energy / (double) MAX_ENERGY; }
    public double getShieldTime() { return Math.max(0, shieldTime); }
    public double getDoubleShotTime() { return Math.max(0, doubleShotTime); }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getCenterX() { return x + width / 2; }
}
