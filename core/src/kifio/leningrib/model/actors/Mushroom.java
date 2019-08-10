package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;

import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class Mushroom extends MovableActor implements Speech.SpeechProducer {

    public Mushroom(int x, int y, String packFile) {
        super(new Vector2(GameScreen.tileSize * x, GameScreen.tileSize * y), packFile);
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
