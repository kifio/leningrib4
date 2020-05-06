package kifio.leningrib.model.actors.game;

import kifio.leningrib.model.actors.MovableActor;
import kifio.leningrib.screens.GameScreen;

public class Friend extends MovableActor {

    private static final String IDLE = "friend_idle";
    private static final String RUN = "friend_run";

    private float velocity = GameScreen.tileSize * 6;

    public Friend(float x, float y) {
        super(x, y);
        isPaused = false;
        goLeft = true;
    }

    public float getVelocity() {
        return velocity;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected float getDelayTime() {
        return 0.0f;
    }

    @Override
    public float getFrameDuration() {
        return 0.1f;
    }

    @Override
    protected String getIdlingState() {
        return IDLE;
    }

    @Override
    protected String getRunningState() {
        return RUN;
    }
}
