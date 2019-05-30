package kifio.leningrib.controller;

import kifio.leningrib.levels.Level;
import kifio.leningrib.screens.GameScreen;

public class WorldController {

    private GameScreen gameScreen;
    private Level level;

    public WorldController(GameScreen gameScreen, Level level) {
        this.gameScreen = gameScreen;
        this.level = level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void movePlayerTo(float x, float y) {
        level.resetPath(x, y);
        level.startMoving();
    }

    public void update() {
        if (level.player.getY() < 0) {
            gameScreen.onLevelPassed();
            return;
        }

        level.updateForesters();
    }
}