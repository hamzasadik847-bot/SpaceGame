package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PowerUp {

    public enum Type {
        SHIELD, DOUBLE_SHOT, REPAIR
    }

    private final Type type;
    private double x, y;
    private final double size = 28;
    private final double speed;
    private boolean active = true;
    private double spin = 0;

    public PowerUp(double x, double y, Type type, double speed) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.speed = speed;
    }

    public void update(double delta) {
        y += speed * delta;
        spin += delta * 4;
        if (y > MainApp.HEIGHT + size) active = false;
    }

    public void draw(GraphicsContext gc) {
        Color mainColor = switch (type) {
            case SHIELD -> Color.CYAN;
            case DOUBLE_SHOT -> Color.LIME;
            case REPAIR -> Color.HOTPINK;
        };
        String label = switch (type) {
            case SHIELD -> "S";
            case DOUBLE_SHOT -> "2";
            case REPAIR -> "+";
        };

        double glow = 4 + Math.sin(spin) * 2;
        gc.setFill(Color.color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 0.25));
        gc.fillOval(x - glow, y - glow, size + glow * 2, size + glow * 2);

        gc.setFill(Color.color(0.05, 0.05, 0.12, 0.92));
        gc.fillRoundRect(x, y, size, size, 10, 10);
        gc.setStroke(mainColor);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, size, size, 10, 10);

        gc.setFill(mainColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText(label, x + 8, y + 20);
    }

    public boolean intersects(double ox, double oy, double ow, double oh) {
        return x < ox + ow && x + size > ox && y < oy + oh && y + size > oy;
    }

    public Type getType() { return type; }
    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
}
