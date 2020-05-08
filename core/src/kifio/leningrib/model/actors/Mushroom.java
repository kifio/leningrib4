package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.concurrent.ThreadLocalRandom;

import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor {

    private static final float DEFAULT_EFFECT_TIME = 5f;

    private static final float EFFECT_ALERT_TIME = 2f; // Время, спустя котрое персонаж начинает мигать
    private static final float EFFECT_ALERT_INTERVAL = 0.2f; // Интервал мигания

    private Color speechColor = new Color(Effect.NO_EFFECT.color);

    public enum Effect {
        POWER(0xD8390FFF),
        DEXTERITY(0x84e00eFF),
        INVISIBLE(0xFFFFFF20),
        SPEED(0x106189FF),
        PATHFINDER(0x450327FF),
        NO_EFFECT(0xFFFFFFFF);

        public final int color;

        Effect(int color) {
            this.color = color;
        }
    };

    private Effect effect = Effect.NO_EFFECT;
    private String stableSpeech = null;
    private boolean isEaten = false;
    private float scale = 1f;
    private float effectTime = DEFAULT_EFFECT_TIME;

    public Mushroom(int x, int y, boolean hasEffect) {
        super(x, y);
        isPaused = false;
        if (hasEffect) {
            Effect[] effects = Effect.values();
            effect = effects[ThreadLocalRandom.current().nextInt(effects.length)];
            speechColor.set(effect.color);
        }
    }

    public Mushroom(int x, int y) {
        this(x, y, false);
    }

    public Mushroom(int x, int y, Effect effect) {
        super(x, y);
        isPaused = false;
        this.effect = effect;
        speechColor.set(effect.color);
    }

    public Mushroom(int x, int y, Effect effect, float effectTime) {
        this(x, y, effect);
        this.effectTime = effectTime;
    }
    
    @Override public void act(float delta) {
        super.act(delta);
    }

    @Override public void draw(Batch batch, float alpha) {
        if (isEaten) return;
        float x = getX();
        float y = getY();
        float size = GameScreen.tileSize * scale;
        bounds.set(x, y, size, size);
        float offset = (GameScreen.tileSize - size) / 2;
        batch.draw(getTextureRegion(), x + offset, y - offset, size, size);
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getEffectName() {
        if (effect == Effect.NO_EFFECT) {
            return null;
        } else {
            return effect.name().toLowerCase();
        }
    }

    public String getSpeech() {
        if (stableSpeech != null) {
            return stableSpeech;
        } else if (effect == Mushroom.Effect.POWER) {
            return LabelManager.getInstance().getPowerMushroomSpeech();
        } else if (effect == Mushroom.Effect.SPEED) {
            return LabelManager.getInstance().getSpeedMushroomSpeech();
        } else if (effect == Mushroom.Effect.DEXTERITY) {
            return LabelManager.getInstance().getDexterityMushroomSpeech();
        } else if (effect == Mushroom.Effect.INVISIBLE) {
            return LabelManager.getInstance().getInvisibilityMushroomSpeech();
        } else {
            return LabelManager.getInstance().getRandomMushroomSpeech();
        }
    }

    public float getEffectTime() {
        if (effect != Effect.NO_EFFECT) return effectTime;
        else return 0;
    }

    public float getSpeedMultiplier() {
        if (effect == Effect.SPEED) return 1.5f;
        else return 1f;
    }

    public boolean isInvisibilityMushroom() {
        return effect == Effect.INVISIBLE;
    }

    public boolean isStrengthMushroom() {
        return effect == Effect.POWER;
    }

    public boolean isDexterityMushroom() {
        return effect == Effect.DEXTERITY;
    }

    public float getEffectAlertTime() {
        if (effectTime == Float.POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        } else {
            return effectTime - 3f;
        }
    }

    public static float getEffectAlertInterval() {
        return EFFECT_ALERT_INTERVAL;
    }

    public void setStableSpeech(String speech) {
        this.stableSpeech = speech;
    }

    public String getStableSpeech() {
        return this.stableSpeech;
    }

    @Override
    public boolean hasStableSpeech() {
        return stableSpeech != null;
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
            case PATHFINDER: return "pathfinder_mushroom";
            default: return "common_mushroom";
        }
    }

    @Override
    protected float getDrawingWidth() {
        return GameScreen.tileSize * scale;
    }

    @Override
    protected float getDrawingHeight() {
        return GameScreen.tileSize * scale;
    }

    @Override
    public float getFrameDuration() {
        return 0.1f;
    }

    public void setEaten() {
        this.isEaten = true;
    }

    public Color getSpeechColor() {
        return speechColor;
    }
}
