package kifio.leningrib.model.actors.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import kifio.leningrib.model.actors.MovableActor;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class Grandma extends MovableActor {

    private static final String IDLE = "grandma_idle";
    private static final float dialogThreshold = (GameScreen.tileSize * GameScreen.tileSize) * 2;

    private boolean speechesNotStarted = true;
    private boolean giveVodkaSpeechesNotStarted = true;

    public enum DialogState {
        BEFORE_DIALOG, DIALOG_ACTIVE
    }

    private Player player = null;
    private int initialPlayerMushroomCount = 0;

    private String[] speeches = new String[] {
            "А я бабка я..",
            "Стою тут старею..",
            "Грибы собираю..",
            "Маразмом крепчаю..",
            ""
    };

    private String[] giveVodkaSpeeches = new String[]{
            "Ты гляди че делает!",
            "Грибы с земли ест!",
            "На хоть самогончику",
            "Рот пополощи..",
            "Лесникам не давай!",
            "Он их ума лишает..",
            ""
    };

    private DialogState dialogState = DialogState.BEFORE_DIALOG;

    private Label grandmaLabel;

    public Grandma(float x, float y) {
        super(x, y);
        isPaused = false;
        grandmaLabel = LabelManager.getInstance().getLabel("", x,
                y + 1.3f * GameScreen.tileSize);
    }

    public float getVelocity() {
        return 0f;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override public void draw(Batch batch, float alpha) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTextureRegion(), getX(), getY(), getDrawingWidth(), getDrawingHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (player != null && speechesNotStarted) {
            speechesNotStarted = false;
            initialPlayerMushroomCount = player.getMushroomsCount();
            setSpeeches(speeches);
        }

        if (player != null && player.getMushroomsCount() != initialPlayerMushroomCount && giveVodkaSpeechesNotStarted) {
            giveVodkaSpeechesNotStarted = false;
            grandmaLabel.clearActions();
            setSpeeches(giveVodkaSpeeches);
        }
    }

    private void setSpeeches(final String[] texts) {
        SequenceAction sequenceAction = new SequenceAction();
        for (int i = 0; i < texts.length; i++) {
            final int index = i;
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    LabelManager lm = LabelManager.getInstance();
                    float w = lm.getTextWidth(texts[index], lm.smallFont);
                    grandmaLabel.setX(getX() - (w * 0.5f) - (GameScreen.tileSize));
                    grandmaLabel.setText(texts[index]);
                }
            }));
            sequenceAction.addAction(Actions.delay(1.5f));
        }
        grandmaLabel.addAction(sequenceAction);
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
}
