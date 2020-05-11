package kifio.leningrib.model.pathfinding;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.rmi.CORBA.Util;

import generator.Config;
import kifio.leningrib.Utils;
import kifio.leningrib.screens.GameScreen;
import kotlin.Pair;

public class ForestGraph implements IndexedGraph<Vector2> {

    private Array<Connection<Vector2>> empty = new Array<>();

    // соединениея между нодами
    private HashMap<Integer, Array<Connection<Vector2>>> connections = new HashMap<>();

    private Heuristic<Vector2> heuristic = new Heuristic<Vector2>() {
        @Override
        public float estimate(Vector2 node, Vector2 endNode) {
            return node.dst2(endNode);
        }
    };

    // ноды - прямоугольники. дерево - 4 ноды
    private Array<Vector2> nodes = new Array<>();

    // Инициализирует ноды, которые могут использоваться для поиска маршрута
    public ForestGraph(Array<? extends Actor> trees) {

        int xMin = 0;
        int xMax = Gdx.graphics.getWidth();

        int yMin = (int) trees.get(0).getY();
        int yMax = (int) trees.get(0).getY();

        long start = System.nanoTime();

        for (Actor tree : trees) {
            int y = (int) tree.getY();
            if (y > yMax) {
                yMax = y;
            }

            if (y < yMin) {
                yMin = y;
            }
        }

        long finish = System.nanoTime();
        Gdx.app.log("kifio_time", "Searching for min max took: " + (finish - start) / 1000000);

        int x, y;

        start = System.nanoTime();
        for (int i = xMin; i < xMax; i+=GameScreen.tileSize) {
            for (int j = yMin; j < yMax; j+=GameScreen.tileSize) {
                x = i;
                y = j;
                if (!isActor(x, y, trees)) {
                    nodes.add(new Vector2(x, y));
                }
            }
        }
        finish = System.nanoTime();

        Gdx.app.log("kifio_time", "Adding nodes took: " + (finish - start) / 1000000);

        start = System.nanoTime();
        for (int i = 0; i < nodes.size; i++) {
            addNeighbours(nodes.get(i), trees);
        }

        finish = System.nanoTime();
        Gdx.app.log("kifio_time", "Adding connections took: " + (finish - start) / 1000000);
    }

    @Override
    public int getIndex(Vector2 node) {
        return nodes.indexOf(node, true);
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<Vector2>> getConnections(Vector2 fromVector2) {
        Vector2 vec = getVector2(fromVector2.x, fromVector2.y);
        int index = this.nodes.indexOf(vec, false);
        if (index == -1) {
            return empty;
        }
        return this.connections.get(index);
    }

    public void updateForestGraph(float cameraPositionY) {
        Iterator<Vector2> nodesIterator = this.nodes.iterator();

        while (nodesIterator.hasNext()) {
            Vector2 from = nodesIterator.next();
            if (Utils.mapCoordinate(from.y) < cameraPositionY - Gdx.graphics.getHeight()) {
                int fromIndex = nodes.indexOf(from, true);
                nodesIterator.remove();
                Array<Connection<Vector2>> c = connections.get(fromIndex);
                if (c != null) c.clear();
                connections.remove(fromIndex);
            }
        }

        for (Array<Connection<Vector2>> connections : this.connections.values()) {
            Iterator<Connection<Vector2>> cIterator = connections.iterator();
            while (cIterator.hasNext()) {
                Connection<Vector2> connection = cIterator.next();
                if (!this.nodes.contains(connection.getToNode(), false)) {
                    cIterator.remove();
                } else  if (!this.nodes.contains(connection.getFromNode(), false)) {
                    cIterator.remove();
                }
            }
        }
    }

    private void addNeighbours(Vector2 origin, Array<? extends Actor> actors) {

        if (origin.x > 0) {
            addConnection(origin, origin.x - GameScreen.tileSize, origin.y, actors);
        }

        if (origin.x < Gdx.graphics.getWidth() - GameScreen.tileSize) {
            addConnection(origin, origin.x + GameScreen.tileSize, origin.y, actors);
        }

        addConnection(origin, origin.x, origin.y + GameScreen.tileSize, actors);

        if (origin.y > 0) {
            addConnection(origin, origin.x, origin.y - GameScreen.tileSize, actors);
        }
    }

    // Добавление пути между двумя нодами
    private void addConnection(Vector2 from, float toX, float toY, Array<? extends Actor> actors) {

        Vector2 to = getVector2(toX, toY);

        if (to == null || isActor((int) toX, (int) toY, actors)) {
            return;
        }

        int fromIndex = nodes.indexOf(from, true);

        Array<Connection<Vector2>> connections = this.connections.get(fromIndex);

        if (connections == null) {
            connections = new Array<>();
            this.connections.put(fromIndex, connections);
        }

        for (Connection connection : connections) {
            Vector2 foo = ((PointsConnection) connection).getFromNode();
            Vector2 bar = ((PointsConnection) connection).getToNode();
            if (foo.epsilonEquals(from) && to.epsilonEquals(bar)) {
                return;
            }
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
    private Vector2 getVector2(float x, float y) {
        for (int i = 0; i < nodes.size; i++) {
            Vector2 node = nodes.get(i);
            if (node.x == x && node.y == y) {
                return node;
            }
        }
        return null;
    }

    // Нодой может быть только клетка, на которой нет актера
    private boolean isActor(int x, int y, Array<? extends Actor> actor) {
        for (int i = 0; i < actor.size; i++) {
            Actor segment = actor.get(i);
            if (segment != null && Utils.mapCoordinate(segment.getX()) == x && Utils.mapCoordinate(segment.getY()) == y) {
                return true;
            }
        }
        return false;
    }

    // Проверяем cуществует ли нода с такими координатами
    public boolean isNodeExists(float x, float y) {
        for (int i = 0; i < nodes.size; i++) {
            Vector2 node = nodes.get(i);
            if (node.x == x && node.y == y) {
                return true;
            }
        }
        return false;
    }

    public void dispose() {
        this.connections.clear();
        this.nodes.clear();
        this.connections = null;
        this.nodes = null;
    }

    private Array<Vector2> playerNeighbours = new Array<>();

    public Vector2 findNearest(int px, int py, int fx, int fy) {

        playerNeighbours.clear();

        playerNeighbours.add(new Vector2(px + GameScreen.tileSize, py + GameScreen.tileSize));
        playerNeighbours.add(new Vector2(px, py + GameScreen.tileSize));
        playerNeighbours.add(new Vector2(px - GameScreen.tileSize, py + GameScreen.tileSize));

        playerNeighbours.add(new Vector2(px + GameScreen.tileSize, py));
        playerNeighbours.add(new Vector2(px - GameScreen.tileSize, py));

        playerNeighbours.add(new Vector2(px + GameScreen.tileSize, py - GameScreen.tileSize));
        playerNeighbours.add(new Vector2(px, py - GameScreen.tileSize));
        playerNeighbours.add(new Vector2(px - GameScreen.tileSize, py - GameScreen.tileSize));

        Vector2 target = new Vector2(px, py);
        float minL = Float.POSITIVE_INFINITY;
        float l;

        for (Vector2 v : nodes) {
            l = v.dst2(fx, fy);
            if (playerNeighbours.contains(v, false) && l < minL) {
                minL = l;
                target = v;
            }
        }

        return target;
    }
}
