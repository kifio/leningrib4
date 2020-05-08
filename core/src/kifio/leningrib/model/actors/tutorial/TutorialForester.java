package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.items.Bottle;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;
import sun.misc.FpUtils;

public class TutorialForester extends TutorialCharacter {

    private String idle;
    private String run;
    private Bottle targetBottle = null;
    private float speechWidth;

    public TutorialForester(float x, float y,
                            String texture,
                            String label
    ) {
        super(x, y);
        this.idle = texture + "_idle";
        this.run = texture + "_run";
        this.isPaused = false;
        this.speechWidth = LabelManager.getInstance()
                .getTextWidth(label, LabelManager.getInstance().smallFont);

        this.label = LabelManager.getInstance().getLabel(label, x - 0.5f * GameScreen.tileSize,
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
        label.setX(getX() + (0.5f * GameScreen.tileSize) - (speechWidth / 2));
        label.setY(getY() + 1.3f * GameScreen.tileSize);
    }

    public void runToBottle(Bottle bottle, ForestGraph forestGraph) {
        if (targetBottle != null) return;
        targetBottle = bottle;
//        label.setText("БОЖЕ МОЙ, ЭТО ОНА!");

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
            addAction(getMoveActionsSequence());
        }
    }

    public void setIdleState() {
        current = UIState.obtainUIState(getIdlingState(), this);
    }
}
