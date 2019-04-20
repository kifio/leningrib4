package kifio.leningrib.levels;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.HashSet;
import java.util.Set;

import kifio.leningrib.model.MovableActor;
import kifio.leningrib.model.TextureManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.screens.GameScreen;

public abstract class Level {

    protected int tileSize;

    public TiledMap map;
    public MovableActor player;
    public MovableActor forester;

    // Деревья
    public Set<Group> trees = new HashSet<>();

    // Множестов зон, в которые нельзя попасть
    public Set<Rectangle> unreachableBounds = new HashSet<>();

    public Level(GameScreen gameScreen) {
        tileSize = gameScreen.tileSize;
        init();
    }

    public void init() {
        initPlayer();
        initForester();
        initMap();
//        initTrees();
//        initMushrooms();
    }

    // TODO: не использовать TileMap, использовать свой формат карты и хранить ее в json
    private void initMap() {
        map = new TmxMapLoader().load("lvl_0.tmx");
        for (MapLayer layer : map.getLayers()) {
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
            handleLayers(tileLayer.getWidth(), tileLayer.getHeight(), tileLayer);
        }
    }

    private void handleLayers(int columns, int rows, TiledMapTileLayer layer) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                TiledMapTileLayer.Cell cell = layer.getCell(j, i);
                if (cell != null) addTreeSegment(j * tileSize, i * tileSize, cell.getTile());
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

    private void initPlayer() {
        this.player = new MovableActor(
                0f,
                0f,
                tileSize,
                "player.txt");
    }

    private void initForester() {
        this.forester = new MovableActor(
                tileSize * 3f,
                tileSize * 4f,
                tileSize,
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
        addBottomLeftSegment(group, x, y - tileSize);
        addTopRightSegment(group, x + tileSize, y);
        addBottomRightSegment(group, x + tileSize, y - tileSize);
    }

    private void addTopLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_0"), x, y, tileSize, tileSize));
    }

    private void addTopRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_3"), x, y, tileSize, tileSize));
    }

    private void addBottomLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_1"), x, y, tileSize, tileSize));
        unreachableBounds.add(new Rectangle(x, y, tileSize, tileSize));
    }

    private void addBottomRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_2"), x, y, tileSize, tileSize));
        unreachableBounds.add(new Rectangle(x, y, tileSize, tileSize));
    }

    public boolean isUnreachableZone(Vector2 point) {
        for (Rectangle r : unreachableBounds)
            if (r.contains(point)) return true;
        return false;
    }
}
