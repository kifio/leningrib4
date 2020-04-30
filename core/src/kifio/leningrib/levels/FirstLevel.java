package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import generator.Config;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class FirstLevel extends Level {

    private Grandma grandma = new Grandma(GameScreen.tileSize * 5, GameScreen.tileSize * 18);

    public FirstLevel(LevelMap levelMap) {
        super(null, levelMap);
    }

    private float halfHeight = Gdx.graphics.getHeight() / 2f;

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
                GameScreen.tileSize,
                GameScreen.tileSize * 28,
                GameScreen.tileSize * 6,
                ThreadLocalRandom.current().nextInt(1, 4),
                GameScreen.tileSize * 23,
                GameScreen.tileSize * 35,
                GameScreen.tileSize,
                GameScreen.tileSize * 7));
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

    public Label getGrandmaLabel() {
        return grandma.getGrandmaLabel();
    }
}