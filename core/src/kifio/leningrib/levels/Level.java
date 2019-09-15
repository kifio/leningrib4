package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.util.List;
import kifio.leningrib.levels.helpers.ForestersManager;
import kifio.leningrib.levels.helpers.MushroomsManager;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;

public abstract class Level {

    private GameScreen gameScreen;
    private ForestGraph forestGraph;
    MushroomsManager mushroomsManager;
    ForestersManager forestersManager;
    TreesManager treesManager;
    protected Array<Actor> actors;
    private boolean isDisposed = false;

    Level(int x, int y, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        LevelMap levelMap = getLevelMap(gameScreen, x, y);

        forestersManager = new ForestersManager(gameScreen, initForesters(levelMap, gameScreen));
        treesManager = new TreesManager();
        mushroomsManager = new MushroomsManager();

        treesManager.buildTrees(levelMap, gameScreen.constantsConfig);
        mushroomsManager.initMushrooms(initMushrooms(gameScreen.constantsConfig, treesManager.trees));

        forestGraph = new ForestGraph(gameScreen.constantsConfig, treesManager, getActors());

        // Хак, чтобы обойти момент с тем, что генератор складно выдает уровни лишь слева направо, снизу вверх
        if (y > 0 && gameScreen.worldMap.getLevel(x + 1, y - 1) == null) {
            gameScreen.worldMap.addLevel(x + 1, y - 1, gameScreen.constantsConfig);
        }

        for (Forester f: forestersManager.getForesters()) {
            f.initPath(forestGraph);
        }
    }

    protected abstract Array<Actor> getActors();

    protected abstract LevelMap getLevelMap(GameScreen gameScreen, int x, int y);

    protected abstract Array<Mushroom> initMushrooms(ConstantsConfig config, Array<Actor> trees);

    protected abstract Array<Forester> initForesters(LevelMap levelMap, GameScreen gameScreen);

    public void update(float delta, float cameraY) {
        forestGraph.updateForestGraph(cameraY);
        forestersManager.updateForesters(delta, forestGraph);
        mushroomsManager.updateMushrooms(gameScreen.player, cameraY);
    }

    public Player getPlayer() {
        return gameScreen.player;
    }

    public Array<Mushroom> getMushrooms() {
        return mushroomsManager.getMushrooms();
    }

    public void movePlayerTo(float x, float y) {
        getPlayer().resetPlayerPath(x, y, forestGraph, gameScreen);
    }

    public Array<Forester> getForesters() {
        return forestersManager.getForesters();
    }

    public Array<Actor> getTrees() {
        return treesManager.trees;
    }

    public Grandma getGrandma() {
        return this instanceof FirstLevel ? (Grandma) getActors().get(0) : null;
    }

    public List<Label> getTutorialLabels() {
        return this instanceof FirstLevel ? ((FirstLevel) this).tutorialLabels : null;
    }

    public Label[] getForestersSpeeches() {
        return forestersManager.speeches;
    }

    public Label[] getMushroomsSpeeches() {
        return mushroomsManager.getSpeeches();
    }

    public void dispose() {
        if (isDisposed) return;
        actors.clear();
        mushroomsManager.dispose();
        forestersManager.dispose();
        actors = null;
        forestGraph = null;
        gameScreen = null;
        isDisposed = true;
    }
}