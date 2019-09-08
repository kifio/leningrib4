package kifio.leningrib.model.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class ForestGraph implements IndexedGraph<Vector2> {

    // ноды - прямоугольники. дерево - 4 ноды
    private Array<Vector2> nodes = new Array<>();

    // соединениея между нодами
    private HashMap<Integer, Array<Connection<Vector2>>> connections = new HashMap<>();

    private Heuristic<Vector2> heuristic = new Heuristic<Vector2>() {
        @Override
        public float estimate(Vector2 node, Vector2 endNode) {
            return node.dst2(endNode);
        }
    };

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
        if (index == -1) return new Array<>();
        Array<Connection<Vector2>> connections = this.connections.get(index);
        if (connections.size == 0) {
            Gdx.app.log("kifio", fromVector2.toString() + " has no connections!");
        }
        return connections;
    }

    public void addNode(float x, float y){
        nodes.add(new Vector2(x, y));
    }

    public void addConnection(float fromX, float fromY, float toX, float toY) {

        Vector2 from = getVector2(fromX, fromY);
        Vector2 to = getVector2(toX, toY);

        if (from == null || to == null) return;

        int fromIndex = nodes.indexOf(from, true);

        Array<Connection<Vector2>> connections = this.connections.get(fromIndex);

        if (connections == null) {
            connections = new Array<>();
            this.connections.put(fromIndex, connections);
        }

        connections.add(new PointsConnection(from, to));
    }

    public GraphPath<Vector2> updatePath(float fromX, float fromY, float toX, float toY, GraphPath<Vector2> path) {
        path.clear();
        Vector2 f = getVector2(fromX, fromY);
        Vector2 t = getVector2(toX, toY);
        if (f != null && t != null) {
            IndexedAStarPathFinder<Vector2> pathFinder = new IndexedAStarPathFinder<>(this);
            pathFinder.searchNodePath(f, t, heuristic, path);
        }
        return path;
    }

    private Vector2 getVector2(float x, float y) {
        for (int i = 0; i < nodes.size; i++) {
            Vector2 node = nodes.get(i);
            if (node.x == x && node.y == y) {
                return node;
            }
        }
        return null;
    }
}
