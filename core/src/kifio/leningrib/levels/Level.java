package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import kifio.leningrib.Utils;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;
import model.GeneratorKt;
import model.LevelConstantsKt;
import model.LevelMap;
import model.Segment;
import model.WorldMap;

public class Level {

    public Player player;
    public List<Forester> foresters;

    // FIXME: these parameters should be passed to lib.
    public int mapWidth = LevelConstantsKt.LEVEL_WIDTH;
    public int mapHeight = LevelConstantsKt.LEVEL_HEIGHT;

    private static final int INITIAL_NEIGHBORS_CAPACITY = 5;
    private List<Tile> neighbours = new ArrayList<>(INITIAL_NEIGHBORS_CAPACITY);

    private Rectangle result = new Rectangle();
    private Random random = new Random();

    private static float caughtArea = 0.5f * GameScreen.tileSize * GameScreen.tileSize;

    // Граф поиска пути
    private ForestGraph forestGraph = new ForestGraph();

    // Объекты
    public ArrayList<Actor> trees = new ArrayList<>();
    public ArrayList<Mushroom> mushrooms = new ArrayList<>();
    public ArrayList<Speech> speeches = new ArrayList<>(8);

    public Level(int x, int y, WorldMap worldMap) {
        initMap(x, y, worldMap);
        initPlayer();
        initMushrooms("mushrooms_lvl_1");
        initForester();
    }

    private void initMap(int x, int y, WorldMap worldMap) {

        LevelMap levelMap = GeneratorKt.generateLevel(x, y, worldMap);
        Set<Segment> treesSegments = levelMap.getSegments();

        for (Segment s : treesSegments) {
            handleCell(s.getValue(), s.getX() * GameScreen.tileSize, s.getY() * GameScreen.tileSize);
        }

        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                forestGraph.addNode(GameScreen.tileSize * i, GameScreen.tileSize *j);
            }
        }

        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                addNeighbours(i, j, treesSegments);
            }
        }
    }

    private void handleCell(int value, int x, int y) {
        Actor group = getActorFromCell(value, x, y);
        if (group != null) trees.add(group);
    }

    private Actor getActorFromCell(int value, int x, int y) {
        if (value == LevelConstantsKt.TREE_TOP_LEFT) {
            return getObstacle("tree", 0, x, y);
        } else if (value == LevelConstantsKt.TREE_TOP_RIGHT) {
            return getObstacle("tree", 2, x, y);
        } else if (value == LevelConstantsKt.TREE_BOTTOM_LEFT) {
            return getObstacle("tree", 1, x, y);
        } else if (value == LevelConstantsKt.TREE_BOTTOM_RIGHT) {
            return getObstacle("tree", 3, x, y);
        } if (value == LevelConstantsKt.STONE) {
            return getObstacle("stone", 0, x, y);
        } else {
            return null;
        }
    }

    private Actor getObstacle(String name, int value, int x, int y) {
        return new TreePart(ResourcesManager.get(String.format(Locale.getDefault(), "%s_%d", name, value)), x, y,
                GameScreen.tileSize, GameScreen.tileSize);
    }

    // FIXME: Very non optimized code
    private void addNeighbours(int x, int y, Set<Segment> segments) {
        if (isTileAvailable(x, y, segments)) {

            float fromX = GameScreen.tileSize * x;
            float fromY = GameScreen.tileSize * y;

            for (Tile tile : neighbours) {
                if (tile != null && (tile.x != x || tile.y != y)) {
                    forestGraph.addConnection(fromX, fromY,
                            GameScreen.tileSize * tile.x,
                            GameScreen.tileSize * tile.y);
                }
            }
        }
    }

    private boolean isTileAvailable(int x, int y, Set<Segment> segments) {

        Tile origin = new Tile(x, y);

        neighbours.clear();
        neighbours.add(origin);
        if (x > 0) neighbours.add(new Tile(x - 1, y));
        if (x < mapWidth - 1) neighbours.add(new Tile(x + 1, y));
        if (y < mapHeight - 1) neighbours.add(new Tile(x, y - 1));
        if (y > 0) neighbours.add(new Tile(x, y + 1));

        int index;
        for (Segment segment : segments) {
            index = neighbours.indexOf(new Tile(segment.getX(), segment.getY()));
            if (index != -1) neighbours.set(index, null);
        }

        return neighbours.contains(origin);
    }

    private static class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Tile)) return false;
            Tile other = (Tile) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }
    }

    public void resetPlayerPath(float x, float y) {
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

    private void initPlayer() {
        this.player = new Player(
                0f, GameScreen.tileSize, "player.txt");
    }

    private void initForester() {
        this.foresters = new ArrayList<>();
//        this.foresters.add(new Forester(
//                new Vector2(
//                        GameScreen.tileSize * 0f,
//                        GameScreen.tileSize * 13f),
//                new Vector2(
//                        GameScreen.tileSize * 4f,
//                        GameScreen.tileSize * 13f), "enemy.txt"));

//        this.foresters.add(new Forester(
//                new Vector2(
//                        GameScreen.tileSize * 1f,
//                        GameScreen.tileSize * 5f),
//                new Vector2(
//                        GameScreen.tileSize * 4f,
//                        GameScreen.tileSize * 5f), "enemy.txt"));

//        this.foresters.add(new Forester(
//                new Vector2(
//                        GameScreen.tileSize * 0f,
//                        GameScreen.tileSize * 23f),
//                new Vector2(
//                        GameScreen.tileSize * 4f,
//                        GameScreen.tileSize * 23f), "enemy.txt"));
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

    private void addSpeech(Mushroom m, float stateTime) {

        for (Speech speech : speeches) {
            if (speech.getMushroom().equals(m)) return;
        }

        // С некоторой вероятностью добавляем новую речь
        if (random.nextInt(128) / 8 == 0) {
            speeches.add(new Speech(m, stateTime, SpeechManager.getInstance().getRandomSpeech()));
        }
    }

    private Actor getTopLeftSegment(int x, int y) {
        return new TreePart(ResourcesManager.get("tree_0"), x, y, GameScreen.tileSize, GameScreen.tileSize);
    }

    private Actor getTopRightSegment(int x, int y) {
        return new TreePart(ResourcesManager.get("tree_3"), x, y, GameScreen.tileSize, GameScreen.tileSize);
    }

    private Actor getBottomLeftSegment(int x, int y) {
        return new TreePart(ResourcesManager.get("tree_1"), x, y, GameScreen.tileSize, GameScreen.tileSize);
    }

    private Actor getBottomRightSegment(int x, int y) {
        return new TreePart(ResourcesManager.get("tree_2"), x, y, GameScreen.tileSize, GameScreen.tileSize);
    }

    public void update(float delta, float gameTime) {
        updateForesters(delta);
        updateMushrooms(gameTime);
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
            setForesterPath(forester, player.bounds.x, player.bounds.y);
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

    private void updateMushrooms(float stateTime) {

        // Удаляем просроченные реплики
        Iterator<Speech> speechIterator = speeches.iterator();
        while (speechIterator.hasNext()) {
            Speech sp = speechIterator.next();
            if (stateTime - sp.getStartTime() > 1) {
                sp.dispose();
                speechIterator.remove();
            }
        }

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        Iterator<Mushroom> iterator = mushrooms.iterator();
        while (iterator.hasNext()) {
            Mushroom m = iterator.next();
            if (m.bounds.overlaps(player.bounds)) {
                m.remove();
                iterator.remove();
                player.increaseMushroomCount();
            } else if (player.getMushroomsCount() > 0) {
                if (speeches.size() > 0) return;
                addSpeech(m, stateTime);
            }
        }
    }
}