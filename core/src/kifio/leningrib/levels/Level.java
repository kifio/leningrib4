package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import kifio.leningrib.Utils;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public abstract class Level {

    public TiledMap map;
    public Player player;
    public List<Forester> foresters;
    public int mapWidth;
    public int mapHeight;

    private Rectangle result = new Rectangle();

    private static float caughtArea = 0.5f * GameScreen.tileSize * GameScreen.tileSize;

    // Граф поиска пути
    private ForestGraph forestGraph = new ForestGraph();

    // Деревья
    public Set<Group> trees = new HashSet<>();
    public Set<Mushroom> mushrooms = new HashSet<>();

    // Множество зон, в которые нельзя попасть
    public Set<Rectangle> unreachableBounds = new HashSet<>();

    public Level(String levelName) {
        init(levelName);
    }

    public void init(String levelName) {
        initMushrooms("mushrooms_" + levelName);
        initPlayer();
        initForester();
        initMap(levelName + ".tmx");
    }

    // TODO: не использовать TileMap, использовать свой формат карты и хранить ее в json
    private void initMap(String fileName) {
        map = new TmxMapLoader().load(fileName);
        mapWidth = (int) map.getProperties().get("width");
        mapHeight = (int) map.getProperties().get("height");

        for (MapLayer layer : map.getLayers()) {
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
            handleLayer(tileLayer.getWidth(), tileLayer.getHeight(), tileLayer);
        }

        TiledMapTileLayer treesLayer = (TiledMapTileLayer) map.getLayers().get(1);

        for (int i = 0; i < treesLayer.getHeight(); i++) {
            for (int j = 0; j < treesLayer.getWidth(); j++) {
                forestGraph.addNode(j * GameScreen.tileSize, i * GameScreen.tileSize);
            }
        }

        for (int i = 0; i < treesLayer.getHeight(); i++) {
            for (int j = 0; j < treesLayer.getWidth(); j++) {
                addNeighbours(j, i, treesLayer);
            }
        }
    }

    private void handleLayer(int columns, int rows, TiledMapTileLayer layer) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                TiledMapTileLayer.Cell cell = layer.getCell(j, i);
                if (cell != null) {
                    addTreeSegment(j * GameScreen.tileSize, i * GameScreen.tileSize, cell.getTile());
                }
            }
        }
    }

    private void addTreeSegment(int x, int y, TiledMapTile tile) {
        if (tile == null) return;
        MapProperties properties = tile.getProperties();
        if (properties.containsKey("index")) {
            Group group = new Group();
            switch ((int) properties.get("index")) {
                case 0:
                    addTopLeftSegment(group, x, y);
                    break;
                case 1:
                    addBottomLeftSegment(group, x, y);
                    break;
                case 2:
                    addTopRightSegment(group, x, y);
                    break;
                case 3:
                    addBottomRightSegment(group, x, y);
                    break;
            }
            trees.add(group);
        }
    }

    private void addNeighbours(int x, int y, TiledMapTileLayer layer) {
        if (!isTileAvailable(layer.getCell(x, y))) return;

        float fromX = GameScreen.tileSize * x;
        float fromY = GameScreen.tileSize * y;

        float toX, toY;

        if (x > 0 && isTileAvailable(layer.getCell(x - 1, y))) {
            toX = GameScreen.tileSize * (x - 1);
            toY = GameScreen.tileSize * y;
            forestGraph.addConnection(fromX, fromY, toX, toY);
        }

        if (x < mapWidth - 1 && isTileAvailable(layer.getCell(x + 1, y))) {
            toX = GameScreen.tileSize * (x + 1);
            toY = GameScreen.tileSize * y;
            forestGraph.addConnection(fromX, fromY, toX, toY);
        }

        if (y < mapHeight - 1 && isTileAvailable(layer.getCell(x, y + 1))) {
            toX = GameScreen.tileSize * x;
            toY = GameScreen.tileSize * (y + 1);
            forestGraph.addConnection(fromX, fromY, toX, toY);
        }

        if (y > 0 && isTileAvailable(layer.getCell(x, y - 1))) {
            toX = GameScreen.tileSize * x;
            toY = GameScreen.tileSize * (y - 1);
            forestGraph.addConnection(fromX, fromY, toX, toY);
        }
    }

    public void resetPath(float x, float y) {
        GraphPath<Vector2> path = forestGraph.getPath(
                Utils.mapCoordinate(player.getX()),
                Utils.mapCoordinate(player.getY()),
                Utils.mapCoordinate(x),
                Utils.mapCoordinate(y));
        player.stop();

        // Первая точка пути совпадает с координатами игрока,
        // чтобы игрок не стоял на месте лишнее время ее из пути удаляем.
        for (int i = 1; i < path.getCount(); i++) {
            player.path.add(new Vector2(path.get(i)));
        }

        SequenceAction playerActionsSequence = player.getMoveActionsSequence();
        player.addAction(playerActionsSequence);
    }

    private boolean isTileAvailable(TiledMapTileLayer.Cell cell) {
        if (cell == null) return true;
        TiledMapTile tile = cell.getTile();
        if (tile == null) return true;
        MapProperties properties = tile.getProperties();
        if (properties.containsKey("index")) {
            int index = (int) properties.get("index");
            return (index == 0 || index == 2);
        } else {
            return true;
        }
    }

    private void initPlayer() {
        this.player = new Player(
                0f,
                10f,
                "player.txt");
    }

    private void initForester() {
        this.foresters = new ArrayList<>();
        this.foresters.add(new Forester(
                new Vector2(
                        GameScreen.tileSize * 1f,
                        GameScreen.tileSize * 13f),
                new Vector2(
                        GameScreen.tileSize * 4f,
                        GameScreen.tileSize * 13f), "enemy.txt"));

        this.foresters.add(new Forester(
                new Vector2(
                        GameScreen.tileSize * 1f,
                        GameScreen.tileSize * 4f),
                new Vector2(
                        GameScreen.tileSize * 4f,
                        GameScreen.tileSize * 4f), "enemy.txt"));

        this.foresters.add(new Forester(
                new Vector2(
                        GameScreen.tileSize * 0f,
                        GameScreen.tileSize * 24f),
                new Vector2(
                        GameScreen.tileSize * 4f,
                        GameScreen.tileSize * 24f), "enemy.txt"));
    }

    private static final String NEW_LINE = "\n";
    private static final String COMMA = ",";
    private static final String POWER_MUSHROOM = "power_mushroom.txt";

    // TODO: Сделать парсинг карты и инициализацию групп актеров.
    private void initMushrooms(String fileName) {
        try {
            FileHandle handle = Gdx.files.internal(fileName);
            String content = handle.readString();
            String[] positions = content.split(NEW_LINE);

            for (String position : positions) {
                String[] coordinates = position.split(COMMA);
                mushrooms.add(new Mushroom(Integer.parseInt(coordinates[0]),
                        Integer.parseInt(coordinates[1]), POWER_MUSHROOM));
            }
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
    }

    private void addTopLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(ResourcesManager.get("tree_0"), x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    private void addTopRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(ResourcesManager.get("tree_3"), x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    private void addBottomLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(ResourcesManager.get("tree_1"), x, y, GameScreen.tileSize, GameScreen.tileSize));
        unreachableBounds.add(new Rectangle(x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    private void addBottomRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(ResourcesManager.get("tree_2"), x, y, GameScreen.tileSize, GameScreen.tileSize));
        unreachableBounds.add(new Rectangle(x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    public void update(float delta) {
        updateForesters(delta);
        updateMushrooms();
    }

    private void updateForesters(float delta) {
        for (Forester forester : foresters) {
            result.set(0f, 0f, 0f, 0f);
            if (isPlayerCaught(forester.bounds, player.bounds)) {
                GameScreen.gameOver = true;
                player.stop();
                forester.stop();
                forester.path.add(new Vector2(player.getX(), player.getY()));
                forester.addAction(forester.getMoveActionsSequence());
            } else if (GameScreen.gameOver) {
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
        forester.updateMoving(player, delta);
        if (forester.isPursuePlayer()) {
            setForesterPath(forester, player.getX(), player.getY());
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
        for (int i = 1; i < path.getCount(); i++) {
            forester.path.add(new Vector2(path.get(i)));
        }

        forester.addAction(forester.getMoveActionsSequence());
    }

    private void updateMushrooms() {
        Iterator<Mushroom> iterator = mushrooms.iterator();
        while (iterator.hasNext()) {
            Mushroom m = iterator.next();
            if (m.bounds.overlaps(player.bounds)) {
                m.remove();
                iterator.remove();
            }
        }
    }
}