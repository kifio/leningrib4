package kifio.leningrib.model.actors.fixed;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class TutorialForester extends TutorialCharacter {

    private String idle;
    private String run;
    private Bottle targetBottle = null;
    private boolean isDrinking = false;

    private String[] labels = new String[] {
            "Я за тобой бегать не буду",
            "Не пущу и все",
            "Нет покоя в наших лесах",
            "Я на посту всегда",
            "Тут проход в лес запрещен"
    };

    private String[] drinkingLabels = new String[] {
            "Грех за такое не выпить! ",
            "За честь и отвагу!",
            "Ну, за Веру, Царя и Отечество!",
            "За красоту!",
            "За природу!"
    };

    private String[] drunkLabels = new String[] {
            "Постою тут пожалуй, потрезвею.",
            "Без водки вообще бы плохо было...",
            "Я как стекло трезвый...",
            "Хоспади, я в говнину просто...",
            "Не, работать я так точно не смогу..."
    };

    public TutorialForester(float x, float y,
                            String texture
    ) {
        super(x, y);
        this.idle = texture + "_idle";
        this.run = texture + "_run";
        this.isPaused = false;
        this.label = LabelManager.getInstance().getLabel(labels[0], false, x - 0.5f * GameScreen.tileSize,
                y + 1.3f * GameScreen.tileSize, Forester.DEFAULT_SPEECH_COLOR);
    }

    public float getVelocity() {
        return GameScreen.tileSize * 3f;
    }

    @Override
    protected float getDelayTime() {
        return 0.0f;
    }

    @Override
    public float getFrameDuration() {
        return 0.2f;
    }

    @Override
    protected String getIdlingState() {
        return idle;
    }

    @Override
    protected String getRunningState() {
        return run;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    void setSpeech() {
        if (targetBottle == null) {
            label.setText(labels[speechIndex]);
        } else {
            if (isDrinking) {
                if (targetBottle.isEmpty()) {
                    label.setText(drunkLabels[speechIndex]);
                } else {
                    label.setText(drinkingLabels[speechIndex]);
                }
            }
        }
    }

    public void runToBottle(Bottle bottle, ForestGraph forestGraph) {
        if (targetBottle != null) return;
        targetBottle = bottle;
        label.setText("Опа, водочку подвезли!");

        float fromX = Utils.mapCoordinate(getX());
        float fromY = Utils.mapCoordinate(getY());
        float toX = Utils.mapCoordinate(bottle.getX());
        float toY = Utils.mapCoordinate(bottle.getY());

        if (!forestGraph.isNodeExists(toX, toY)) {
            return;
        }

        forestGraph.updatePath(fromX, fromY, toX, toY, path);

        if (path.getCount() > 0 && current != null) {
            current = UIState.obtainUIState(getRunningState(), this);
            SequenceAction sequenceAction = getMoveActionsSequence();
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    isDrinking = true;
                }
            }));
            addAction(sequenceAction);
        }
    }

    public void setIdleState() {
        current = UIState.obtainUIState(getIdlingState(), this);
    }
}
