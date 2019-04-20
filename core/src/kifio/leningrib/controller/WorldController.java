package kifio.leningrib.controller;

import com.badlogic.gdx.math.Vector2;

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
        if (level.player.checkIsStayed(x, y)) return;
        if (level.isUnreachableZone(new Vector2(x, y))) return;
        if (level.player.checkMoveLeft(x, y)) level.player.moveLeft();
        else if (level.player.checkMoveRight(x, y)) level.player.moveRight();
        else if (level.player.checkMoveUp(x, y)) level.player.moveUp();
        else if (level.player.checkMoveDown(x, y)) level.player.moveDown();
    }

    public void update() {
        checkIsLevelPassed();
    }

    private void checkIsLevelPassed() {
        if (level.player.getY() < 0) gameScreen.onLevelPassed();
    }
}