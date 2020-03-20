package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor {

    private static final float DEFAULT_EFFECT_TIME = 10f; // nanoseconds

    // COLORS in rgba!! not argb.
    public static final int POWER = 0xD8390FFF;
    public static final int SPEED = 0x106189FF;
    public static final int DEXTERITY = 0xF49C37FF;
    public static final int INVISIBILITY = 0xFFFFFF20;
    public static final int NO_EFFECT = 0xFFFFFFFF;

    public int[] effects = new int[]{
            POWER, DEXTERITY, INVISIBILITY, SPEED
    };

    private int effect = 0;
    private boolean isEaten = false;

    public Mushroom(int x, int y, boolean hasEffect) {
        super(x, y);

        int effectIndex = ThreadLocalRandom.current().nextInt(effects.length);

        if (hasEffect) {
            effect = effects[effectIndex];
        }
    }

    private boolean b = ThreadLocalRandom.current().nextBoolean();

    @Override public void act(float delta) {
        super.act(delta);
    }

    @Override public void draw(Batch batch, float alpha) {
        if (isEaten) return;
        float x = getX();
        float y = getY();
        bounds.set(x, y, GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTextureRegion(), x, y, getDrawingWidth(), getDrawingHeight());
    }

    public int getEffect() {
        return effect != 0 ? effect : NO_EFFECT;
    }

    public float getEffectTime() {
        if (effect != NO_EFFECT) return DEFAULT_EFFECT_TIME;
        else return 0;
    }

    public float getSpeedModificator() {
        if (effect == SPEED) return 1.5f;
        else return 1f;
    }

    public boolean isInvisibilityMushroom() {
        return effect == INVISIBILITY;
    }

    public boolean isStrengthMushroom() {
        return effect == POWER;
    }

    public boolean isDexterityMushroom() {
        return effect == DEXTERITY;
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
        return getStateName();//effect.getEffectName();
    }

    @Override protected String getRunningState() {
        return getStateName();//effect.getEffectName();
    }

    private String getStateName() {
        switch (effect) {
            case SPEED: return "speed_mushroom";
            case DEXTERITY: return "dexterity_mushroom";
            case POWER: return "power_mushroom";
            default: return "common_mushroom";
        }
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
