package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.List;

import javax.rmi.CORBA.Util;

import generator.Config;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.helpers.BottleManager;
import kifio.leningrib.levels.helpers.ForestersManager;
import kifio.leningrib.levels.helpers.MushroomsManager;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.items.Bottle;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;

public abstract class Level {

    private static final Array<Actor> emptyArr = new Array<Actor>();

    // Если позиция камеры выходит за рамки этих значений, камера перестает двигаться
    protected float bottomCameraThreshold = Gdx.graphics.getHeight() / 2f;
    protected float lastKnownCameraPosition = Gdx.graphics.getHeight() / 2f;

    protected ForestersManager forestersManager;
    protected ForestGraph forestGraph;
    protected Array<Rectangle> roomsRectangles;
    protected BottleManager bottleManager;
    private ForestGraph strengthForestGraph;    // Граф, с которым можно гоняться за лесниками
    private ForestGraph dexterityForestGraph;  // Граф, с которым можно ходить за деревьями
    private MushroomsManager mushroomsManager;
    private TreesManager treesManager;
    public int generatedLevelsCount = 0;

    protected void copy(Level level) {
        this.forestersManager = level.forestersManager;
        this.forestGraph = level.forestGraph;
        this.roomsRectangles = level.roomsRectangles;
        this.bottleManager = level.bottleManager;
        this.strengthForestGraph = level.strengthForestGraph;
        this.dexterityForestGraph = level.dexterityForestGraph;
        this.mushroomsManager = level.mushroomsManager;
        this.treesManager = level.treesManager;
        this.generatedLevelsCount = level.generatedLevelsCount;
        this.bottomCameraThreshold = level.bottomCameraThreshold;
    }

    protected void setup(Player player, LevelMap levelMap, Config levelConfig) {
        roomsRectangles = new Array<>();
        Rectangle[] rectangles = getRoomsRectangles(levelMap, levelConfig, generatedLevelsCount);
        Array<Forester> foresters = initForesters(levelMap, levelConfig, player, rectangles);
        roomsRectangles.addAll(rectangles);

        bottleManager = new BottleManager();
        forestersManager = new ForestersManager(foresters);
        treesManager = new TreesManager();
        mushroomsManager = new MushroomsManager();

        treesManager.updateTrees(levelMap, levelConfig, generatedLevelsCount);
        mushroomsManager.initMushrooms(initMushrooms(levelConfig,
                treesManager,
                player == null ? 0 : player.getMushroomsCount()));

        forestGraph = new ForestGraph(levelConfig,
                treesManager.getObstacleTrees());

        dexterityForestGraph = new ForestGraph(levelConfig,
                treesManager.getOuterBordersTrees());

        strengthForestGraph = new ForestGraph(levelConfig,
                treesManager.getObstacleTrees());

        for (Forester f : forestersManager.getForesters()) {
            f.initPath(forestGraph);
        }
    }

    public int clearLevelPartially(Player player, Config levelConfig) {
        int threshold = (int) Utils.mapCoordinate(player.getY()) - Gdx.graphics.getHeight() - GameScreen.tileSize;

        Iterator<Rectangle> roomsIterator = roomsRectangles.iterator();
        while (roomsIterator.hasNext()) {
            Rectangle next = roomsIterator.next();
            if (next.y < threshold / (float) GameScreen.tileSize) {
                roomsIterator.remove();
            }
        }

        threshold *= GameScreen.tileSize;
        removeActorsFrom(forestersManager.getForesters(), threshold);
        removeActorsFrom(treesManager.getOuterBordersTrees(), threshold);
        removeActorsFrom(treesManager.getObstacleTrees(), threshold);
        removeActorsFrom(treesManager.getBottomBorderNonObstaclesTrees(), threshold);
        removeActorsFrom(treesManager.getTopBorderNonObstaclesTrees(), threshold);
        removeActorsFrom(treesManager.getInnerBordersTrees(), threshold);
        return threshold;
    }

    public void addLevelMapIfNeeded(LevelMap levelMap, Player player, Config levelConfig, int threshold) {

        long start = System.nanoTime();
        Rectangle[] rectangles = getRoomsRectangles(levelMap, levelConfig, generatedLevelsCount);
        Array<Forester> foresters = initForesters(levelMap, levelConfig, player, rectangles);
        roomsRectangles.addAll(rectangles);
        forestersManager.addForesters(foresters);
        treesManager.updateTrees(levelMap, levelConfig, generatedLevelsCount);
        Gdx.app.log("kifio_time", "Update actors took: " + (System.nanoTime() - start) / 1_000_000);

        start = System.nanoTime();
        forestGraph.update(levelConfig,
                treesManager.getObstacleTrees(),
                threshold,
                generatedLevelsCount);
        Gdx.app.log("kifio_time", "Update forestGraph took: " + (System.nanoTime() - start) / 1_000_000);

        start = System.nanoTime();
        dexterityForestGraph.update(levelConfig,
                treesManager.getOuterBordersTrees(),
                threshold,
                generatedLevelsCount);
        Gdx.app.log("kifio_time", "Update dexterityGraph took: " + (System.nanoTime() - start) / 1_000_000);

        start = System.nanoTime();
        strengthForestGraph.update(levelConfig,
                treesManager.getObstacleTrees(),
                threshold,
                generatedLevelsCount);
        Gdx.app.log("kifio_time", "Update strength graph: " + (System.nanoTime() - start) / 1_000_000);
    }

    private void removeActorsFrom(Array<? extends Actor> actors, float threshold) {
        Iterator<? extends Actor> iterator = actors.iterator();
        while (iterator.hasNext()) {
            Actor next = iterator.next();
            if (next.getY() < threshold / (float) GameScreen.tileSize) {
                next.clear();
                next.remove();
                iterator.remove();
            }
        }
    }

    public void updateCamera(OrthographicCamera camera, Player player) {
        if (camera.position.y > lastKnownCameraPosition) {
            lastKnownCameraPosition = camera.position.y;
            bottomCameraThreshold = lastKnownCameraPosition - Gdx.graphics.getHeight() / 2f;
        }
    }

    public abstract int getLevelHeight();

    protected abstract Array<? extends Actor> getActors();

    protected abstract Array<Mushroom> initMushrooms(Config config, TreesManager treesManager, int mushroomsCount);

    protected abstract Array<Forester> initForesters(LevelMap levelMap, Config config, Player player, Rectangle[] roomRectangles);

    public void update(float delta, OrthographicCamera camera, GameScreen gameScreen) {
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

    private Rectangle[] getRoomsRectangles(LevelMap levelMap, Config config, int index) {
        List<Room> rooms = levelMap.getRooms();
        int size = rooms.size();
        Rectangle[] rectangles = new Rectangle[size];

        for (int i = 0; i < size; i++) {
            Room room = rooms.get(i);
            rectangles[i] = new Rectangle(0,
                    room.getY() + config.getLevelHeight() * index,
                    config.getLevelWidth(),
                    room.getHeight() - 2);
        }

        return rectangles;
    }

    public void dispose() {
        this.forestersManager = null;
        this.forestGraph = null;
        this.roomsRectangles = null;
        this.bottleManager = null;
        this.strengthForestGraph = null;
        this.dexterityForestGraph = null;
        this.mushroomsManager = null;
        this.treesManager = null;
    }
}
