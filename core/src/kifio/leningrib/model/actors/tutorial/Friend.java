package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.Utils;
import kifio.leningrib.model.actors.MovableActor;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Friend extends MovableActor {

    private static final String IDLE = "friend_idle";
    private static final String RUN = "friend_run";

    private float velocity = GameScreen.tileSize * 6;

    public Friend(float x, float y) {
        super(x, y);
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

    public void moveToExit(ForestGraph forestGraph) {
        float fromX = Utils.mapCoordinate(getX());
        float fromY = Utils.mapCoordinate(getY());
        float toX = Utils.mapCoordinate(GameScreen.tileSize * 4);
        float toY = Utils.mapCoordinate(0);

        if (!forestGraph.isNodeExists(toX, toY)) {
            return;
        }

        forestGraph.updatePath(fromX, fromY, toX, toY, path);

        if (path.getCount() > 0 && current != null) {
            SequenceAction sequenceAction = getMoveActionsSequence();
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    remove();
                }
            }));

            addAction(sequenceAction);
        }
    }
}
