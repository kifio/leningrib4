package kifio.leningrib.model.pathfinding;

import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.math.Vector2;

public class PointsConnection extends DefaultConnection<Vector2> {

    private float cost = fromNode.dst2(toNode);

    public PointsConnection(Vector2 fromNode, Vector2 toNode) {
        super(fromNode, toNode);
    }

    @Override
    public float getCost() {
        return cost;
    }
}
