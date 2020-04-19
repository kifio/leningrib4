package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Batch;

import kifio.leningrib.screens.GameScreen;

public class Space extends MovableActor {

    private static final String IDLE = "space";

    public Space(float x, float y) {
        super(x, y);
    }

    public float getVelocity() {
        return 0f;
    }

    @Override public void draw(Batch batch, float alpha) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTextureRegion(), getX(), getY(), getDrawingWidth(), getDrawingHeight());
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

    @Override protected float getDrawingWidth() {
        return GameScreen.tileSize;
    }

    @Override protected float getDrawingHeight() {
        return GameScreen.tileSize;
    }
}
