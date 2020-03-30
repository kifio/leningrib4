package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import generator.Config;
import generator.Generator;
import kifio.leningrib.levels.helpers.ForestersManager;
import kifio.leningrib.levels.helpers.MushroomsManager;
import kifio.leningrib.levels.helpers.SpaceManager;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.actors.Space;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import model.Exit;
import model.LevelMap;
import model.Room;

public abstract class Level {

    protected GameScreen gameScreen;
    private ForestGraph forestGraph;
    private ForestGraph strengthForestGraph;    // Гра, с которым можно гоняться за лесниками
    private ForestGraph dexterityForestGraph;  // Граф, с которым можно ходить за деревьями
    protected MushroomsManager mushroomsManager;
    protected ForestersManager forestersManager;
    protected TreesManager treesManager;
    private SpaceManager spaceManager;
    private boolean isDisposed = false;
    private Rectangle[] rectangles;

    Level(int x, int y, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        LevelMap levelMap = getLevelMap(x, y);

        forestersManager = new ForestersManager(gameScreen, initForesters(levelMap));
        treesManager = new TreesManager();
        mushroomsManager = new MushroomsManager();
        spaceManager = new SpaceManager();

        treesManager.buildTrees(levelMap, gameScreen.getConstantsConfig());
        mushroomsManager.initMushrooms(initMushrooms(gameScreen.getConstantsConfig(), treesManager));

        spaceManager.buildSpaces(getPlayer(),
                gameScreen.getConstantsConfig(),
                treesManager.getObstacleTrees(),
                mushroomsManager.getMushrooms(),
                getRoomsRectangles(levelMap, gameScreen.getConstantsConfig()));

        forestGraph = new ForestGraph(gameScreen.getConstantsConfig(),
                treesManager.getObstacleTrees(),
                forestersManager.getForesters(),
                spaceManager.getSpaces());

        dexterityForestGraph = new ForestGraph(gameScreen.getConstantsConfig(),
                treesManager.getOuterBordersTrees(),
                forestersManager.getForesters(),
                spaceManager.getSpaces());

        strengthForestGraph = new ForestGraph(gameScreen.getConstantsConfig(),
                treesManager.getObstacleTrees(),
                new Array<Actor>(),
                spaceManager.getSpaces());

        for (Forester f : forestersManager.getForesters()) {
            f.initPath(forestGraph);
        }
    }

    protected abstract LevelMap getLevelMap(int x, int y);

    protected abstract Array<Mushroom> initMushrooms(Config config, TreesManager treesManager);

    protected abstract Array<Forester> initForesters(LevelMap levelMap);

    public void update(float delta, float cameraY, boolean isPaused) {
        strengthForestGraph.updateForestGraph(cameraY);
        forestGraph.updateForestGraph(cameraY);
        forestersManager.updateForesters(delta, forestGraph, isPaused);
        mushroomsManager.updateMushrooms(gameScreen.player, cameraY, isPaused);
    }

    public Player getPlayer() {
        return gameScreen.player;
    }

    public Array<Mushroom> getMushrooms() {
        return mushroomsManager.getMushrooms();
    }

    public void movePlayerTo(float x, float y) {
        Player player = getPlayer();
        if (player.isDexterous()) {
            player.resetPlayerPath(x, y, dexterityForestGraph, gameScreen);
        } else if (player.isStrong()) {
            player.resetPlayerPath(x, y, strengthForestGraph, gameScreen);
        } else {
            player.resetPlayerPath(x, y, forestGraph, gameScreen);
        }
    }

    public Array<Forester> getForesters() {
        return forestersManager.getForesters();
    }

    public TreesManager getTreesManager() {
        return treesManager;
    }

    public abstract Grandma getGrandma();

    public abstract Label[] getTutorialLabels();

    public Label[] getForestersSpeeches() {
        return forestersManager.speeches;
    }

    public Label[] getMushroomsSpeeches() {
        return mushroomsManager.getSpeeches();
    }

    public Array<Space> getSpaces() {
        return spaceManager.getSpaces();
    }

    protected Rectangle[] getRoomsRectangles(LevelMap levelMap, Config config) {
        if (rectangles == null) {
            List<Room> rooms = levelMap.getRooms();
            int size = rooms.size();
            rectangles = new Rectangle[size];

            for (int i = 0; i < size; i++) {
                Room room = rooms.get(i);
                rectangles[i] = new Rectangle(0, room.getY(), config.getLevelWidth(), room.getHeight() - 2);
            }
        }
        return rectangles;
    }

    public void dispose() {
        if (isDisposed) return;
        mushroomsManager.dispose();
        forestersManager.dispose();
        forestGraph = null;
        gameScreen = null;
        isDisposed = true;
    }
}
