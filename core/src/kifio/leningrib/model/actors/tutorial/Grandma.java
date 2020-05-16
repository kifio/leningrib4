package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class Grandma extends TutorialCharacter {

    private static final String IDLE = "grandma_idle";

    public Grandma(float x, float y) {
        super(x, y);
        this.isPaused = false;
        this.label = LabelManager.getInstance().getLabel("", x,
                y + 1.3f * GameScreen.tileSize);
    }

    @Override
    void setSpeech() {
        label.setText(LabelManager.getInstance().getGrandmaSpeech(speechIndex + 1));
    }

    @Override
    public float getVelocity() {
        return 0f;
    }

    public void stopTalking() {
        label.clearActions();
    }

    @Override
    protected float getDelayTime() {
        return 0.1f;
    }

    @Override
    public float getFrameDuration() {
        return 0.4f;
    }

    @Override
    protected String getIdlingState() {
        return IDLE;
    }

    @Override
    protected String getRunningState() {
        return IDLE;
    }
}
