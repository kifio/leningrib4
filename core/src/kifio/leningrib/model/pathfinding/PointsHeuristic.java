package kifio.leningrib.model.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;


public class PointsHeuristic implements Heuristic<Vector2> {
    
    @Override
    public float estimate(Vector2 node, Vector2 endNode) {
        return node.dst2(endNode);
    }
}
