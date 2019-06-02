package kifio.leningrib.model.speech;

public class Speech {

    private float x;
    private float y;
    private float startTime;
    private float y0;
    private String speech;

    public Speech(float x, float y, float startTime, float y0, String speech) {
        this.x = x;
        this.y = y;
        this.startTime = startTime;
        this.y0 = y0;
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

    public float getY0() {
        return y0;
    }

    public void setY0(float y0) {
        this.y0 = y0;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }
}
