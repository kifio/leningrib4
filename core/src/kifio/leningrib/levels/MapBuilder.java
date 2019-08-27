package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import generator.ConstantsConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;
import model.Segment;

public class MapBuilder {

	int mapWidth;
	int mapHeight;

	private static final int INITIAL_NEIGHBORS_CAPACITY = 5;

	private List<Vector2> neighbours = new ArrayList<>(INITIAL_NEIGHBORS_CAPACITY);
	private ArrayList<Actor> trees = new ArrayList<>();

	LevelMap initMap(LevelMap levelMap, ConstantsConfig constantsConfig, ForestGraph forestGraph) {

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

		// TODO: Тоже возможно больше не нужно
		for (Vector2 node : nodes) {
			if (isTileAvailable(node, treesSegments)) {
				addNeighbours(node, forestGraph);
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

	static Actor getActorFromCell(int value, int x, int y, ConstantsConfig constantsConfig) {
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

	private static Actor getObstacle(String name, int value, int x, int y) {
		return new TreePart(ResourcesManager.get(String.format(Locale.getDefault(), "%s_%d", name, value)), x, y,
			GameScreen.tileSize, GameScreen.tileSize);
	}

	private void addNeighbours(Vector2 origin, ForestGraph forestGraph) {

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

	Rectangle[] getRoomsRectangles(LevelMap levelMap) {
		List<Room> rooms = levelMap.getRooms();
		int size = rooms.size();
		Rectangle[] rectangles = new Rectangle[size];

		for (int i = 0; i < size; i++) {
			Room room = rooms.get(i);
			rectangles[i] = new Rectangle(0, room.getY(), mapWidth,room.getHeight() - 2);
		}
		return rectangles;
	}

	public List<Actor> getTrees() {
		return trees;
	}

	public void dispose() {
		neighbours.clear();
		Iterator<Actor> treesIterator = trees.iterator();
		while (treesIterator.hasNext()) {
			treesIterator.next().remove();
			treesIterator.remove();
		}
		trees = null;
		neighbours = null;
	}
}
