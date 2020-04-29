package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
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

    private static final int PLAYER_SPEECH_TIME = 2;

    private Grandma grandma = new Grandma(GameScreen.tileSize * 5, GameScreen.tileSize * 18);

    public FirstLevel(LevelMap levelMap) {
        super(null, levelMap);
    }

    @Override
    protected Array<Mushroom> initMushrooms(Config config, TreesManager treesManager, int mushroomsCount) {
        Array<Mushroom> mushrooms = new Array<>();
        mushrooms.add(new Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 14, false));
        return mushrooms;
    }

    @Override
    protected Array<Forester> initForesters(LevelMap levelMap, Config config, Player player, Rectangle[] roomRectangles) {
        Array<Forester> foresters = new Array<>(1);
        foresters.add(new Forester(
                GameScreen.tileSize,
                GameScreen.tileSize * 27,
                GameScreen.tileSize * 6,
                ThreadLocalRandom.current().nextInt(1, 4),
                GameScreen.tileSize * 23,
                GameScreen.tileSize * 27,
                GameScreen.tileSize,
                GameScreen.tileSize * 7));
        return foresters;
    }

    @Override
    public Grandma getGrandma() {
        return grandma;
    }

}