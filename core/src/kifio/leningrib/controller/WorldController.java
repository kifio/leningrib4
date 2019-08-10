package kifio.leningrib.controller;

import kifio.leningrib.levels.Level;
import kifio.leningrib.screens.GameScreen;

public class WorldController {

    private GameScreen gameScreen;
    private Level level;

    public WorldController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void reset(Level level) {
        this.level = level;
    }

    public void movePlayerTo(float x, float y) {
        level.resetPlayerPath(x, y);
    }

    public void update(float delta, float gameTime) {
        if (level.player.getY() < 0) {
            gameScreen.onLevelPassed();
            return;
        }

        level.update(delta, gameTime);
    }
}