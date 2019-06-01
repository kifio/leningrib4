package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;

public class Mushroom extends MovableActor {

    public Mushroom(Vector2 xy, String packFile) {
        super(xy, packFile);
    }

    @Override
    protected float getVelocity() {
        return 0;
    }

    @Override
    protected float getFrameDuration() {
        return 1 / 2f;
    }
}
