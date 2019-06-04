package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;

import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor {

    public Mushroom(int x, int y, String packFile) {
        super(new Vector2(GameScreen.tileSize * x, GameScreen.tileSize * y), packFile);
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
