package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor implements Speech.SpeechProducer {

    private static long DEFAULT_EFFECT_TIME = 5000; // milliseconds

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

    public Mushroom(int x, int y, String packFile, Random random) {
        super(new Vector2(GameScreen.tileSize * x, GameScreen.tileSize * y), packFile);
        Effect[] effects = Effect.values();
        effect = random.nextInt() / 4 == 0 ? effects[random.nextInt()] : null;
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

    @Override
    protected float getFrameDuration() {
        return 1 / 2f;
    }

    @Override
    public Vector2 getSpeechPosition(String speech) {
        return new Vector2(
                getX() + (bounds.getWidth() / 2) - (SpeechManager.getInstance().getTextWidth(speech) / 2),
                getY() + bounds.getHeight()
        );
    }
}
