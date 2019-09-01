package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor {

    private static long DEFAULT_EFFECT_TIME = 5000; // milliseconds
    private static final String POWER_MUSHROOM = "power_mushroom.txt";
    private static final String SPEED_MUSHROOM = "speed_mushroom.txt";
    private static final String MUSHROOM = "mushroom.txt";

    public enum Effect {

        POWER(DEFAULT_EFFECT_TIME),
        SPEED(DEFAULT_EFFECT_TIME),
        INVISIBLE(DEFAULT_EFFECT_TIME),
        DEXTERITY(DEFAULT_EFFECT_TIME);

        private long effectTime;

        Effect(long effectTime) {
            this.effectTime = effectTime;
        }

        public long getEffectTime() {
            return effectTime;
        }
    }

    private Effect effect;
    private boolean isEaten = false;

    public Mushroom(int x, int y, Random random) {
        super(new Vector2(x, y));
        Effect[] effects = Effect.values();
        effect = random.nextInt() / 4 == 0 ? effects[random.nextInt()] : null;
    }

    @Override public void draw(Batch batch, float alpha) {
        if (isEaten) return;
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        TextureRegion region = getTextureRegion();
        batch.draw(region, getX() + (GameScreen.tileSize - region.getRegionWidth()) / 2f,
            getY() + (GameScreen.tileSize - region.getRegionHeight()) / 2f,
            getDrawingWidth(), getDrawingHeight());
    }

    public Effect getEffect() {
        return effect;
    }

    @Override
    protected float getVelocity() {
        return 0;
    }

    @Override
    protected float getDelayTime() {
        return 0;
    }

    @Override protected String getIdlingState() {
        return POWER_MUSHROOM;
    }

    @Override protected String getRunningState() {
        return POWER_MUSHROOM;
    }

    protected float getDrawingWidth() {
        return GameScreen.tileSize;
    }

    protected float getDrawingHeight() {
        return GameScreen.tileSize;
    }

    @Override
    public float getFrameDuration() {
        return 1 / 2f;
    }

    public void setEaten() {
        this.isEaten = true;
    }
}
