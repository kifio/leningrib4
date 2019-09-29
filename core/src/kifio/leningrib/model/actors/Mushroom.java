package kifio.leningrib.model.actors;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor {

    private static final long DEFAULT_EFFECT_TIME = 5000; // milliseconds
    private static final float SCALE_ANIMATION_TIME = 0.25f; // milliseconds
    private static final String MUSHROOM = "mushroom_";
    private static final String COIN = "coins";

    // COLORS in rgba!! not argb.
    private static final int POWER = 0x780C0280;
    private static final int SPEED = 0x245EFF80;
    private static final int DEXTERITY = 0x2BBF6A80;
    private static final int INVISIBILITY = 0x0FFFFFF20;

    public int[] effects = new int[]{
        POWER, DEXTERITY, INVISIBILITY, SPEED
    };

    private int effect = 0;
    private float scale = 1f;
    private boolean isEaten = false;
    private TextureRegion region;


    public Mushroom(int x, int y) {
        super(x, y);

        int effectIndex = ThreadLocalRandom.current().nextInt(effects.length);
        boolean hasEffect = ThreadLocalRandom.current().nextInt(256) % 8 == 0;
//        boolean hasEffect = ThreadLocalRandom.current().nextBoolean();
        String textureName = MUSHROOM + effectIndex;

        if (hasEffect) {
            effect = effects[effectIndex];
            region = ResourcesManager.getRegionWithTint(textureName, effect, effect == INVISIBILITY);
        } else {
            region = ResourcesManager.getRegion(textureName);
        }

    }

    private boolean b = ThreadLocalRandom.current().nextBoolean();
    private float scalingTime = SCALE_ANIMATION_TIME;

    @Override public void act(float delta) {
        super.act(delta);
        scalingTime += delta;

        if (scalingTime >= SCALE_ANIMATION_TIME) {
            scale = b ? 1f : 0.8f;
            b = !b;
            scalingTime = 0f;
        }
    }

    @Override public void draw(Batch batch, float alpha) {
        if (isEaten) return;
        float x = getX();
        float y = getY();

        bounds.set(x, y, GameScreen.tileSize, GameScreen.tileSize);

        float scaleOffset = (1 - scale) / 2;
        float offsetX = getDrawingWidth() * scaleOffset;
        float offsetY = getDrawingHeight() * scaleOffset;

        float drawingWidth = getDrawingWidth() - (2 * offsetX);
        float drawingHeight = getDrawingWidth() - (2 * offsetX);

        batch.draw(region, x + offsetX, y + offsetY, drawingWidth, drawingHeight);
    }

    public int getEffect() {
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
        return COIN;//effect.getEffectName();
    }

    @Override protected String getRunningState() {
        return COIN;//effect.getEffectName();
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
