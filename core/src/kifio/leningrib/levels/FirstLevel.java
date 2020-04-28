package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import generator.Config;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class FirstLevel extends Level {

    private Grandma grandma = new Grandma(GameScreen.tileSize * 4, GameScreen.tileSize * 19);

    public FirstLevel(LevelMap levelMap) {
        super(null, levelMap);
    }

    @Override
    protected Array<Mushroom> initMushrooms(Config config, TreesManager treesManager, int mushroomsCount) {
        return FirstLevelBuilder.getMushrooms();
    }

    @Override
    protected Array<Forester> initForesters(LevelMap levelMap, Config config, Player player, Rectangle[] roomRectangles) {
        Array<Forester> foresters = new Array<>(1);
        foresters.add(FirstLevelBuilder.getForester());
        return foresters;    }

    @Override
    public Grandma getGrandma() {
        return grandma;
    }

    @Override
    public Label[] getTutorialLabels() {
        return FirstLevelBuilder.getTutorialLabels();
    }
}