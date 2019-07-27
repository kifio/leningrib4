package kifio.leningrib.model.speech;

public class Speech {

    private float x;
    private float y;
    private float startTime;
    private String speech;

    public Speech(float x, float y, float startTime, String speech) {
        this.x = x;
        this.y = y;
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
}
