package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.math.Rectangle;

import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.screens.GameScreen;

public class Sign extends TutorialCharacter {

    private static final String IDLE = "sign_idle";
    private Rectangle dialogArea;

    public Sign(float x, float y, Rectangle room) {
        super(x, y);
        this.isPaused = false;
        this.room = room;

        int size = GameScreen.tileSize;
        this.dialogArea = new Rectangle(x, y - size, size - 1, size - 1);
    }

    public boolean shouldShowDialog(float x, float y) {
        if (!isDialogActive && dialogArea.contains(x, y)) {
            isDialogActive = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldHideDialog(float x, float y) {
        if (isDialogActive && !dialogArea.contains(x, y)) {
            isDialogActive = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getVelocity() {
        return 0f;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    protected float getDelayTime() {
        return 0.1f;
    }

    @Override
    public float getFrameDuration() {
        return 0.2f;
    }

    @Override
    protected String getIdlingState() {
        return IDLE;
    }

    @Override
    protected String getRunningState() {
        return IDLE;
    }

    @Override
    public boolean shouldStartDialog() {
        return false;
    }
}
