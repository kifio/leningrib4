package kifio.leningrib.controller;

import kifio.leningrib.levels.FirstLevel;
import kifio.leningrib.levels.Level;
import kifio.leningrib.model.*;

import java.util.*;

import kifio.leningrib.LGCGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.math.Rectangle;

public class WorldController {

    private LGCGame game;
    private Level level;
    public MovableActor player;

    public WorldController(LGCGame game) {
        this.game = game;
        this.player = new MovableActor(
                game.tileSize / 2f,
                game.tileSize / 2f,
                game.width / game.tileSize,
                game.tileSize,
                "player.txt");
        this.level = new FirstLevel(game);
    }

    public void movePlayerTo(float x, float y) {
        if (player.checkIsStayed(x, y)) return;
        if (level.isUnreachableZone(new Vector2(x, y))) return;
        if (player.checkMoveLeft(x, y)) player.moveLeft();
        else if (player.checkMoveRight(x, y)) player.moveRight();
        else if (player.checkMoveUp(x, y)) player.moveUp();
        else if (player.checkMoveDown(x, y)) player.moveDown();
    }

    public void update() {
        checkIsLevelPassed();
    }

    private void checkIsLevelPassed() {
        if (player.getY() < 0) game.onLevelPassed();
    }

}