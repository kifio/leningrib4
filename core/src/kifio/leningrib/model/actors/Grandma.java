package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import kifio.leningrib.screens.GameScreen;

public class Grandma extends MovableActor {

    private static final String IDLE = "grandma_idle.txt";
    private static final float dialogTreshold = GameScreen.tileSize * 2;

    public enum DialogState {
        BEFORE_DIALOG, DIALOG_ACTIVE, HAS_POSITIVE_RESPONSE, HAS_NEGATIVE_RESPONSE;
    }

    private DialogState dialogState = DialogState.BEFORE_DIALOG;

    public Grandma(float x, float y) {
        super(new Vector2(x, y));
    }

    public float getVelocity() {
        return 0f;
    }

    @Override public void draw(Batch batch, float alpha) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTextureRegion(), getX(), getY(), -getDrawingWidth(), getDrawingHeight());
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

    public boolean isReadyForDialog(MovableActor actor) {
        return dialogState.equals(DialogState.BEFORE_DIALOG)
            && Vector2.dst2(actor.getX(), actor.getY(), getX(), getY()) < dialogTreshold; //FIXME
    }

    public boolean isDialogActive() {
        return dialogState.equals(DialogState.DIALOG_ACTIVE);
    }

    public void setDialogResult(DialogState dialogResult) {
        dialogState = dialogResult;
    }
}
