package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class Grandma extends TutorialCharacter {

    private static final String IDLE = "grandma_idle";

    private int initialPlayerMushroomCount = 0;

    private String[] speeches = new String[]{
            "А я бабка я..",
            "Стою тут старею..",
            "Грибы собираю..",
            "Маразмом крепчаю..",
            ""
    };

    public Grandma(float x, float y, Rectangle rectangle) {
        super(x, y);
        this.isPaused = false;
        this.room = rectangle;
        this.label = LabelManager.getInstance().getLabel("", x,
                y + 1.3f * GameScreen.tileSize);
    }

    @Override
    public float getVelocity() {
        return 0f;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
        if (player != null) {
            this.initialPlayerMushroomCount = player.getMushroomsCount();
            setSpeeches(speeches, null);
        }
    }

    public void stopTalking() {
        isDialogActive = true;
        label.clearActions();
    }

    private void setSpeeches(final String[] texts, Runnable callback) {
        SequenceAction sequenceAction = new SequenceAction();

        for (int i = 0; i < texts.length; i++) {
            final int index = i;
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    LabelManager lm = LabelManager.getInstance();
                    float w = lm.getTextWidth(texts[index], lm.smallFont);
                    label.setX(getX() - (w * 0.5f) + (GameScreen.tileSize * 0.5f));
                    label.setText(texts[index]);
                }
            }));

            if (index == texts.length - 3 && callback != null) {
                sequenceAction.addAction(Actions.run(callback));
            }

            if (index < texts.length - 1) {
                sequenceAction.addAction(Actions.delay(1.5f));
            }
        }

        label.addAction(sequenceAction);
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

    @Override
    public boolean shouldStartDialog() {
        return player != null && player.getMushroomsCount() != initialPlayerMushroomCount
                && !isDialogActive;
    }
}
