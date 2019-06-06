package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;

public class Player extends MovableActor {

    public Player(float x, float y, String packFile) {
        super(new Vector2(x, y), packFile);
    }

    public float getVelocity() {
        return 1000f;
    }

    @Override
    protected float getFrameDuration() {
        return 1 / 15f;
    }
}
