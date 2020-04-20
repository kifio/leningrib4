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
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class FirstLevel extends Level {

    private Actor[] actors;

    FirstLevel(GameScreen gameScreen, LevelMap levelMap) {
        super(0, 0, gameScreen, levelMap);
        initActors(gameScreen.getConstantsConfig());
        gameScreen.isFirstLevelPassed = false;
    }

    private void initActors(Config constantsConfig) {
        actors = new Actor[2];

        // Инициализация бабки
        actors[0] = (new Grandma(GameScreen.tileSize * 4, GameScreen.tileSize * 19));

        // Инициализация лесника
        actors[1] = (forestersManager.getForesters().get(0));
    }

    @Override
    protected Array<Mushroom> initMushrooms(Config constantsConfig, TreesManager treesManager) {
        return FirstLevelBuilder.getMushrooms();
    }

    @Override
    protected Array<Forester> initForesters(LevelMap levelMap, Config config, Rectangle[] roomRectangles) {
        Array<Forester> foresters = new Array<>(1);
        foresters.add(FirstLevelBuilder.getForester());
        return foresters;
    }

    @Override
    public Grandma getGrandma() {
        return ((Grandma) actors[0]);
    }

    @Override
    public Label[] getTutorialLabels() {
        return FirstLevelBuilder.getTutorialLabels();
    }
}