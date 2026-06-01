# JEF updates

This update turns the project into a more modern JavaFX space shooter.

## What changed

- Energy bar instead of simple hearts.
- Power-ups: Shield, Double Shot, Repair.
- Random falling power-ups and bonus drops from enemies.
- Enemies move with a more dynamic wave motion.
- Bullets can destroy bombs for small bonus points.
- Pause with `P`.
- More neon-style menu and HUD.
- Multiple monster classes: Scout, Shooter, Tank, and Boss/Mothership.
- Health bars above monsters and a `MONSTER STATUS` panel showing the current monster type and HP.
- Balanced wave progression: level 1 starts with Scout Drone and Hornet only, level 2 adds Shield Manta and Crab Bomber, and level 3 introduces Void Reaper plus Mega Core.
- Clearer enemy spacing, smaller health bars, score popups, hit flashes, different explosion sizes, and light screen shake for big impacts.
- HUD now shows energy, score, level, current wave, enemies left, monster status, and a small power-up legend.

## Open-source inspiration checked

- `kailielexx/Space-Shooter-Game` was reviewed. Its README describes dynamic enemies, power-ups, score, and difficulty-level features, and its MIT license allows reuse with attribution. The available `game.js` currently only implements a simple ship movement and bullet loop, so there were no ready monster classes or monster sprites to copy directly.
- Kenney `Space Shooter Redux` was also used as visual inspiration for arcade-style enemy variety. Kenney lists that asset pack under Creative Commons CC0.

## Enemy sprite attribution

The enemy sprite sheet is bundled at `src/main/resources/assets/enemies.png`, so the game does not need to download artwork at runtime. The Canvas enemy drawings remain as a fallback if the image cannot be loaded.

- Artwork: OpenGameArt "Shoot-em-up Enemies" / `enemies_0.png`
- Artist credit shown on OpenGameArt: helpcomputer
- Source: https://opengameart.org/content/shoot-em-up-enemies
- License noted for this project: Creative Commons Attribution 3.0 (`CC-BY 3.0`). Keep this credit with redistributed copies of the game.

## Run

```bash
mvn clean compile
mvn javafx:run
```

## Controls

- `Q` / `D` or arrow keys: move the ship
- `Space`: shoot
- `P`: pause / resume
- `Enter`: replay or next level
- `Escape`: return to menu
