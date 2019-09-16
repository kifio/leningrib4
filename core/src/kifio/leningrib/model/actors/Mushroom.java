package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor {

    private static long DEFAULT_EFFECT_TIME = 5000; // milliseconds
    private static final String POWER_MUSHROOM = "power.txt";
    private static final String SPEED_MUSHROOM = "speed.txt";
    private static final String DEXTERITY_MUSHROOM = "dexterity.txt";
    private static final String MUSHROOM = "mushroom.txt";

    public enum Effect {

        POWER(POWER_MUSHROOM, DEFAULT_EFFECT_TIME),
        SPEED(SPEED_MUSHROOM, DEFAULT_EFFECT_TIME),
//        INVISIBLE(DEFAULT_EFFECT_TIME, DEFAULT_EFFECT_TIME),
        DEXTERITY(DEXTERITY_MUSHROOM, DEFAULT_EFFECT_TIME);

        private String effectName;
        private long effectTime;

        Effect(String effectName, long effectTime) {
            this.effectName = effectName;
            this.effectTime = effectTime;
        }

        public String getEffectName() {
            return effectName;
        }

        public long getEffectTime() {
            return effectTime;
        }
    }

    private Effect effect;
    private boolean isEaten = false;

    public Mushroom(int x, int y) {
        super(x, y);
        Effect[] effects = Effect.values();
        int index = ThreadLocalRandom.current().nextInt(3);
            effect = effects[index];
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
        return effect.getEffectName();
    }

    @Override protected String getRunningState() {
        return effect.getEffectName();
    }

    protected float getDrawingWidth() {
        return GameScreen.tileSize / 2;
    }

    protected float getDrawingHeight() {
        return GameScreen.tileSize / 2;
    }

    @Override
    public float getFrameDuration() {
        return 1 / 2f;
    }

    public void setEaten() {
        this.isEaten = true;
    }
}
