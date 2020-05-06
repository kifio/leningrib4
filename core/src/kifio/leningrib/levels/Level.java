package kifio.leningrib.levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import generator.Config;
import kifio.leningrib.LGCGame;
import kifio.leningrib.levels.helpers.BottleManager;
import kifio.leningrib.levels.helpers.ForestersManager;
import kifio.leningrib.levels.helpers.MushroomsManager;
import kifio.leningrib.levels.helpers.SpaceManager;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.actors.Space;
import kifio.leningrib.model.items.Bottle;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;

public abstract class Level {

    protected ForestersManager forestersManager;
    protected ForestGraph forestGraph;
    protected Rectangle[] roomsRectangles;

    private ForestGraph strengthForestGraph;    // Гра, с которым можно гоняться за лесниками
    private ForestGraph dexterityForestGraph;  // Граф, с которым можно ходить за деревьями
    private MushroomsManager mushroomsManager;
    private TreesManager treesManager;
    private BottleManager bottleManager;
    private boolean isDisposed = false;

    Level(Player player, LevelMap levelMap) {
        Config levelConfig = new Config(LGCGame.Companion.getLevelWidth(), LGCGame.Companion.getLevelHeight());
        roomsRectangles = getRoomsRectangles(levelMap, levelConfig);
        Array<Forester> foresters = initForesters(levelMap, levelConfig, player, roomsRectangles);

        bottleManager = new BottleManager();
        forestersManager = new ForestersManager(foresters);
        treesManager = new TreesManager();
        mushroomsManager = new MushroomsManager();

        treesManager.buildTrees(levelMap);
        mushroomsManager.initMushrooms(initMushrooms(levelConfig,
                treesManager,
                player == null ? 0 : player.getMushroomsCount()));

        forestGraph = new ForestGraph(levelConfig,
                treesManager.getObstacleTrees(),
                getActors());

        dexterityForestGraph = new ForestGraph(levelConfig,
                treesManager.getOuterBordersTrees(),
                forestersManager.getForesters());

        strengthForestGraph = new ForestGraph(levelConfig,
                treesManager.getObstacleTrees(),
                new Array<Actor>());

        for (Forester f : forestersManager.getForesters()) {
            f.initPath(forestGraph);
        }
    }

    protected abstract Array<? extends Actor> getActors();

    protected abstract Array<Mushroom> initMushrooms(Config config, TreesManager treesManager, int mushroomsCount);

    protected abstract Array<Forester> initForesters(LevelMap levelMap, Config config, Player player, Rectangle[] roomRectangles);

    public void update(float delta, GameScreen gameScreen) {
        boolean isPaused = gameScreen.isPaused();
        float cameraY = gameScreen.getCameraPostion().y;

        gameScreen.player.isPaused = gameScreen.isPaused();
        gameScreen.player.checkStuckUnderTrees(gameScreen, treesManager);
        bottleManager.updateBottles();
        strengthForestGraph.updateForestGraph(cameraY);
        forestGraph.updateForestGraph(cameraY);
        forestersManager.updateForesters(gameScreen, delta, bottleManager.getBottles(), forestGraph);
        mushroomsManager.updateMushrooms(gameScreen.player, cameraY, isPaused);
    }

    public void addBottle(Bottle bottle) {
        bottleManager.addBottle(bottle);
    }

//    public Player getPlayer() {
//        return gameScreen.player;
//    }

    public Array<Mushroom> getMushrooms() {
        return mushroomsManager.getMushrooms();
    }

    public void movePlayerTo(float x, float y, Player player) {
        if (player.isDexterous()) {
            player.resetPlayerPath(x, y, dexterityForestGraph);
        } else if (player.isStrong()) {
            player.resetPlayerPath(x, y, strengthForestGraph);
        } else {
            player.resetPlayerPath(x, y, forestGraph);
        }
    }

    public Array<Forester> getForesters() {
        return forestersManager.getForesters();
    }

    public TreesManager getTreesManager() {
        return treesManager;
    }

    public Label[] getForestersSpeeches() {
        return forestersManager.speeches;
    }

    public Label[] getMushroomsSpeeches() {
        return mushroomsManager.getSpeeches();
    }

    private Rectangle[] getRoomsRectangles(LevelMap levelMap, Config config) {
        List<Room> rooms = levelMap.getRooms();
        int size = rooms.size();
        Rectangle[] rectangles = new Rectangle[size];

        for (int i = 0; i < size; i++) {
            Room room = rooms.get(i);
            rectangles[i] = new Rectangle(0, room.getY(), config.getLevelWidth(), room.getHeight() - 2);
        }
        return rectangles;
    }

    public void dispose() {
        if (isDisposed) return;
        mushroomsManager.dispose();
        forestersManager.dispose();
        forestGraph = null;
        isDisposed = true;
    }
}
