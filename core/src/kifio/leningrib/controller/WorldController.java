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
        if (this.level != null) this.level.dispose();
        this.level = level;
    }

    public void movePlayerTo(float x, float y) {
        level.resetPlayerPath(x, y);
    }

    public void update(float delta, float gameTime) {
        if (!GameScreen.gameOver && level.getPlayer().getY() >= (level.mapHeight - 1) * GameScreen.tileSize) {
            gameScreen.onGoUp();
            gameScreen.onLevelPassed();
            return;
        } else if (!GameScreen.gameOver && level.getPlayer().getX() >= (level.mapWidth - 1) * GameScreen.tileSize) {
            gameScreen.onGoRight();
            gameScreen.onLevelPassed();
            return;
        }

        level.update(delta, gameTime);
    }
}