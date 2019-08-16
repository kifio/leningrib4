package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import generator.ConstantsConfig;
import generator.Side;
import kifio.leningrib.Utils;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;
import model.Segment;

public class Level {

    public List<Forester> foresters;

    public int mapWidth;
    public int mapHeight;

    private GameScreen gameScreen;

    private static final int INITIAL_NEIGHBORS_CAPACITY = 5;
    private List<Vector2> neighbours = new ArrayList<>(INITIAL_NEIGHBORS_CAPACITY);
    private Rectangle result = new Rectangle();
    private Random random = new Random();

    private static float caughtArea = 0.5f * GameScreen.tileSize * GameScreen.tileSize;

    // Граф поиска пути
    private ForestGraph forestGraph = new ForestGraph();

    // Объекты
    public ArrayList<Actor> trees = new ArrayList<>();

    private MushroomsManager mushroomsManager = new MushroomsManager(random);
    private ExitsManager exitsManager = new ExitsManager(random);

    private boolean isDisposed = false;

    public Level(int x, int y, GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        // Хак, чтобы обойти момент с тем, что генератор складно выдает уровни лишь слева направо, снизу вверх
        if (y > 0 && gameScreen.worldMap.getLevel(x + 1, y - 1) == null) {
            gameScreen.worldMap.addLevel(x + 1, y - 1, gameScreen.constantsConfig);
        }

        LevelMap levelMap = initMap(gameScreen.worldMap.addLevel(x, y, gameScreen.constantsConfig),
                gameScreen.constantsConfig);
        mushroomsManager.initMushrooms(new ArrayList<Mushroom>());
        initForester(x, y, getRoomsRectangles(levelMap));
        exitsManager.init(levelMap.getExits(Side.RIGHT));
    }

    private LevelMap initMap(LevelMap levelMap, ConstantsConfig constantsConfig) {

        this.mapWidth = constantsConfig.getLevelWidth();
        this.mapHeight= constantsConfig.getLevelHeight();

        Set<Segment> treesSegments = levelMap.getSegments();

        for (Segment s : treesSegments) {
            Actor group = getActorFromCell(s.getValue(),
                    s.getX() * GameScreen.tileSize,
                    s.getY() * GameScreen.tileSize,
                    constantsConfig);
            if (group != null) trees.add(group);
        }

        Set<Vector2> nodes = new HashSet<>();

        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                if (!isSegment(i, j, treesSegments)) {
                    nodes.add(new Vector2(i , j));
                        forestGraph.addNode(GameScreen.tileSize * i, GameScreen.tileSize * j);
                }
            }
        }

        for (Vector2 node : nodes) {
            if (isTileAvailable(node, treesSegments)) {
                addNeighbours(node);
            }
        }

        return levelMap;
    }

    private boolean isSegment(int x, int y, Set<Segment> treesSegments) {
        for (Segment segment : treesSegments) {
            if (segment.getX() == x && segment.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private Actor getActorFromCell(int value, int x, int y, ConstantsConfig constantsConfig) {
        if (value == constantsConfig.getTreeTopLeft()) {
            return getObstacle("tree", 0, x, y);
        } else if (value == constantsConfig.getTreeTopRight()) {
            return getObstacle("tree", 2, x, y);
        } else if (value == constantsConfig.getTreeBottomLeft()) {
            return getObstacle("tree", 1, x, y);
        } else if (value == constantsConfig.getTreeBottomRight()) {
            return getObstacle("tree", 3, x, y);
        } if (value == constantsConfig.getStone()) {
            return getObstacle("stone", 0, x, y);
        } else {
            return null;
        }
    }

    private Actor getObstacle(String name, int value, int x, int y) {
        return new TreePart(ResourcesManager.get(String.format(Locale.getDefault(), "%s_%d", name, value)), x, y,
                GameScreen.tileSize, GameScreen.tileSize);
    }

    private void addNeighbours(Vector2 origin) {

        float fromX = GameScreen.tileSize * origin.x;
        float fromY = GameScreen.tileSize * origin.y;

        for (Vector2 node : neighbours) {
            if (node != null && (node.x != origin.x || node.y != origin.y)) {
                forestGraph.addConnection(fromX, fromY,
                        GameScreen.tileSize * node.x,
                        GameScreen.tileSize * node.y);
            }
        }
    }

    private boolean isTileAvailable(Vector2 origin, Set<Segment> segments) {

        neighbours.clear();
        neighbours.add(origin);
        if (origin.x > 0) neighbours.add(new Vector2(origin.x - 1, origin.y));
        if (origin.x < mapWidth - 1) neighbours.add(new Vector2(origin.x + 1, origin.y));
        if (origin.y < mapHeight - 1) neighbours.add(new Vector2(origin.x, origin.y + 1));
        if (origin.y > 0) neighbours.add(new Vector2(origin.x, origin.y - 1));

        int index;
        for (Segment segment : segments) {
            index = neighbours.indexOf(new Vector2(segment.getX(), segment.getY()));
            if (index != -1) neighbours.set(index, null);
        }

        return neighbours.contains(origin);
    }

    public void dispose() {
        if (isDisposed) return;
        mushroomsManager.dispose();
        exitsManager.dispose();
        forestGraph = null;
        foresters.clear();
        neighbours.clear();
        trees.clear();
        gameScreen = null;
        isDisposed = true;
    }

    public Player getPlayer() {
        return gameScreen.player;
    }

    public void resetPlayerPath(float x, float y) {
        GraphPath<Vector2> path = forestGraph.getPath(
                Utils.mapCoordinate(gameScreen.player.getX()),
                Utils.mapCoordinate(gameScreen.player.getY()),
                Utils.mapCoordinate(x),
                Utils.mapCoordinate(y));

        gameScreen.player.stop();

        // Первая точка пути совпадает с координатами игрока,
        // чтобы игрок не стоял на месте лишнее время ее из пути удаляем.
        for (int i = 1; i < path.getCount(); i++) {
            gameScreen.player.path.add(new Vector2(path.get(i)));
        }

        SequenceAction playerActionsSequence = gameScreen.player.getMoveActionsSequence();
        gameScreen.player.addAction(playerActionsSequence);
    }

    private void initForester(int levelX, int levelY, Rectangle[] roomsRectangles) {
        this.foresters = new ArrayList<>();
        Vector2 playerPosition;

        if (levelX == 0 && levelY == 0) {
            playerPosition = new Vector2(0, 1);
        } else {
            playerPosition = new Vector2(gameScreen.player.getX(), gameScreen.player.getY());
        }

        for (Rectangle rectangle : roomsRectangles) {
            if (!rectangle.contains(playerPosition) && rectangle.height > 1) {
                boolean generateDifficultWay = false;
                if (rectangle.height > 3) generateDifficultWay = random.nextBoolean();

                if (generateDifficultWay) {
                    Array<Vector2> points = new Array<>();
                    float y0 = rectangle.y + (rectangle.height - 1);
                    float y1 = rectangle.y + 1;

                    points.add(new Vector2(GameScreen.tileSize * (rectangle.x + 1), GameScreen.tileSize * y0));
                    points.add(new Vector2(GameScreen.tileSize * (rectangle.width - 2), GameScreen.tileSize * y0));
                    points.add(new Vector2(GameScreen.tileSize * (rectangle.width - 2), GameScreen.tileSize * y1));
                    points.add(new Vector2(GameScreen.tileSize * (rectangle.x + 1), GameScreen.tileSize * y1));

                    this.foresters.add(new Forester(points, "enemy.txt"));
                } else {
                    float y = GameScreen.tileSize * MathUtils.random(rectangle.y, rectangle.y + (rectangle.height - 2));
                    this.foresters.add(new Forester(
                        new Vector2(GameScreen.tileSize * (rectangle.x + 1), y),
                        new Vector2(GameScreen.tileSize * (rectangle.width - 2), y), "enemy.txt"));
                }
            }
        }
    }

    public void update(float delta, float gameTime) {
        updateForesters(delta);
        mushroomsManager.updateMushrooms(gameTime, gameScreen.player);
        exitsManager.updateExits(gameTime);
    }

    private void updateForesters(float delta) {
        for (Forester forester : foresters) {
            result.set(0f, 0f, 0f, 0f);
            if (isPlayerCaught(forester.bounds, gameScreen.player.bounds)) {
                gameScreen.gameOver = true;
                gameScreen.player.stop();
                forester.stop();
                forester.path.add(new Vector2(gameScreen.player.getX(), gameScreen.player.getY()));
                forester.addAction(forester.getMoveActionsSequence());
            } else if (gameScreen.isGameOver()) {
                forester.stop();
            } else {
                updateForestersPath(forester, delta);
            }
        }
    }

    private boolean isPlayerCaught(Rectangle f, Rectangle p) {
        Intersector.intersectRectangles(f, p, result);
        float resultArea = result.area();
        return resultArea >= caughtArea;
    }

    private void updateForestersPath(Forester forester, float delta) {
        forester.updateMoving(gameScreen.player, delta);
        if (forester.isPursuePlayer()) {
            setForesterPath(forester, gameScreen.player.bounds.x, gameScreen.player.bounds.y);
        }
    }

    private void setForesterPath(Forester forester, float tx, float ty) {
        GraphPath<Vector2> path = forestGraph.getPath(
                Utils.mapCoordinate(forester.getX()),
                Utils.mapCoordinate(forester.getY()),
                Utils.mapCoordinate(tx),
                Utils.mapCoordinate(ty));

        forester.stop();

        // Первая точка пути совпадает с координатами игрока,
        // чтобы лесник не стоял на месте лишнее время ее из пути удаляем.
        int start = path.getCount() > 1 ? 1 : 0;
        for (int i = start; i < path.getCount(); i++) {
            forester.path.add(new Vector2(path.get(i)));
        }

        forester.addAction(forester.getMoveActionsSequence());
    }

    public List<Mushroom> getMushrooms() {
        return mushroomsManager.mushrooms;
    }

    public List<Speech> getMushroomsSpeeches() {
        return mushroomsManager.mushroomsSpeeches;
    }

    public List<Speech> getExitsSpeeches() {
        return exitsManager.exitsSpeeches;
    }

    private Rectangle[] getRoomsRectangles(LevelMap levelMap) {
        List<Room> rooms = levelMap.getRooms();
        int size = rooms.size();
        Rectangle[] rectangles = new Rectangle[size];

        for (int i = 0; i < size; i++) {
            Room room = rooms.get(i);
            rectangles[i] = new Rectangle(0, room.getY()
                    , mapWidth,room.getHeight() - 2);
        }
        return rectangles;
    }
}