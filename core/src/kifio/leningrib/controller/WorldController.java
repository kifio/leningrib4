package kifio.leningrib.controller;

import com.badlogic.gdx.Gdx;
import kifio.leningrib.levels.Level;
import kifio.leningrib.screens.GameScreen;

public class WorldController {

    private GameScreen gameScreen;
    private Level level;
    private float xLimit = Gdx.graphics.getWidth() - GameScreen.tileSize;
    private float yLimit;

    public WorldController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void reset(Level level) {
        yLimit = (level.getLevelHeight() - 1) * GameScreen.tileSize;
        if (this.level != null) this.level.dispose();
        this.level = level;
    }

    public void movePlayerTo(float x, float y) {
        level.movePlayerTo(x, y);
    }

    public void update(float delta, float cameraPositionY) {
        if (!gameScreen.isGameOver() && level.getPlayer().getY() >= yLimit) {
            gameScreen.onGoUp();
            gameScreen.onLevelPassed();
            return;
        } else if (!gameScreen.isGameOver() && level.getPlayer().getX()  >= xLimit) {
            gameScreen.onGoRight();
            gameScreen.onLevelPassed();
            return;
        }

        level.update(delta, cameraPositionY);
    }

    public void dispose() {
        this.level.dispose();
        gameScreen = null;
        level = null;
    }
}