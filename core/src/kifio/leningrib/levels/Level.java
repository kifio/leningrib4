package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.util.List;
import java.util.Random;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;

public class Level {

    // Граф поиска пути
    private ForestGraph forestGraph;

    private GameScreen gameScreen;
//    private Random random = new Random();

    private MushroomsManager mushroomsManager = new MushroomsManager();
    private ForestersManager forestersManager;
    private TreesManager treesManager = new TreesManager();
    private Grandma grandma = null;
    private List<Label> tutorialLabels = null;

    private boolean isDisposed = false;

    private int levelHeight;

    public Level(int x, int y, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        forestersManager = new ForestersManager(gameScreen);

        // Хак, чтобы обойти момент с тем, что генератор складно выдает уровни лишь слева направо, снизу вверх
        if (y > 0 && gameScreen.worldMap.getLevel(x + 1, y - 1) == null) {
            gameScreen.worldMap.addLevel(x + 1, y - 1, gameScreen.constantsConfig);
        }

        LevelMap levelMap;

        if (gameScreen.isFirstLaunch) {
            gameScreen.isFirstLaunch = false;
            grandma = new Grandma(GameScreen.tileSize * 4, GameScreen.tileSize * 19);
            levelMap = FirstLevel.getFirstLevel(gameScreen.constantsConfig);
            // Возвращает результат помещения урвоня в пустой хэшмап (null).
            gameScreen.worldMap.addLevel(x, y, levelMap);
            treesManager.buildTrees(levelMap, gameScreen.constantsConfig);
            mushroomsManager.initMushrooms(FirstLevel.getMushrooms());
//            forestersManager.initDebugForester(FirstLevel.getForester());
            tutorialLabels = FirstLevel.getTutorialLabels();
        } else {
            levelMap = gameScreen.worldMap.addLevel(x, y, gameScreen.constantsConfig);
            Rectangle[] roomRectangles = getRoomsRectangles(levelMap, gameScreen.constantsConfig);
            treesManager.buildTrees(levelMap, gameScreen.constantsConfig);
            mushroomsManager.initMushrooms(gameScreen.constantsConfig, treesManager.trees);
            forestersManager.initForester(roomRectangles);
        }

        this.forestGraph = new ForestGraph(gameScreen.constantsConfig, treesManager, forestersManager, grandma);
        for (Forester f: forestersManager.getForesters()) {
            f.initPath(forestGraph);
        }

        levelHeight = gameScreen.constantsConfig.getLevelHeight();
    }

    public void dispose() {
        if (isDisposed) return;
        mushroomsManager.dispose();
        forestersManager.dispose();
        forestGraph = null;
        gameScreen = null;
        isDisposed = true;
    }

    public Player getPlayer() {
        return gameScreen.player;
    }

    public void update(float delta, float cameraY) {
        forestGraph.updateForestGraph(cameraY);
        forestersManager.updateForesters(delta, forestGraph);
        mushroomsManager.updateMushrooms(gameScreen.player, cameraY);
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
        return grandma;
    }

    public List<Label> getTutorialLabels() {
        return tutorialLabels;
    }

    public Label[] getForestersSpeeches() {
        return forestersManager.speeches;
    }

    public Label[] getMushroomsSpeeches() {
        return mushroomsManager.getSpeeches();
    }

    private Rectangle[] getRoomsRectangles(LevelMap levelMap, ConstantsConfig config) {
        List<Room> rooms = levelMap.getRooms();
        int size = rooms.size();
        Rectangle[] rectangles = new Rectangle[size];

        for (int i = 0; i < size; i++) {
            Room room = rooms.get(i);
            rectangles[i] = new Rectangle(0, room.getY(), config.getLevelWidth(), room.getHeight() - 2);
        }
        return rectangles;
    }

    public float getLevelHeight() {
        return levelHeight;
    }
}