package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import kifio.leningrib.Utils;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private static final String IDLE = "idle";
    private static final String RUN = "run";

    public static Color DEFAULT_SPEECH_COLOR = Color.valueOf("#A0A33B");
    public static Color AGGRESSIVE_SPEECH_COLOR = Color.valueOf("#FF603B");

    private final String running;
    private final String idle;

    private ForesterStateMachine foresterStateMachine = new ForesterStateMachine();

    // Не обновляем путь для лесника, если позиция персонажа не изменилась
    private int lastKnownPlayerX, lastKnownPlayerY;

    public void updateArea() {
        foresterStateMachine.updateArea(
                (int) Utils.mapCoordinate(getX()),
                (int) Utils.mapCoordinate(getY())
        );
    }

    public float speechDuration = 0f;
    private float pursueTime = 0f;    // TODO: Нужно определять время погони, чтобы менять реплики леснику.
    private float stopTime = 0f;    // TODO: Нужно определять время погони, чтобы менять реплики леснику.

    public Rectangle getNoticeArea() {
        return foresterStateMachine.getNoticeArea();
    }

    public Rectangle getPursueArea() {
        return foresterStateMachine.getPursueArea();
    }

    public void setPathDirectly(Vector2 vector2) {
        path.clear();
        path.add(vector2);
    }

    private float originalFromX, originalToX, originalToY;
    private int originalBottomLimit, originalTopLimit, originalLeftLimit, originalRightLimit;
    private ForesterStateMachine.MovingState movingState = ForesterStateMachine.MovingState.PATROL;

    public String speech = "";
    public Color speechColor = DEFAULT_SPEECH_COLOR;

    // Лесники начинают с патрулирования леса, поэтому у них две координаты
    public Forester(float originalFromX, float originalFromY, float originalToX, float originalToY, int index,
                    int originalBottomLimit, int originalTopLimit, int originalLeftLimit, int originalRightLimit,
                    Rectangle scaredArea) {
        super(originalFromX, originalFromY);
        this.originalFromX = originalFromX;
        this.originalToX = originalToX;
        this.originalToY = originalToY;
        this.originalBottomLimit = originalBottomLimit;
        this.originalTopLimit = originalTopLimit;
        this.originalLeftLimit = originalLeftLimit;
        this.originalRightLimit = originalRightLimit;
        running = String.format(Locale.getDefault(), "enemy_%d_run", index);
        idle = String.format(Locale.getDefault(), "enemy_%d_idle", index);
//        setPath(originalToX, originalToY, null);
    }

    public void initPath(ForestGraph forestGraph) {
        setPath(originalToX, originalToY, forestGraph);
    }

    public void updateMovementState(Player player,
                                    float delta,
                                    ForestGraph forestGraph) {

        int px = player.getOnLevelMapX();
        int py = player.getOnLevelMapY();

        ForesterStateMachine.MovingState state = foresterStateMachine.updateState(
                movingState, px, py, player.isStrong(), player.isInvisible(), stopTime, null
        );

        boolean wasChanged = !state.equals(movingState);

        if (wasChanged) {
            speechDuration = 0f;
        }

        switch (state) {
            case PATROL:
                speechColor = Forester.DEFAULT_SPEECH_COLOR;
                speech = SpeechManager.getInstance().getForesterPatrolSpeech();

                if (wasChanged) {
                    stopTime = 0f;
                    initPath(forestGraph);
                }

                if (player.isInvisible()) {
                    speech = SpeechManager.getInstance().getForesterInvisiblePlayerSpeech();
                }

                break;
            case SCARED:
                speech = SpeechManager.getInstance().getForesterScaredSpeech();
                setScaredRoute(px, py, forestGraph);
                break;

            case PURSUE:
                speechColor = Forester.AGGRESSIVE_SPEECH_COLOR;
                speech = SpeechManager.getInstance().getForesterPursuitSpeech();

                if (wasChanged) {
                    speech = SpeechManager.getInstance().getForesterAlarmSpeech();
                }

                setPathToPlayer(px, py, forestGraph);
                break;
            case STOP:
                stopTime += delta;
                speechColor = Forester.DEFAULT_SPEECH_COLOR;

                if (wasChanged) {
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                    stop();
                }

                if (current.getPackFile().contains(Forester.RUN)) {
                    current.setPackFile(current.getPackFile().replace(Forester.RUN, Forester.IDLE));
                }

                break;
            case DISABLED:
                stopTime += delta;
                speechColor = Forester.DEFAULT_SPEECH_COLOR;
                speech = "...";

                if (current.getPackFile().contains(Forester.RUN)) {
                    current.setPackFile(current.getPackFile().replace(Forester.RUN, Forester.IDLE));
                }

        }

        movingState = state;
        lastKnownPlayerX = px;
        lastKnownPlayerY = py;
    }

    public boolean isShouldResetSpeech() {
        return Float.compare(speechDuration, 0) == 0;
    }

    public boolean isShouldRemoveSpeech() {
        return speechDuration > 3f;
    }

    public void updateSpeechDuration(float delta) {
        if (speechDuration > 3.5f) {
            speechDuration = 0f;
            return;
        }
        speechDuration += delta;
    }

    private void setScaredRoute(int px, int py, ForestGraph forestGraph) {

        int x = (int) Utils.mapCoordinate(bounds.x);
        int y = (int) Utils.mapCoordinate(bounds.y);

        if (px < x) {
            if (x < originalRightLimit) {
                setPath(originalRightLimit, y, forestGraph);
            } else if (x - px == GameScreen.tileSize) {
                if (y - originalBottomLimit >= originalTopLimit - y) {
                    setPath(x, originalBottomLimit, forestGraph);
                } else {
                    setPath(x, originalTopLimit - GameScreen.tileSize, forestGraph);
                }
            } else {
//                Gdx.app.log("kifio", "Stay on the right");
            }
        } else if (px > x) {
            if (x > originalLeftLimit) {
                setPath(originalLeftLimit, y, forestGraph);
            } else if (px - x == GameScreen.tileSize) {
                if (y - originalBottomLimit >= originalTopLimit - y) {
                    setPath(x, originalBottomLimit, forestGraph);
                } else {
                    setPath(x, originalTopLimit - GameScreen.tileSize, forestGraph);
                }
            } else {
//                Gdx.app.log("kifio", "Stay on the left");
            }
        } else if (py < y) {
            if (y < originalTopLimit - GameScreen.tileSize) {
                setPath(x, originalTopLimit - GameScreen.tileSize, forestGraph);
            } else if (y - py == GameScreen.tileSize) {
                if (x - originalLeftLimit >= originalRightLimit - x) {
                    setPath(originalLeftLimit, y, forestGraph);
                } else {
                    setPath(originalRightLimit, y, forestGraph);
                }
            } else {
//                Gdx.app.log("kifio", "Stay on the top");
            }
        } else if (py > y) {
            if (y > originalBottomLimit) {
                setPath(x, originalBottomLimit, forestGraph);
            } else if (py - y == GameScreen.tileSize) {
                if (x - originalLeftLimit >= originalRightLimit - x) {
                    setPath(originalLeftLimit, y, forestGraph);
                } else {
                    setPath(originalRightLimit, y, forestGraph);
                }
            } else {
//                Gdx.app.log("kifio", "Stay on the bottom");
            }
        }
    }

    public void disable(Label label) {
        if (movingState != ForesterStateMachine.MovingState.DISABLED) {
            movingState = ForesterStateMachine.MovingState.DISABLED;
            stop();
        }
    }

    public SequenceAction getMoveActionsSequence(final ForestGraph forestGraph) {
        SequenceAction seq = new SequenceAction();
        float fromX = getX();
        float fromY = getY();

        int count = path.getCount();
        int i = count > 1 ? 1 : 0;

        for (; i < path.getCount(); i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
            seq.addAction(getDelayAction(getDelayTime()));
            fromX = vec.x;
            fromY = vec.y;
        }

        seq.addAction(getDelayAction(getDelayTime()));

        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                if (movingState.equals(ForesterStateMachine.MovingState.PATROL)) {
                    setNewPath(forestGraph);
                } else if (movingState.equals(ForesterStateMachine.MovingState.SCARED)) {
                    if (current.getPackFile().contains(RUN)) {
                        current.setPackFile(current.getPackFile().replace(RUN, IDLE));
                    }
                }
            }
        }));

        return seq;
    }

    private void setNewPath(ForestGraph forestGraph) {
        int toX = bounds.x == (int) originalToX ? (int) originalFromX : (int) originalToX;
        int toY = ThreadLocalRandom.current().nextInt(originalBottomLimit, originalTopLimit);
        setPath(toX, toY, forestGraph);
    }

    // Вычисляет путь лесника от a, до b.
    private void setPath(float tx, float ty, ForestGraph forestGraph) {
        if (forestGraph == null) return;
        stop();

        forestGraph.updatePath(Utils.mapCoordinate(getX()), Utils.mapCoordinate(getY()),
                Utils.mapCoordinate(tx), Utils.mapCoordinate(ty), this.path);

        addAction(getMoveActionsSequence(forestGraph));
    }

    private void setPathToPlayer(int px, int py, ForestGraph forestGraph) {
        if (forestGraph == null) return;

        if (lastKnownPlayerX != px || lastKnownPlayerY != py) {
            stop();
            forestGraph.updatePath(
                    Utils.mapCoordinate(getX()),
                    Utils.mapCoordinate(getY()),
                    px, py,
                    this.path);
        }

        addAction(getMoveActionsSequence(forestGraph));
    }

    public float getVelocity() {
        return 200f * Gdx.graphics.getDensity();
    }

    public float getNewSpeechX(float w) {
        return getX() - (w * 0.5f) + (0.5f * GameScreen.tileSize);
    }

    public float getNewSpeechY() {
        return getY() + GameScreen.tileSize * 1.5f;
    }

    @Override
    protected float getDelayTime() {
        return 0.0f;
    }

    @Override
    protected String getIdlingState() {
        return running;
    }

    @Override
    protected String getRunningState() {
        return idle;
    }

    @Override
    public float getFrameDuration() {
        return 0.1f;
    }
}
