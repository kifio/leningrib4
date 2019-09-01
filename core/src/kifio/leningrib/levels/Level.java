package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import generator.Side;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class Level {

    // Граф поиска пути
    private ForestGraph forestGraph = new ForestGraph();

    private GameScreen gameScreen;
    private Random random = new Random();

    private MushroomsManager mushroomsManager = new MushroomsManager(random);
    private ExitsManager exitsManager = new ExitsManager(random);
    private ForestersManager forestersManager;
    private MapBuilder mapBuilder = new MapBuilder();
    private Grandma grandma = null;
    private List<Label> tutorialLabels = null;

    private boolean isDisposed = false;

    public Level(int x, int y, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        forestersManager = new ForestersManager(random, gameScreen, forestGraph);

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
            mapBuilder.initMap(levelMap,
				gameScreen.constantsConfig, grandma, forestGraph);
            mushroomsManager.initMushrooms(FirstLevel.getMushrooms(random));
//            forestersManager.initDebugForester(FirstLevel.getForester());
            tutorialLabels = FirstLevel.getTutorialLabels();
        } else {
            levelMap = mapBuilder.initMap(gameScreen.worldMap.addLevel(x, y, gameScreen.constantsConfig),
                gameScreen.constantsConfig, null, forestGraph);

            Rectangle[] roomRectangles = mapBuilder.getRoomsRectangles(levelMap);
            mushroomsManager.initMushrooms(roomRectangles, mapBuilder.getTrees());
            exitsManager.init(levelMap.getExits(Side.RIGHT));
            forestersManager.initForester(x, y, roomRectangles, random);
        }
    }

    public void dispose() {
        if (isDisposed) return;
        mushroomsManager.dispose();
        exitsManager.dispose();
        forestersManager.dispose();
        mapBuilder.dispose();
        mapBuilder = null;
        forestGraph = null;
        gameScreen = null;
        isDisposed = true;
    }

    public Player getPlayer() {
        return gameScreen.player;
    }

    public void update(float delta, float cameraY) {
        forestersManager.updateForesters(delta);
        mushroomsManager.updateMushrooms(gameScreen.player, cameraY);
        exitsManager.updateExits();
    }

    public List<Mushroom> getMushrooms() {
        return mushroomsManager.getMushrooms();
    }

    public List<Speech> getMushroomsSpeeches() {
        return mushroomsManager.mushroomsSpeeches;
    }

    public List<Speech> getExitsSpeeches() {
        return exitsManager.exitsSpeeches;
    }

    public void movePlayerTo(float x, float y) {
        getPlayer().resetPlayerPath(x, y, forestGraph, gameScreen);
    }

    public List<Forester> getForesters() {
        return forestersManager.getForesters();
    }

    public List<Actor> getTrees() {
        return mapBuilder.getTrees();
    }

    public int getLevelWidth() {
        return mapBuilder.mapWidth;
    }

    public int getLevelHeight() {
        return mapBuilder.mapHeight;
    }

    public Grandma getGrandma() {
        return grandma;
    }

    public List<Label> getTutorialLabels() {
        return tutorialLabels;
    }
}