package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.List;

import generator.Config;
import kifio.leningrib.LGCGame;
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

    protected ForestersManager forestersManager;
    protected ForestGraph forestGraph;
    protected Array<Rectangle> roomsRectangles;
    protected BottleManager bottleManager;
    private ForestGraph strengthForestGraph;    // Граф, с которым можно гоняться за лесниками
    private ForestGraph dexterityForestGraph;  // Граф, с которым можно ходить за деревьями
    private MushroomsManager mushroomsManager;
    private TreesManager treesManager;
    public int nextLevel = 0;

    protected void copy(Level level) {
        this.forestersManager = level.forestersManager;
        this.forestGraph = level.forestGraph;
        this.roomsRectangles = level.roomsRectangles;
        this.bottleManager = level.bottleManager;
        this.strengthForestGraph = level.strengthForestGraph;
        this.dexterityForestGraph = level.dexterityForestGraph;
        this.mushroomsManager = level.mushroomsManager;
        this.treesManager = level.treesManager;
        this.nextLevel = level.nextLevel;
        this.bottomCameraThreshold = level.bottomCameraThreshold;
    }

    protected void setup(Player player, LevelMap levelMap, Config levelConfig) {
        roomsRectangles = new Array<>();
        Rectangle[] rectangles = getRoomsRectangles(levelMap, levelConfig, nextLevel);
        Array<Forester> foresters = initForesters(levelMap, levelConfig, player, rectangles);
        roomsRectangles.addAll(rectangles);

        bottleManager = new BottleManager();
        forestersManager = new ForestersManager(foresters);
        treesManager = new TreesManager();
        mushroomsManager = new MushroomsManager();

        treesManager.updateTrees(levelMap, levelConfig, nextLevel);
        mushroomsManager.addMushrooms(initMushrooms(levelConfig,
                treesManager,
                player == null ? 0 : player.getMushroomsCount()));

        forestGraph = new ForestGraph(treesManager.getObstacleTrees());
        dexterityForestGraph = new ForestGraph(treesManager.getOuterBordersTrees());
        strengthForestGraph = new ForestGraph(treesManager.getObstacleTrees());

        for (Forester f : forestersManager.gameObjects) {
            f.initPath(forestGraph);
        }
    }

    public void clearPassedLevels(int currentLevel) {
        int threshold = (currentLevel - 1) * getLevelHeight() * GameScreen.tileSize;
        if (threshold <= 0) return;

        Iterator<Rectangle> roomsIterator = roomsRectangles.iterator();
        while (roomsIterator.hasNext()) {
            Rectangle next = roomsIterator.next();
            if (next.y < threshold / (float) GameScreen.tileSize) {
                roomsIterator.remove();
            }
        }

        removeActorsFrom(forestersManager.gameObjects, threshold);
        removeActorsFrom(mushroomsManager.gameObjects, threshold);
        removeActorsFrom(treesManager.getOuterBordersTrees(), threshold);
        removeActorsFrom(treesManager.getObstacleTrees(), threshold);
        removeActorsFrom(treesManager.getBottomBorderNonObstaclesTrees(), threshold);
        removeActorsFrom(treesManager.getTopBorderNonObstaclesTrees(), threshold);
        removeActorsFrom(treesManager.getInnerBordersTrees(), threshold);
    }

    public void addLevelMapIfNeeded(LevelMap levelMap,
                                    Player player,
                                    Config levelConfig) {
        Rectangle[] rectangles = getRoomsRectangles(levelMap, levelConfig, nextLevel);
        Array<Forester> foresters = initForesters(levelMap, levelConfig, player, rectangles);
        roomsRectangles.addAll(rectangles);
        forestersManager.addForesters(foresters);
        treesManager.updateTrees(levelMap, levelConfig, nextLevel);
        mushroomsManager.addMushrooms(initMushrooms(levelConfig,
                treesManager,
                player == null ? 0 : player.getMushroomsCount()));
        forestGraph = new ForestGraph(treesManager.getObstacleTrees());
        dexterityForestGraph= new ForestGraph(treesManager.getOuterBordersTrees());
        strengthForestGraph = new ForestGraph(treesManager.getObstacleTrees());

        for (Forester f : forestersManager.gameObjects) {
            f.initPath(forestGraph);
        }
    }

    private void removeActorsFrom(Array<? extends Actor> actors, float threshold) {
        Iterator<? extends Actor> iterator = actors.iterator();
        while (iterator.hasNext()) {
            Actor next = iterator.next();
            if (next == null) {
                iterator.remove();
            } else if (next.getY() < threshold) {
                next.clear();
                next.remove();
                iterator.remove();
            }
        }
    }

    public void updateCamera(OrthographicCamera camera, Player player) {
        if (camera.position.y > LGCGame.Companion.getLastKnownCameraPosition()) {
            LGCGame.Companion.setLastKnownCameraPosition(camera.position.y);
//            bottomCameraThreshold = lastKnownCameraPosition - Gdx.graphics.getHeight() / 2f;
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

        if (bottleManager != null) {
            bottleManager.updateBottles();
            if (forestersManager != null) {
                forestersManager.updateForesters(gameScreen, delta, bottleManager.getBottles(), forestGraph);
            }
        }

        if (mushroomsManager != null) {
            mushroomsManager.updateMushrooms(gameScreen.player, cameraY, isPaused);
        }
    }

    public void addBottle(Bottle bottle) {
        bottleManager.addBottle(bottle);
    }

    public Array<Mushroom> getMushrooms() {
        return mushroomsManager.gameObjects;
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
        return forestersManager.gameObjects;
    }

    public TreesManager getTreesManager() {
        return treesManager;
    }

    public Label[] getForestersSpeeches() {
        if (forestersManager == null) return null;
        return forestersManager.speeches;
    }

    public Label[] getMushroomsSpeeches() {
        if (mushroomsManager == null) return null;
        return mushroomsManager.speeches;
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
