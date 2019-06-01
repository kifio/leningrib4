package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayList;
import java.util.List;

public class Player extends MovableActor {

    public Player(float x, float y, String packFile) {
        super(new Vector2(x, y), packFile);
    }

    public float getVelocity() {
        return 2000f;
    }

    @Override
    protected float getFrameDuration() {
        return 1 / 15f;
    }
}
