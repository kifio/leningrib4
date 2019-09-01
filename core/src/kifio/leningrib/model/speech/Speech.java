package kifio.leningrib.model.speech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Speech {

    public static long LIFETIME = 1000;

    private float x;
    private float y;
    private long startTime;
    private SpeechProducer speechProducer;
    private String speech;

    public Speech(SpeechProducer speechProducer, String speech) {
        Vector2 position = speechProducer.getSpeechPosition(speech);
        this.x = position.x;
        this.y = position.y;
        this.speechProducer = speechProducer;
        this.startTime = System.currentTimeMillis();
        this.speech = speech;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public void increaseY(float dy) {
        this.y += dy;
    }

    public void dispose() {
        speechProducer = null;
    }

    public SpeechProducer getSpeechProducer() {
        return speechProducer;
    }

    public void decreaseX(float dx) {
        this.x -= dx;
    }

    public interface SpeechProducer {
        Vector2 getSpeechPosition(String speech);
    }
}
