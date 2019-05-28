package kifio.leningrib.levels;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.HashSet;
import java.util.Set;

import kifio.leningrib.Utils;
import kifio.leningrib.model.TextureManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public abstract class Level {

    public TiledMap map;
    public Player player;
    public Forester forester;
    public int mapWidth;
    public int mapHeight;

    // Граф поиска пути
    private ForestGraph forestGraph = new ForestGraph();

    // Деревья
    public Set<Group> trees = new HashSet<>();

    // Множество зон, в которые нельзя попасть
    public Set<Rectangle> unreachableBounds = new HashSet<>();

    public Level(String fileName) {
        init(fileName);
    }

    public void init(String fileName) {
        initPlayer();
        initForester();
        initMap(fileName);
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
                0f,
                "player.txt");
    }

    private void initForester() {
        Vector2 from = new Vector2(
                GameScreen.tileSize * 1f,
                GameScreen.tileSize * 4f);

        Vector2 to = new Vector2(
                GameScreen.tileSize * 4f,
                GameScreen.tileSize * 4f);

        this.forester = new Forester(from, to,
                "enemy.txt");
    }

    // TODO: Сделать парсинг карты и инициализацию групп актеров.
    private void initMushrooms() {
//        Group group = new Group();
//        group.addActor(new MovableActor(tileSize * 3, tileSize * 3, -1,
//                tileSize, "power_mushroom.txt"));
//        trees.add(group);
    }

    private void initTrees() {
        // TODO: Разные группы будут отвечать за разные эффекты деревьев
//        Group group = new Group();
//        addTree(group, tileSize, 3 * tileSize);
//        addTree(group, tileSize * 3, 5 * tileSize);
//        addTree(group, tileSize * 4, 2 * tileSize);
//
//        addTopLeftSegment(group, tileSize, 0);
//        addTopRightSegment(group, 2 * tileSize, 0);
//
//        addBottomRightSegment(group, 0, 4 * tileSize);
//        addTopRightSegment(group, 0, 5 * tileSize);
//
//        trees.add(group);
    }

    private void addTree(Group group, int x, int y) {
        addTopLeftSegment(group, x, y);
        addBottomLeftSegment(group, x, y - GameScreen.tileSize);
        addTopRightSegment(group, x + GameScreen.tileSize, y);
        addBottomRightSegment(group, x + GameScreen.tileSize, y - GameScreen.tileSize);
    }

    private void addTopLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_0"), x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    private void addTopRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_3"), x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    private void addBottomLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_1"), x, y, GameScreen.tileSize, GameScreen.tileSize));
        unreachableBounds.add(new Rectangle(x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    private void addBottomRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_2"), x, y, GameScreen.tileSize, GameScreen.tileSize));
        unreachableBounds.add(new Rectangle(x, y, GameScreen.tileSize, GameScreen.tileSize));
    }

    public void startMoving() {
        SequenceAction playerActionsSequence = player.getMoveActionsSequence();
        player.addAction(playerActionsSequence);
    }
}
