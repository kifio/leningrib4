package kifio.leningrib.levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import generator.Config;
import kifio.leningrib.LGCGame;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Friend;
import kifio.leningrib.model.actors.game.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class FirstLevel extends Level {

    private static final int PLAYER_INITIAL_Y = GameScreen.tileSize * 5;
    private static final int PLAYER_INITIAL_X = GameScreen.tileSize * 3;
    private static final int FRIEND_INITIAL_X = GameScreen.tileSize * 6;

    private Grandma grandma;
    private Friend friend;

    public FirstLevel(LevelMap levelMap) {
        super(null, levelMap);
    }

    private float halfHeight = Gdx.graphics.getHeight() / 2f;

    @Override
    protected Array<? extends Actor> getActors() {
        Array<Actor> arr = new Array<>();
        if (grandma == null) grandma = new Grandma(GameScreen.tileSize * 5, GameScreen.tileSize * 18);
        if (friend == null) friend = new Friend(FRIEND_INITIAL_X, PLAYER_INITIAL_Y);
        arr.add(grandma);
        arr.add(friend);
        arr.addAll(forestersManager.getForesters());
        return arr;
    }

    public Player getPlayer() {
        return new Player(PLAYER_INITIAL_X, PLAYER_INITIAL_Y);
    }

    @Override
    protected Array<Mushroom> initMushrooms(Config config, TreesManager treesManager, int mushroomsCount) {
        Array<Mushroom> mushrooms = new Array<>();
        mushrooms.add(new Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 16, false));
        mushrooms.add(new Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 5, false));
        return mushrooms;
    }

    @Override
    protected Array<Forester> initForesters(LevelMap levelMap, Config config, Player player, Rectangle[] roomRectangles) {
        Array<Forester> foresters = new Array<>(1);
        foresters.add(new Forester(
                GameScreen.tileSize * 2,
                GameScreen.tileSize * 24,
                GameScreen.tileSize * (LGCGame.Companion.getLevelWidth() - 2),
                ThreadLocalRandom.current().nextInt(1, 4),
                GameScreen.tileSize * 23,
                GameScreen.tileSize * (LGCGame.Companion.getLevelHeight() - 2),
                GameScreen.tileSize  * 2,
                GameScreen.tileSize * (LGCGame.Companion.getLevelWidth() - 2)));
        return foresters;
    }

    @Override
    public void update(float delta, GameScreen gameScreen) {
        super.update(delta, gameScreen);

        float playerY = gameScreen.player.getY();
        float grandmaY = grandma.getY() + grandma.getHeight();

        if (Math.abs(playerY - grandmaY) <= halfHeight - GameScreen.tileSize && grandma.getPlayer() == null) {
            grandma.setPlayer(gameScreen.player);
        } else if (Math.abs(playerY - grandmaY) > halfHeight && grandma.getPlayer() != null) {
            grandma.setPlayer(null);
        }
    }

    public Grandma getGrandma() {
        return grandma;
    }

    public Friend getFriend() {
        return friend;
    }

    public Label getGrandmaLabel() {
        return grandma.getGrandmaLabel();
    }
}