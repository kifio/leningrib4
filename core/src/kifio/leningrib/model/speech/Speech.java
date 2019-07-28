package kifio.leningrib.model.speech;

import com.badlogic.gdx.Gdx;

import kifio.leningrib.model.actors.Mushroom;

public class Speech {

    private float x;
    private float y;
    private float startTime;
    private Mushroom mushroom;
    private String speech;

    public Speech(Mushroom mushroom, float startTime, String speech) {
        this.x = mushroom.getX() + (mushroom.bounds.getWidth() / 2) - (SpeechManager.getInstance().getTextWidth(speech) / 2);
        this.y = mushroom.getY() + mushroom.bounds.getHeight();
        this.mushroom = mushroom;
        this.startTime = startTime;
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

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
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
        mushroom = null;
    }

    public Mushroom getMushroom() {
        return mushroom;
    }
}
