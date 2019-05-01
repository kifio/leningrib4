package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayList;
import java.util.List;

public class Player extends MovableActor {

    public List<Vector2> path = new ArrayList<>();

    public Player(float x, float y, String packFile) {
        super(new Vector2(x, y), packFile);
    }

    public void moveOnPath() {
        SequenceAction seq = new SequenceAction();
        float fromX = getX();
        float fromY = getY();
        for (Vector2 vec: path) {
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
            seq.addAction(getDelayAction(0.2f));
            fromX = vec.x;
            fromY = vec.y;
        }
        addAction(seq);
    }

    public void stop() {
        clearActions();
        path.clear();
    }
}
