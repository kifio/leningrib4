package kifio.leningrib.model.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import generator.ConstantsConfig;
import java.util.HashMap;
import java.util.Locale;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.ForestersManager;
import kifio.leningrib.levels.TreesManager;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.screens.GameScreen;

public class ForestGraph implements IndexedGraph<Vector2> {

	private Array<Connection<Vector2>> empty = new Array<>();
	private ForestersManager forestersManager;
	private Grandma grandma;

	private int mapWidth;
	private int mapHeight;

	// Массив позиций лесников, чтобы не перестраивать граф, когда ничего не сдвинулся с места.
	private Vector2[] currentActorsPositions;

	// соединениея между нодами
	private HashMap<Integer, Array<Connection<Vector2>>> connections = new HashMap<>();

	private Heuristic<Vector2> heuristic = new Heuristic<Vector2>() {
		@Override public float estimate(Vector2 node, Vector2 endNode) {
			return node.dst2(endNode);
		}
	};

	// ноды - прямоугольники. дерево - 4 ноды
	private Array<Vector2> nodes = new Array<>();

	// Инициализирует ноды, которые могут использоваться для поиска маршрута
	public ForestGraph(ConstantsConfig constantsConfig,
		TreesManager treesManager,
		ForestersManager forestersManager,
		Grandma grandma) {

		// На первом уровне будут бабка и лесник.
		// На остальных пока только лесники.
		if (grandma != null) {
			currentActorsPositions = new Vector2[2];
			currentActorsPositions[0] = new Vector2(grandma.getX(), grandma.getY());
			currentActorsPositions[1] = new Vector2(GameScreen.tileSize * 3, GameScreen.tileSize * 27);
		} else {
			int size = forestersManager.gameObjects.size;
			currentActorsPositions = new Vector2[size];
			for (int i = 0; i < size; i++) {
				Actor actor = forestersManager.gameObjects.get(i);
				currentActorsPositions[i] = new Vector2(actor.getX(), actor.getY());
			}
		}

		this.grandma = grandma;
		this.forestersManager = forestersManager;
		this.mapWidth = constantsConfig.getLevelWidth() * GameScreen.tileSize;
		this.mapHeight = constantsConfig.getLevelHeight() * GameScreen.tileSize;

		int x, y;

		for (int i = 0; i < constantsConfig.getLevelWidth(); i++) {
			for (int j = 0; j < constantsConfig.getLevelHeight(); j++) {
				x = GameScreen.tileSize * i;
				y = GameScreen.tileSize * j;
				if (!isObstacle(x, y, treesManager.trees) && !isActor(i, j, grandma)) {
					nodes.add(new Vector2(x, y));
				}
			}
		}

		for (int i = 0; i < nodes.size; i++) {
			addNeighbours(nodes.get(i));
		}
	}


	@Override public int getIndex(Vector2 node) {
		return nodes.indexOf(node, true);
	}

	@Override public int getNodeCount() {
		return nodes.size;
	}

	@Override public Array<Connection<Vector2>> getConnections(Vector2 fromVector2) {
		Vector2 vec = getVector2(fromVector2.x, fromVector2.y);
		int index = this.nodes.indexOf(vec, false);
		if (index == -1) { return empty; }
		return this.connections.get(index);
	}

	public void updateForestGraph(float cameraPositionY) {

		if (!isGraphChanged(cameraPositionY)) return;

		long start = System.currentTimeMillis();

		for (int i = 0; i < nodes.size; i++) {
			addNeighbours(nodes.get(i));
		}

//		Gdx.app.log("kifio", "updateForestGraph: " + (System.currentTimeMillis() - start) / 1000);
	}

	private boolean isGraphChanged(float cameraPositionY) {
		boolean isChanged = false;
		int size = currentActorsPositions.length;
		if (grandma != null) {

		} else {
			for (int i = 0; i < size; i++) {
				Actor actor = forestersManager.gameObjects.get(i);
				int oldX = (int) currentActorsPositions[i].x;
				int oldY = (int) currentActorsPositions[i].y;
				int newX = (int) Utils.mapCoordinate(actor.getX());
				int newY = (int) Utils.mapCoordinate(actor.getY());

				if (isActorOnScreen(actor, cameraPositionY) && (oldX != newX || oldY != newY)) {
					isChanged = true;
					Gdx.app.log("kifio", String.format(Locale.getDefault(),
						"oldx:%d; newx:%d; oldy:%d; newy:%d", oldX, newX, oldY, newY));
					currentActorsPositions[i].x = newX;
					currentActorsPositions[i].y = newY;
				}
			}
		}

		return isChanged;
	}

	private boolean isActorOnScreen(Actor actor, float cameraPositionY) {
		return actor.getY() >= cameraPositionY - (Gdx.graphics.getHeight() / 2f)
			&& actor.getY() <= cameraPositionY + (Gdx.graphics.getHeight() / 2f);
	}

	private void addNeighbours(Vector2 origin) {

		if (origin.x > 0) {
			addConnection(origin, origin.x - GameScreen.tileSize, origin.y);
		}

		if (origin.x < mapWidth - 1) {
			addConnection(origin, origin.x + GameScreen.tileSize, origin.y);
		}

		if (origin.y < mapHeight - 1) {
			addConnection(origin, origin.x, origin.y + GameScreen.tileSize);
		}

		if (origin.y > 0) {
			addConnection(origin, origin.x, origin.y - GameScreen.tileSize);
		}
	}

	// Добавление пути между двумя нодами
	private void addConnection(Vector2 from, float toX, float toY) {

//		Vector2 from = getVector2(fromX, fromY);
		Vector2 to = getVector2(toX, toY);

		if (to == null) { return; }

		int fromIndex = nodes.indexOf(from, true);

		Array<Connection<Vector2>> connections = this.connections.get(fromIndex);

		if (connections == null) {
			connections = new Array<>();
			this.connections.put(fromIndex, connections);
		}

		connections.add(new PointsConnection(from, to));
	}

	// Поиск маршрута в графе
	public void updatePath(float fromX, float fromY, float toX, float toY, GraphPath<Vector2> path) {
		path.clear();
		Vector2 f = getVector2(fromX, fromY);
		Vector2 t = getVector2(toX, toY);
		if (f != null && t != null) {
			IndexedAStarPathFinder<Vector2> pathFinder = new IndexedAStarPathFinder<>(this);
			pathFinder.searchNodePath(f, t, heuristic, path);
		}
	}

	// Поиск ноды, чтобы не создавать новую.
	// TODO: Эффективнее использовать SpatialMap
	private Vector2 getVector2(float x, float y) {
		for (int i = 0; i < nodes.size; i++) {
			Vector2 node = nodes.get(i);
			if (node.x == x && node.y == y) {
				return node;
			}
		}
		return null;
	}

	// Если на ноде находится другой персонаж, мы не строим маршрут через нее
	private boolean isActor(int x, int y, Actor actor) {
		if (actor == null) { return false; }
		return (int) actor.getX() == x && (int) actor.getY() == y;
	}

	// Нодой может быть только клетка, на которой нет дерева
	private boolean isObstacle(int x, int y, Array<Actor> treesSegments) {
		for (int i = 0; i < treesSegments.size; i++) {
			Actor segment = treesSegments.get(i);
			if (segment.getX() == x && segment.getY() == y) {
				return true;
			}
		}
		return false;
	}

	public void dispose() {
		this.grandma = null;
		this.forestersManager = null;
		this.connections.clear();
		this.nodes.clear();
		this.connections = null;
		this.nodes = null;
	}
}
