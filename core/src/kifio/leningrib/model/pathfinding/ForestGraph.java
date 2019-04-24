package kifio.leningrib.model.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import kifio.leningrib.screens.GameScreen;

public class ForestGraph implements IndexedGraph<Vector2> {

    private HashMap<Vector2, Array<PointsConnection>> connections = new HashMap<>();
    private Array<Vector2> connectionsArr = null;

    private PointsHeuristic heuristic = new PointsHeuristic();

    @Override
    public int getIndex(Vector2 node) {
        if (connectionsArr == null) {
            connectionsArr = new Array<>();
            for (Vector2 vec : connections.keySet()) {
                connectionsArr.add(vec);
            }
        }
        return connectionsArr.indexOf(node, true);
    }

    @Override
    public int getNodeCount() {
        return connections.size();
    }

    @Override
    public Array<Connection<Vector2>> getConnections(Vector2 fromNode) {
        Array<Connection<Vector2>> connections = new Array<>();
        if (!this.connections.containsKey(fromNode)) return connections;
        Array<PointsConnection> pointsConnections = this.connections.get(fromNode);
        for (PointsConnection connection : pointsConnections) connections.add(connection);
        return connections;
    }

    public void addConnection(Vector2 from, Vector2 to) {
        Array<PointsConnection> connections = this.connections.get(from);
        if (connections == null) {
            connections = new Array<>();
            this.connections.put(from, connections);
        }
        connections.add(new PointsConnection(from, to));
    }

    public GraphPath<Vector2> getPath(Vector2 from, Vector2 to) {
        GraphPath<Vector2> path = new DefaultGraphPath<>();
        IndexedAStarPathFinder<Vector2> pathFinder = new IndexedAStarPathFinder<>(this);
        pathFinder.searchNodePath(from, to, heuristic, path);
        return path;
    }
}
