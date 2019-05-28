package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayList;
import java.util.List;

public class Player extends MovableActor {

    public Player(float x, float y, String packFile) {
        super(new Vector2(x, y), packFile);
    }

    public SequenceAction getMoveActionsSequence() {
        SequenceAction seq = new SequenceAction();
        float fromX = getX();
        float fromY = getY();
        for (int i = 0; i < path.size(); i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
            seq.addAction(getDelayAction(0.2f));
            fromX = vec.x;
            fromY = vec.y;
        }

        return seq;
    }
}
