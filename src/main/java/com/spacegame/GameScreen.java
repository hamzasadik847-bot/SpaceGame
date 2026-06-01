package com.spacegame;

import javafx.animation.AnimationTimer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameScreen {
    private final Stage stage;
    private final int level;
    private Scene scene;
    private final Pane root;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private Player player;
    private Enemy selectedEnemy;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final List<ScorePopup> scorePopups = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Random random = new Random();
    private int score = 0;
    private boolean gameOver = false;
    private boolean victory = false;
    private boolean paused = false;
    private boolean overlayListenerSet = false;
    private double gameTimer = 0;
    private double powerUpTimer = 5.5;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private double shootCooldown = 0;
    private AnimationTimer timer;
    private long lastTime = 0;
    private int currentWave = 0;
    private int totalWaves;
    private double nextWaveTimer = 0;
    private String levelMessage = "";
    private double levelMessageTimer = 3.0;
    private double screenShake = 0;
    private int bossPattern = 0;
    private double playerRedFlash = 0;
    private double shipDestroyedTimer = -1;
    private boolean victorySoundPlayed = false;

    public GameScreen(Stage stage, int level) {
        this.stage = stage;
        this.level = level;
        canvas = new Canvas(MainApp.WIDTH, MainApp.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root = new Pane(canvas);
        for (int i = 0; i < 120; i++) stars.add(new Star());
        player = new Player(MainApp.WIDTH / 2.0 - 25, MainApp.HEIGHT - 90);
        totalWaves = level >= 3 ? 3 : 2;
        levelMessage = "LEVEL START - " + introducedMonsterText();
        spawnWave(1);
    }

    private void spawnWave(int wave) {
        enemies.clear();
        bombs.clear();
        currentWave = wave;
        nextWaveTimer = 0;
        bossPattern = 0;
        double speed = enemySpeed();
        double cooldown = bombCooldown();
        List<Enemy.Type> formation = waveFormation(wave);
        if (formation.contains(Enemy.Type.BOSS)) {
            enemies.add(new Enemy(84, 132, speed, cooldown, Enemy.Type.SCOUT, level));
            enemies.add(new Enemy(210, 152, -speed, cooldown, Enemy.Type.MANTA, level));
            enemies.add(new Enemy(MainApp.WIDTH / 2.0 - 64, 106, speed * 0.7, cooldown, Enemy.Type.BOSS, level));
            enemies.add(new Enemy(558, 152, -speed, cooldown, Enemy.Type.CRAB, level));
            enemies.add(new Enemy(696, 132, speed, cooldown, Enemy.Type.HORNET, level));
            return;
        }
        double spacing = formation.size() <= 5 ? 128 : 96;
        double startX = (MainApp.WIDTH - spacing * (formation.size() - 1)) / 2.0;
        for (int i = 0; i < formation.size(); i++) {
            Enemy.Type type = formation.get(i);
            double y = 70 + (i % 2) * 64 + (wave - 1) * 12;
            if (type == Enemy.Type.BOSS) y = 48;
            double direction = i % 2 == 0 ? 1 : -1;
            enemies.add(new Enemy(startX + i * spacing, y, speed * direction, cooldown, type, level));
        }
    }

    private List<Enemy.Type> waveFormation(int wave) {
        if (level == 1) {
            return wave == 1
                    ? List.of(Enemy.Type.SCOUT, Enemy.Type.HORNET, Enemy.Type.SCOUT, Enemy.Type.HORNET, Enemy.Type.SCOUT)
                    : List.of(Enemy.Type.SCOUT, Enemy.Type.HORNET, Enemy.Type.SCOUT, Enemy.Type.HORNET, Enemy.Type.SCOUT);
        }
        if (level == 2) {
            return wave == 1
                    ? List.of(Enemy.Type.SCOUT, Enemy.Type.MANTA, Enemy.Type.HORNET, Enemy.Type.SCOUT)
                    : List.of(Enemy.Type.HORNET, Enemy.Type.CRAB, Enemy.Type.MANTA, Enemy.Type.SCOUT, Enemy.Type.HORNET);
        }
        if (wave == totalWaves) {
            return List.of(Enemy.Type.SCOUT, Enemy.Type.MANTA, Enemy.Type.BOSS, Enemy.Type.CRAB, Enemy.Type.HORNET);
        }
        if (wave == 1) return List.of(Enemy.Type.SCOUT, Enemy.Type.HORNET, Enemy.Type.REAPER, Enemy.Type.SCOUT, Enemy.Type.MANTA);
        return List.of(Enemy.Type.HORNET, Enemy.Type.CRAB, Enemy.Type.TANK, Enemy.Type.REAPER, Enemy.Type.MANTA, Enemy.Type.SCOUT);
    }

    private double enemySpeed() {
        return Math.min(150, 58 + level * 12);
    }

    private double bombCooldown() {
        return Math.max(1.25, 4.25 - level * 0.24);
    }

    private String introducedMonsterText() {
        if (level == 1) return "Scout Drone and Hornet";
        if (level == 2) return "Shield Manta and Crab Bomber";
        if (level == 3) return "Void Reaper and Mega Core";
        return "Stronger monster waves";
    }

    public void setScene(Scene scene) { this.scene = scene; }

    public void start() {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.Q) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = true;
            if (e.getCode() == KeyCode.SPACE) spacePressed = true;
            if (e.getCode() == KeyCode.P) paused = !paused;
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.Q) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = false;
            if (e.getCode() == KeyCode.SPACE) spacePressed = false;
        });
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (lastTime == 0) { lastTime = now; return; }
                double delta = Math.min(0.05, (now - lastTime) / 1_000_000_000.0);
                lastTime = now;
                update(delta);
                render();
            }
        };
        timer.start();
    }

    private void update(double delta) {
        for (Star s : stars) s.update(delta);
        if (paused || gameOver || victory) return;
        gameTimer += delta;
        if (levelMessageTimer > 0) levelMessageTimer -= delta;
        if (screenShake > 0) screenShake -= delta;
        if (playerRedFlash > 0) playerRedFlash -= delta;
        if (shipDestroyedTimer >= 0) {
            shipDestroyedTimer -= delta;
            updateFeedbackEffects(delta);
            if (shipDestroyedTimer <= 0) {
                gameOver = true;
                SoundEffects.gameOver();
            }
            return;
        }
        player.update(delta);
        if (leftPressed) player.moveLeft(265 * delta);
        if (rightPressed) player.moveRight(265 * delta);
        updateShots(delta);
        updateEnemies(delta);
        updatePowerUps(delta);
        checkCollisions();
        updateFeedbackEffects(delta);
        updateWaves(delta);
        refreshSelectedEnemy();
    }

    private void updateFeedbackEffects(double delta) {
        for (Explosion ex : explosions) ex.update(delta);
        explosions.removeIf(Explosion::isFinished);
        for (ScorePopup popup : scorePopups) popup.update(delta);
        scorePopups.removeIf(ScorePopup::isFinished);
    }

    private void updateShots(double delta) {
        shootCooldown -= delta;
        if (spacePressed && shootCooldown <= 0) {
            if (player.hasDoubleShot()) {
                bullets.add(new Bullet(player.getCenterX() - 12, player.getY() + 4));
                bullets.add(new Bullet(player.getCenterX() + 12, player.getY() + 4));
                SoundEffects.shoot();
                shootCooldown = 0.18;
            } else {
                bullets.add(new Bullet(player.getCenterX(), player.getY()));
                SoundEffects.shoot();
                shootCooldown = 0.28;
            }
        }
        for (Bullet b : bullets) b.update(delta);
        bullets.removeIf(b -> !b.isActive());
    }

    private void updateEnemies(double delta) {
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            e.update(delta);
            if (gameTimer > 2.0 && e.shouldDropBomb()) {
                double baseSpeed = 105 + level * 30;
                double speed = baseSpeed * e.getBombSpeedMultiplier();
                int shots = e.getType() == Enemy.Type.BOSS ? bossBombShots(e) : e.getBombShots();
                for (int i = 0; i < shots; i++) {
                    double spreadIndex = i - (shots - 1) / 2.0;
                    double offset = spreadIndex * (e.getType() == Enemy.Type.BOSS ? 24 : 18);
                    double drift = (e.getType() == Enemy.Type.CRAB || e.getType() == Enemy.Type.BOSS) ? spreadIndex * 38 : 0;
                    bombs.add(new Bomb(e.getCenterX() + offset, e.getBottomY(), speed, drift));
                }
            }
            if (e.getY() > MainApp.HEIGHT) e.kill();
        }
        for (Bomb b : bombs) b.update(delta);
        bombs.removeIf(b -> !b.isActive());
    }

    private int bossBombShots(Enemy boss) {
        double hp = boss.getHealth() / (double) boss.getMaxHealth();
        bossPattern++;
        if (hp < 0.35) return bossPattern % 2 == 0 ? 5 : 3;
        if (hp < 0.70) return bossPattern % 3 == 0 ? 5 : 3;
        return 3;
    }

    private void updateWaves(double delta) {
        if (enemies.stream().anyMatch(Enemy::isAlive)) return;
        if (currentWave >= totalWaves) {
            victory = true;
            if (!victorySoundPlayed) {
                victorySoundPlayed = true;
                SoundEffects.victory();
            }
            return;
        }
        if (nextWaveTimer <= 0) nextWaveTimer = 1.0;
        nextWaveTimer -= delta;
        if (nextWaveTimer <= 0) {
            spawnWave(currentWave + 1);
            levelMessage = "WAVE " + currentWave + " - " + waveHint();
            levelMessageTimer = 1.8;
        }
    }

    private String waveHint() {
        if (level == 1) return "Easy bombs";
        if (level == 2) return "Manta shields and crab spread";
        if (currentWave == totalWaves) return "Mega Core";
        return "Reapers incoming";
    }

    private void updatePowerUps(double delta) {
        powerUpTimer -= delta;
        if (powerUpTimer <= 0) {
            spawnPowerUp();
            powerUpTimer = Math.max(5.0, 9.2 - level * 0.35) + random.nextDouble() * 2.5;
        }
        for (PowerUp p : powerUps) p.update(delta);
        powerUps.removeIf(p -> !p.isActive());
    }

    private void checkCollisions() {
        if (gameTimer <= 1.2) return;
        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) continue;
            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) continue;
                if (bullet.intersects(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight())) {
                    bullet.deactivate();
                    selectedEnemy = enemy;
                    int damage = player.hasDoubleShot() ? 2 : 1;
                    if (isProtectedByManta(enemy)) damage = Math.max(1, damage - 1);
                    boolean defeated = enemy.takeDamage(damage);
                    if (enemy.getType() == Enemy.Type.BOSS) {
                        screenShake = 0.08;
                        SoundEffects.bossHit();
                    }
                    explosions.add(new Explosion(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2, defeated ? explosionSize(enemy) : 15));
                    if (defeated) {
                        score += enemy.getReward();
                        SoundEffects.explosion();
                        scorePopups.add(new ScorePopup(enemy.getCenterX() - 12, enemy.getY(), enemy.getReward()));
                        if (random.nextDouble() < dropChance(enemy)) spawnPowerUpNear(enemy.getCenterX(), enemy.getBottomY());
                    } else {
                        score += level;
                    }
                    break;
                }
            }
        }
        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) continue;
            for (Bomb bomb : bombs) {
                if (!bomb.isActive()) continue;
                if (bullet.intersects(bomb.getX(), bomb.getY(), bomb.getWidth(), bomb.getHeight())) {
                    bullet.deactivate(); bomb.deactivate(); score += 2 * level;
                    explosions.add(new Explosion(bomb.getX(), bomb.getY(), 18));
                    SoundEffects.explosion();
                    break;
                }
            }
        }
        for (Bomb bomb : bombs) {
            if (bomb.isActive() && bomb.intersects(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                bomb.deactivate();
                boolean damaged = player.takeDamage(20);
                explosions.add(new Explosion(player.getCenterX(), player.getY() + 30, damaged ? 28 : 18));
                if (damaged) {
                    screenShake = 0.14;
                    playerRedFlash = 0.22;
                    SoundEffects.playerHit();
                }
                if (player.isDead() && shipDestroyedTimer < 0) {
                    shipDestroyedTimer = 0.9;
                    scorePopups.add(new ScorePopup(player.getCenterX() - 48, player.getY() - 12, "Ship destroyed"));
                    explosions.add(new Explosion(player.getCenterX(), player.getY() + 28, 54));
                }
            }
        }
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive() && powerUp.intersects(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                applyPowerUp(powerUp.getType());
                powerUp.deactivate();
                explosions.add(new Explosion(player.getCenterX(), player.getY() + 25, 22));
                SoundEffects.powerUp();
            }
        }
    }

    private boolean isProtectedByManta(Enemy enemy) {
        if (!enemy.isSmallEnemy()) return false;
        return enemies.stream().anyMatch(other -> other.isAlive()
                && other.getType() == Enemy.Type.MANTA
                && Math.abs(other.getCenterX() - enemy.getCenterX()) < 145
                && Math.abs(other.getY() - enemy.getY()) < 95);
    }

    private double explosionSize(Enemy enemy) {
        return switch (enemy.getType()) {
            case BOSS -> 86;
            case TANK, REAPER, CRAB, MANTA -> 48;
            default -> 32;
        };
    }

    private double dropChance(Enemy enemy) {
        return switch (enemy.getType()) {
            case SCOUT -> 0.10;
            case HORNET -> 0.14;
            case MANTA -> 0.18;
            case CRAB -> 0.26;
            case TANK -> 0.28;
            case REAPER -> 0.45;
            case BOSS -> 1.0;
        };
    }

    private void spawnPowerUp() {
        PowerUp.Type[] types = PowerUp.Type.values();
        powerUps.add(new PowerUp(45 + random.nextDouble() * (MainApp.WIDTH - 90), 24, types[random.nextInt(types.length)], 52 + level * 8));
    }

    private void spawnPowerUpNear(double x, double y) {
        PowerUp.Type[] types = PowerUp.Type.values();
        powerUps.add(new PowerUp(Math.max(20, Math.min(MainApp.WIDTH - 50, x)), y, types[random.nextInt(types.length)], 62 + level * 6));
    }

    private void applyPowerUp(PowerUp.Type type) {
        switch (type) {
            case SHIELD -> {
                player.activateShield(6.0);
                scorePopups.add(new ScorePopup(player.getCenterX() - 48, player.getY() - 8, "Shield activated"));
            }
            case DOUBLE_SHOT -> {
                player.activateDoubleShot(7.0);
                scorePopups.add(new ScorePopup(player.getCenterX() - 46, player.getY() - 8, "Double Shot"));
            }
            case REPAIR -> {
                player.heal(30);
                scorePopups.add(new ScorePopup(player.getCenterX() - 32, player.getY() - 8, "Repair +30"));
            }
        }
        score += 15;
    }

    private void refreshSelectedEnemy() {
        if (selectedEnemy != null && selectedEnemy.isAlive()) return;
        selectedEnemy = enemies.stream().filter(Enemy::isAlive).max(Comparator.comparingInt(Enemy::getMaxHealth)).orElse(null);
    }

    private void render() {
        gc.save();
        if (screenShake > 0) {
            double amount = screenShake * 28;
            gc.translate((random.nextDouble() - 0.5) * amount, (random.nextDouble() - 0.5) * amount);
        }
        drawBackground();
        for (Star s : stars) s.draw(gc);
        if (!gameOver && !victory) {
            for (PowerUp p : powerUps) if (p.isActive()) p.draw(gc);
            for (Enemy e : enemies) if (e.isAlive()) e.draw(gc);
            for (Bullet b : bullets) if (b.isActive()) b.draw(gc);
            for (Bomb b : bombs) if (b.isActive()) b.draw(gc);
            player.draw(gc);
        }
        for (Explosion ex : explosions) ex.draw(gc);
        for (ScorePopup popup : scorePopups) popup.draw(gc);
        gc.restore();
        drawPlayerDamageFlash();
        if (!gameOver && !victory) {
            drawHUD();
            drawLevelMessage();
        }
        if (paused && !gameOver && !victory) drawOverlay("PAUSE", Color.CYAN, "Appuie sur P pour continuer");
        if (gameOver) drawOverlay("GAME OVER", Color.RED, "Score final : " + score);
        if (victory) drawOverlay("LEVEL COMPLETE", Color.LIME, "Score : " + score + " | Niveau " + level + " termine");
    }

    private void drawBackground() {
        gc.setFill(Color.color(0.015, 0.015, 0.055));
        gc.fillRect(0, 0, MainApp.WIDTH, MainApp.HEIGHT);
        gc.setFill(Color.color(0.0, 0.55, 0.9, 0.10));
        gc.fillOval(-140, 90, 360, 260);
        gc.setFill(Color.color(0.75, 0.0, 0.9, 0.09));
        gc.fillOval(MainApp.WIDTH - 260, 130, 420, 320);
        gc.setStroke(Color.color(0.0, 0.9, 1.0, 0.14));
        for (int i = 0; i < 8; i++) gc.strokeLine(0, MainApp.HEIGHT - 25 - i * 20, MainApp.WIDTH, MainApp.HEIGHT - 65 - i * 11);
    }

    private void drawPlayerDamageFlash() {
        if (playerRedFlash <= 0) return;
        double alpha = Math.min(0.28, playerRedFlash / 0.22 * 0.28);
        gc.setFill(Color.color(1.0, 0.0, 0.0, alpha));
        gc.fillRect(0, 0, MainApp.WIDTH, MainApp.HEIGHT);
    }

    private void drawHUD() {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        gc.setFill(Color.WHITE);
        gc.fillText("Score : " + score, 15, 28);
        gc.fillText("Niveau : " + level, MainApp.WIDTH / 2.0 - 40, 28);
        gc.fillText("Wave " + currentWave + "/" + totalWaves, MainApp.WIDTH / 2.0 - 48, 54);
        gc.fillText("Enemies left : " + enemies.stream().filter(Enemy::isAlive).count(), 15, 54);
        gc.fillText("P : Pause", MainApp.WIDTH - 100, MainApp.HEIGHT - 18);
        drawEnergyBar();
        drawPowerTimers();
        drawPowerLegend();
        drawMonsterPanel();
    }

    private void drawEnergyBar() {
        double x = MainApp.WIDTH - 250, y = 15, w = 160, h = 16;
        gc.setFill(Color.color(1, 1, 1, 0.12));
        gc.fillRoundRect(x, y, w, h, 10, 10);
        gc.setFill(player.getEnergy() > 35 ? Color.LIME : Color.ORANGERED);
        gc.fillRoundRect(x, y, w * player.getEnergyPercent(), h, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(x, y, w, h, 10, 10);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setFill(Color.WHITE);
        gc.fillText("ENERGIE " + player.getEnergy() + "%", x + 34, y + 12);
    }

    private void drawPowerTimers() {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        double y = 76;
        if (player.hasShield()) { gc.setFill(Color.CYAN); gc.fillText("Shield " + formatTime(player.getShieldTime()) + "s", 15, y); y += 20; }
        if (player.hasDoubleShot()) { gc.setFill(Color.LIME); gc.fillText("Double shot " + formatTime(player.getDoubleShotTime()) + "s", 15, y); }
    }

    private void drawPowerLegend() {
        double x = 15, y = MainApp.HEIGHT - 76;
        gc.setFill(Color.color(0.02, 0.02, 0.08, 0.58));
        gc.fillRoundRect(x, y, 158, 52, 8, 8);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.CYAN);
        gc.fillText("S = Shield", x + 10, y + 18);
        gc.setFill(Color.LIME);
        gc.fillText("2 = Double Shot", x + 10, y + 34);
        gc.setFill(Color.PINK);
        gc.fillText("+ = Repair", x + 10, y + 50);
    }

    private void drawMonsterPanel() {
        Enemy e = selectedEnemy != null && selectedEnemy.isAlive() ? selectedEnemy : enemies.stream().filter(Enemy::isAlive).max(Comparator.comparingInt(Enemy::getMaxHealth)).orElse(null);
        double x = MainApp.WIDTH - 232, y = 48, w = 217, h = 62;
        gc.setFill(Color.color(0.02, 0.02, 0.08, 0.56));
        gc.fillRoundRect(x, y, w, h, 8, 8);
        gc.setStroke(Color.color(0.0, 0.9, 1.0, 0.45));
        gc.strokeRoundRect(x, y, w, h, 8, 8);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.CYAN);
        gc.fillText("MONSTER STATUS", x + 9, y + 15);
        if (e == null) { gc.setFill(Color.WHITE); gc.fillText("Aucun monstre restant", x + 12, y + 46); return; }
        gc.setFill(Color.WHITE);
        gc.fillText(e.getDisplayName(), x + 9, y + 33);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText(e.getAbilityName(), x + 9, y + 50);
        double hpX = x + 100, hpY = y + 25, hpW = 96, hpH = 8, pct = e.getHealth() / (double) e.getMaxHealth();
        gc.setFill(Color.color(1, 1, 1, 0.12));
        gc.fillRoundRect(hpX, hpY, hpW, hpH, 8, 8);
        gc.setFill(pct > 0.45 ? Color.color(0.45, 0.95, 0.35, 0.78) : Color.color(1.0, 0.25, 0.12, 0.78));
        gc.fillRoundRect(hpX, hpY, hpW * pct, hpH, 8, 8);
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(hpX, hpY, hpW, hpH, 8, 8);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setFill(Color.WHITE);
        gc.fillText("HP " + e.getHealth() + "/" + e.getMaxHealth(), hpX + 18, hpY + 8);
    }

    private void drawLevelMessage() {
        if (levelMessageTimer <= 0) return;
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setFill(Color.color(0.2, 1.0, 1.0, Math.min(1, levelMessageTimer)));
        gc.fillText(levelMessage, MainApp.WIDTH / 2.0 - levelMessage.length() * 6.5, 94);
    }

    private String formatTime(double value) { return String.format("%.1f", value); }

    private void drawOverlay(String title, Color titleColor, String subtitle) {
        gc.setFill(Color.color(0, 0, 0, gameOver || victory ? 0.82 : 0.68));
        gc.fillRect(0, 0, MainApp.WIDTH, MainApp.HEIGHT);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        gc.setFill(titleColor);
        gc.fillText(title, MainApp.WIDTH / 2.0 - title.length() * 15.0, MainApp.HEIGHT / 2.0 - 40);
        gc.setFont(Font.font("Arial", 22));
        gc.setFill(Color.WHITE);
        gc.fillText(subtitle, MainApp.WIDTH / 2.0 - subtitle.length() * 5.5, MainApp.HEIGHT / 2.0 + 10);
        if (paused && !gameOver && !victory) return;
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.LIGHTGRAY);
        String hint = victory ? "Press Enter for next level  |  Press Esc for menu" : "Press Enter to restart  |  Press Esc for menu";
        gc.fillText(hint, MainApp.WIDTH / 2.0 - hint.length() * 4.4, MainApp.HEIGHT / 2.0 + 55);
        if (!overlayListenerSet) {
            overlayListenerSet = true;
            scene.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case ENTER -> {
                        leftPressed = false; rightPressed = false; spacePressed = false;
                        timer.stop();
                        if (victory) new MenuScreen(stage).startGame(level + 1); else new MenuScreen(stage).startGame(level);
                    }
                    case ESCAPE -> {
                        leftPressed = false; rightPressed = false; spacePressed = false;
                        timer.stop();
                        MenuScreen menu = new MenuScreen(stage);
                        stage.setScene(new Scene(menu.getRoot(), MainApp.WIDTH, MainApp.HEIGHT));
                    }
                }
            });
        }
    }

    public Parent getRoot() { return root; }
}
