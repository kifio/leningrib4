package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.ArrayList;
import java.util.List;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class Grandma extends MovableActor {

    private static final int MONOLOG_LENGTH = 1;
    private static final String IDLE = "grandma_idle";
    private static final float dialogThreshold = (GameScreen.tileSize * GameScreen.tileSize) * 2;

    public enum DialogState {
        BEFORE_DIALOG, DIALOG_ACTIVE, HAS_POSITIVE_RESPONSE, HAS_NEGATIVE_RESPONSE;
    }

    private DialogState dialogState = DialogState.BEFORE_DIALOG;
//    private List<Label> positiveReaction = new ArrayList<>(MONOLOG_LENGTH);
//    private List<Label> negativeReaction= new ArrayList<>(MONOLOG_LENGTH);

    private float speechX, speechY;

    private Label grandmaLabel;

    public Grandma(float x, float y) {
        super(x, y);

        speechX = x - GameScreen.tileSize / 2f;
        speechY = y;

        grandmaLabel = SpeechManager.getInstance().getLabel("Ты гляди, че делает!", speechX,
            y + GameScreen.tileSize, GameScreen.tileSize * 3);

//        greetings.add(SpeechManager.getInstance().getLabel("Немытые грибы с земли срывает да ест!", speechX,
//            y + GameScreen.tileSize, GameScreen.tileSize * 3));

//        positiveReaction.add(SpeechManager.getInstance().getLabel("Ох, милок! На, хоть водочкой рот хоть пополощи.", speechX,
//            y + GameScreen.tileSize, GameScreen.tileSize * 3));
//
//        positiveReaction.add(SpeechManager.getInstance().getLabel("Только тутошним лесникам не ее не предлагай! Они от ней совсем ум теряют", speechX,
//            y + GameScreen.tileSize, GameScreen.tileSize * 3));
//
//        negativeReaction.add(SpeechManager.getInstance().getLabel("От грубиян, окаянный!!", speechX,
//            y + GameScreen.tileSize, GameScreen.tileSize * 3));
//
//        negativeReaction.add(SpeechManager.getInstance().getLabel("Помогите! Наркоман смерти моей хочет!", speechX,
//            y + GameScreen.tileSize, GameScreen.tileSize * 3));

        grandmaLabel.addAction(getSpeechAction(new int[]{1, 2}, 4f));

    }

    public float getVelocity() {
        return 0f;
    }

    @Override public void draw(Batch batch, float alpha) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTextureRegion(), getX(), getY(), getDrawingWidth(), getDrawingHeight());
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
        float distance = Vector2.dst2(actor.getX() + (GameScreen.tileSize / 2f),
            actor.getY(), getX() - (GameScreen.tileSize / 2f), getY());

        return dialogState.equals(DialogState.BEFORE_DIALOG)
            && distance < dialogThreshold;
    }

    public boolean isDialogActive() {
        return dialogState.equals(DialogState.DIALOG_ACTIVE);
    }

    public void setDialogResult(DialogState dialogResult) {
        dialogState = dialogResult;
    }

    public void startDialog() {
        dialogState = DialogState.DIALOG_ACTIVE;
    }

    public Label getGrandmaLabel() {
        return grandmaLabel;
    }

    private SequenceAction getSpeechAction(int[] arr, float duration) {
        SequenceAction seq = new SequenceAction();
        for (int i = 0; i < arr.length; i++) {
            final int index = arr[i];
            seq.addAction(Actions.hide());
            seq.addAction(Actions.delay(duration));
            seq.addAction(Actions.run(new Runnable() {
                @Override public void run() {
                    grandmaLabel = SpeechManager.getInstance().getLabel(
                        SpeechManager.getInstance().getGrandmaSpeech(index), speechX,
                        speechY + GameScreen.tileSize, GameScreen.tileSize * 3);

//                    grandmaLabel.setText(SpeechManager.getInstance().getGrandmaSpeech(index));
                }
            }));
            seq.addAction(Actions.delay(duration));
            seq.addAction(Actions.removeActor());
        }
        return seq;
    }
}
