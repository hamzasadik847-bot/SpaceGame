package com.spacegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Random;

public class Enemy {

    // OpenGameArt: "Shoot-em-up Enemies" by helpcomputer.
    // Used under CC-BY 3.0 option with credit in README_JEF.md.
    private static final String ENEMY_SHEET_RESOURCE = "/assets/enemies.png";
    private static Image enemySheet;
    private static boolean triedLoadingSheet = false;

    public enum Type {
        SCOUT("Scout Drone", "fast zig-zag"),
        MANTA("Shield Manta", "wide shield body"),
        HORNET("Hornet", "fast hunter"),
        CRAB("Crab Bomber", "triple bomb spread"),
        REAPER("Void Reaper", "erratic movement"),
        TANK("Iron Tank", "heavy armor"),
        BOSS("Mega Core", "boss bomb storm");

        private final String displayName;
        private final String abilityName;

        Type(String displayName, String abilityName) {
            this.displayName = displayName;
            this.abilityName = abilityName;
        }

        public String getDisplayName() { return displayName; }
        public String getAbilityName() { return abilityName; }
    }

    private double x, y;
    private double speedX;
    private double width;
    private double height;
    private boolean alive = true;
    private static final Random rand = new Random();

    private final Type type;
    private final int level;
    private int health;
    private int maxHealth;
    private int reward;
    private double bombChance;
    private int bombShots;
    private double bombSpeedMultiplier;

    private final double baseY;
    private double waveAmplitude;
    private double waveSpeed;
    private double phase;

    private double bombCooldown;
    private double bombTimer = 0;
    private double hitFlash = 0;

    public Enemy(double x, double y, double speedX, double bombCooldown) {
        this(x, y, speedX, bombCooldown, Type.SCOUT, 1);
    }

    public Enemy(double x, double y, double speedX, double bombCooldown, Type type, int level) {
        this.x = x;
        this.y = y;
        this.baseY = y;
        this.speedX = speedX;
        this.bombCooldown = bombCooldown;
        this.type = type;
        this.level = level;
        this.phase = rand.nextDouble() * Math.PI * 2;
        configureStats();
    }

    private void configureStats() {
        switch (type) {
            case SCOUT -> {
                width = 48;
                height = 54;
                maxHealth = 1 + level / 5;
                reward = 10 * level;
                bombChance = 0.18;
                bombShots = 1;
                bombSpeedMultiplier = 1.0;
                waveAmplitude = 16 + rand.nextDouble() * 10;
                waveSpeed = 2.2 + rand.nextDouble() * 1.4;
            }
            case MANTA -> {
                width = 76;
                height = 82;
                maxHealth = 3 + level / 3;
                reward = 24 * level;
                bombChance = 0.48;
                bombShots = 1;
                bombSpeedMultiplier = 0.85;
                waveAmplitude = 7 + rand.nextDouble() * 6;
                waveSpeed = 1.0 + rand.nextDouble() * 0.8;
                speedX *= 0.75;
                bombCooldown *= 1.10;
            }
            case HORNET -> {
                width = 62;
                height = 58;
                maxHealth = 2 + level / 4;
                reward = 20 * level;
                bombChance = 0.44;
                bombShots = 1;
                bombSpeedMultiplier = 1.35;
                waveAmplitude = 24 + rand.nextDouble() * 12;
                waveSpeed = 2.4 + rand.nextDouble() * 1.1;
                speedX *= 1.18;
                bombCooldown *= 0.78;
            }
            case CRAB -> {
                width = 78;
                height = 64;
                maxHealth = 4 + level / 2;
                reward = 32 * level;
                bombChance = 0.82;
                bombShots = 3;
                bombSpeedMultiplier = 1.12;
                waveAmplitude = 8 + rand.nextDouble() * 5;
                waveSpeed = 1.2 + rand.nextDouble() * 0.6;
                speedX *= 0.72;
                bombCooldown *= 0.95;
            }
            case REAPER -> {
                width = 72;
                height = 102;
                maxHealth = 6 + level;
                reward = 50 * level;
                bombChance = 0.78;
                bombShots = 2;
                bombSpeedMultiplier = 1.45;
                waveAmplitude = 28 + rand.nextDouble() * 12;
                waveSpeed = 2.7 + rand.nextDouble() * 1.0;
                speedX *= 1.10;
                bombCooldown *= 0.70;
            }
            case TANK -> {
                width = 74;
                height = 92;
                maxHealth = 8 + level;
                reward = 58 * level;
                bombChance = 0.56;
                bombShots = 1;
                bombSpeedMultiplier = 0.92;
                waveAmplitude = 4 + rand.nextDouble() * 4;
                waveSpeed = 0.7 + rand.nextDouble() * 0.6;
                speedX *= 0.48;
                bombCooldown *= 1.22;
            }
            case BOSS -> {
                width = 128;
                height = 169;
                maxHealth = 20 + level * 6;
                reward = 180 * level;
                bombChance = 1.0;
                bombShots = 5;
                bombSpeedMultiplier = 1.05;
                waveAmplitude = 10;
                waveSpeed = 1.0;
                speedX *= 0.50;
                bombCooldown *= 0.84;
            }
        }
        bombCooldown *= Math.max(0.72, 1.18 - level * 0.035);
        health = maxHealth;
    }

    public void update(double delta) {
        phase += waveSpeed * delta;
        if (hitFlash > 0) hitFlash -= delta;

        switch (type) {
            case SCOUT -> {
                x += speedX * delta;
                y += Math.sin(phase) * waveAmplitude * delta;
            }
            case MANTA -> {
                x += speedX * delta;
                y += Math.sin(phase) * waveAmplitude * delta * 0.35;
            }
            case HORNET -> {
                x += speedX * delta;
                y += Math.sin(phase) * waveAmplitude * delta * 1.25;
            }
            case CRAB -> {
                x += speedX * delta;
                y += Math.sin(phase) * waveAmplitude * delta * 0.55;
            }
            case REAPER -> {
                x += speedX * delta;
                y += Math.sin(phase * 1.15) * waveAmplitude * delta * 1.15;
            }
            case TANK -> {
                x += speedX * delta;
                y += Math.sin(phase) * waveAmplitude * delta * 0.25;
            }
            case BOSS -> {
                x += speedX * delta;
                y = baseY + Math.sin(phase) * waveAmplitude;
            }
        }

        if (x <= 10 || x + width >= MainApp.WIDTH - 10) {
            speedX = -speedX;
            x = Math.max(10, Math.min(x, MainApp.WIDTH - width - 10));
            if (type != Type.BOSS) {
                y += switch (type) {
                    case TANK, MANTA -> 12;
                    case REAPER -> 16;
                    default -> 22;
                };
            }
        }

        if (type != Type.BOSS && y < baseY - 34) y = baseY - 34;
        bombTimer += delta;
    }

    public boolean shouldDropBomb() {
        if (bombTimer >= bombCooldown) {
            bombTimer = type == Type.BOSS ? 0 : rand.nextDouble() * bombCooldown * 0.45;
            return rand.nextDouble() < bombChance;
        }
        return false;
    }

    public void draw(GraphicsContext gc) {
        if (!drawOpenGameArtSprite(gc)) {
            drawFallback(gc);
        }
        if (hitFlash > 0) {
            double alpha = Math.min(0.55, hitFlash / 0.12);
            gc.setFill(Color.color(1, 1, 1, alpha));
            gc.fillOval(x - 4, y - 4, width + 8, height + 8);
        }
        drawHealthBar(gc);
    }

    private boolean drawOpenGameArtSprite(GraphicsContext gc) {
        Image sheet = getEnemySheet();
        if (sheet == null || sheet.isError() || sheet.getProgress() < 1.0) return false;

        double[] source = switch (type) {
            case SCOUT -> new double[]{173, 0, 55, 50};
            case MANTA -> new double[]{159, 54, 67, 71};
            case HORNET -> new double[]{163, 222, 61, 55};
            case CRAB -> new double[]{158, 290, 70, 58};
            case REAPER -> new double[]{381, 368, 59, 78};
            case TANK -> new double[]{255, 418, 70, 90};
            case BOSS -> new double[]{273, 6, 133, 177};
        };

        Color glowColor = switch (type) {
            case SCOUT -> Color.color(0.0, 0.7, 1.0, 0.16);
            case MANTA -> Color.color(0.0, 1.0, 0.7, 0.16);
            case HORNET -> Color.color(0.8, 0.8, 0.1, 0.18);
            case CRAB -> Color.color(1.0, 0.35, 0.1, 0.18);
            case REAPER -> Color.color(0.75, 0.0, 1.0, 0.22);
            case TANK -> Color.color(0.3, 1.0, 0.1, 0.18);
            case BOSS -> Color.color(0.35, 1.0, 0.1, 0.22);
        };
        double glow = type == Type.BOSS ? 26 : 12;
        gc.setFill(glowColor);
        gc.fillOval(x - glow / 2, y - glow / 3, width + glow, height + glow);
        gc.drawImage(sheet, source[0], source[1], source[2], source[3], x, y, width, height);
        return true;
    }

    private static Image getEnemySheet() {
        if (!triedLoadingSheet) {
            triedLoadingSheet = true;
            try {
                var stream = Enemy.class.getResourceAsStream(ENEMY_SHEET_RESOURCE);
                if (stream != null) enemySheet = new Image(stream);
            } catch (Exception ignored) {
                enemySheet = null;
            }
        }
        return enemySheet;
    }

    private void drawFallback(GraphicsContext gc) {
        Color fill = switch (type) {
            case SCOUT -> Color.DODGERBLUE;
            case MANTA -> Color.SEAGREEN;
            case HORNET -> Color.OLIVE;
            case CRAB -> Color.ORANGERED;
            case REAPER -> Color.MEDIUMPURPLE;
            case TANK -> Color.LIMEGREEN;
            case BOSS -> Color.DARKOLIVEGREEN;
        };
        Color stroke = switch (type) {
            case REAPER -> Color.HOTPINK;
            case BOSS -> Color.LIME;
            default -> Color.CYAN;
        };
        gc.setFill(Color.color(fill.getRed(), fill.getGreen(), fill.getBlue(), 0.22));
        gc.fillOval(x - 8, y - 6, width + 16, height + 12);
        gc.setFill(fill);
        gc.fillRoundRect(x, y + height * 0.20, width, height * 0.72, 18, 18);
        gc.setStroke(stroke);
        gc.setLineWidth(type == Type.BOSS ? 3 : 2);
        gc.strokeRoundRect(x, y + height * 0.20, width, height * 0.72, 18, 18);
        gc.setFill(Color.HOTPINK);
        gc.fillOval(x + width * 0.30, y + height * 0.38, 7, 7);
        gc.fillOval(x + width * 0.62, y + height * 0.38, 7, 7);
    }

    private void drawHealthBar(GraphicsContext gc) {
        double barWidth = Math.min(width, type == Type.BOSS ? 112 : 38);
        double barHeight = type == Type.BOSS ? 6 : 3;
        double barX = x + (width - barWidth) / 2;
        double barY = y - (type == Type.BOSS ? 12 : 8);
        double percent = Math.max(0, health / (double) maxHealth);

        gc.setFill(Color.color(0, 0, 0, 0.58));
        gc.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);
        gc.setFill(percent > 0.45 ? Color.color(0.45, 0.95, 0.35, 0.82) : Color.color(1.0, 0.25, 0.12, 0.82));
        gc.fillRoundRect(barX, barY, barWidth * percent, barHeight, 4, 4);
        gc.setStroke(Color.color(1, 1, 1, 0.75));
        gc.setLineWidth(0.7);
        gc.strokeRoundRect(barX, barY, barWidth, barHeight, 4, 4);

        if (type == Type.BOSS || type == Type.TANK || maxHealth > 6) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, type == Type.BOSS ? 12 : 9));
            gc.setFill(Color.WHITE);
            gc.fillText(health + "/" + maxHealth, barX + barWidth / 2 - 14, barY - 2);
        }
    }

    public boolean intersects(double ox, double oy, double ow, double oh) {
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
    }

    public boolean takeDamage(int amount) {
        if (!alive) return false;
        hitFlash = 0.12;
        health = Math.max(0, health - amount);
        if (health <= 0) {
            kill();
            return true;
        }
        return false;
    }

    public void kill() { alive = false; }
    public boolean isAlive() { return alive; }
    public Type getType() { return type; }
    public String getDisplayName() { return type.getDisplayName(); }
    public String getAbilityName() { return type.getAbilityName(); }
    public int getBombShots() { return bombShots; }
    public double getBombSpeedMultiplier() { return bombSpeedMultiplier; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getReward() { return reward; }
    public boolean isSmallEnemy() { return type == Type.SCOUT || type == Type.HORNET; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getCenterX() { return x + width / 2; }
    public double getBottomY() { return y + height; }
}
