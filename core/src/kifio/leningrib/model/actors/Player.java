package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;

public class Player extends MovableActor {

    private String mushroomsCount = "0";

    public Player(float x, float y, String packFile) {
        super(new Vector2(x, y), packFile);
    }

    public float getVelocity() {
        return 1000f;
    }

    public void increaseMushroomCount() {
        int mushroomsCount = Integer.parseInt(this.mushroomsCount);
        mushroomsCount++;
        this.mushroomsCount = Integer.toString(mushroomsCount);
    }

    public String getMushroomsCount() {
        return mushroomsCount;
    }

    @Override
    protected float getDelayTime() {
        return 0.0f;
    }

    @Override
    protected float getFrameDuration() {
        return 1 / 15f;
    }
}
