package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import kifio.leningrib.screens.GameScreen;

public class Grandma extends MovableActor {

    private static final String IDLE = "grandma_idle.txt";

    public Grandma(float x, float y) {
        super(new Vector2(x, y));
    }

    public float getVelocity() {
        return 0f;
    }

    @Override public void draw(Batch batch, float alpha) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTexturRegion(), getX(), getY(), -getDrawingWidth(), getDrawingHeight());
    }

    @Override
    protected float getDelayTime() {
        return 0.1f;
    }

    @Override
    public float getFrameDuration() {
        return 0.4f;
    }

    @Override
    protected String getIdlingState() {
        return IDLE;
    }

    @Override
    protected String getRunningState() {
        return IDLE;
    }
}
