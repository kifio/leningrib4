package kifio.leningrib.model.actors;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import kifio.leningrib.Utils;
import kifio.leningrib.model.items.Bottle;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private static final String IDLE = "idle";
    private static final String RUN = "run";

    public static Color DEFAULT_SPEECH_COLOR = Color.valueOf("#A0A33B");
    public static Color AGGRESSIVE_SPEECH_COLOR = Color.valueOf("#FF603B");

    private static int NOTICE_AREA_SIDE = 7;
    private static int PURSUE_AREA_SIDE = 13;

    private final String running;
    private final String idle;

//    private DefaultGraphPath<Vector2> tPath = new DefaultGraphPath<>();
    private ForesterStateMachine foresterStateMachine = new ForesterStateMachine();

    // Не обновляем путь для лесника, если позиция персонажа не изменилась
    private int lastKnownPlayerX, lastKnownPlayerY;

    public void updateArea() {
        int x = (int) Utils.mapCoordinate(getX());
        int y = (int) Utils.mapCoordinate(getY());

        noticeArea.setX(x - ((NOTICE_AREA_SIDE - 1) / 2f) * GameScreen.tileSize);
        noticeArea.setY(y - ((NOTICE_AREA_SIDE - 1) / 2f) * GameScreen.tileSize);

        pursueArea.setX(x - ((PURSUE_AREA_SIDE - 1) / 2f) * GameScreen.tileSize);
        pursueArea.setY(y - ((PURSUE_AREA_SIDE - 1) / 2f) * GameScreen.tileSize);
    }

    private float speechDuration = 0f;
    private float stopTime = 0f;

    private Rectangle noticeArea = new Rectangle(0F, 0F,
            NOTICE_AREA_SIDE * GameScreen.tileSize,
            NOTICE_AREA_SIDE * GameScreen.tileSize);

    private Rectangle pursueArea = new Rectangle(0F, 0F,
            (PURSUE_AREA_SIDE * GameScreen.tileSize),
            (PURSUE_AREA_SIDE * GameScreen.tileSize));

    private float originalFromX, originalToX;
    private int toX, toY;
    private int originalBottomLimit, originalTopLimit, originalLeftLimit, originalRightLimit;
    private ForesterStateMachine.MovingState movingState = ForesterStateMachine.MovingState.PATROL;
    private Bottle nearestBottle;

    public String speech = "";
    public Color speechColor = DEFAULT_SPEECH_COLOR;

    private Comparator<Bottle> bottleComparator = new Comparator<Bottle>() {
        @Override
        public int compare(Bottle o1, Bottle o2) {

            final float x = Utils.mapCoordinate(getX());
            final float y = Utils.mapCoordinate(getY());

            return Float.compare(
                    Vector2.dst(o1.getX(), o1.getY(), x, y),
                    Vector2.dst(o2.getX(), o2.getY(), x, y));
        }
    };

    // Лесники начинают с патрулирования леса, поэтому у них две координаты
    public Forester(float originalFromX, float originalFromY, float originalToX, int index,
                    int originalBottomLimit, int originalTopLimit, int originalLeftLimit, int originalRightLimit) {
        super(originalFromX, originalFromY);
        this.originalFromX = originalFromX;
        this.originalToX = originalToX;
        this.originalBottomLimit = originalBottomLimit;
        this.originalTopLimit = originalTopLimit;
        this.originalLeftLimit = originalLeftLimit;
        this.originalRightLimit = originalRightLimit;
        running = String.format(Locale.getDefault(), "enemy_%d_run", index);
        idle = String.format(Locale.getDefault(), "enemy_%d_idle", index);
    }

    public void initPath(ForestGraph forestGraph) {
        setNewPath(forestGraph);
    }

    public void updateMovementState(Player player,
                                    ArrayList<Bottle> bottles,
                                    float delta,
                                    ForestGraph forestGraph) {

        int px = player.getOnLevelMapX();
        int py = player.getOnLevelMapY();

        Collections.sort(bottles, bottleComparator);

        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty() && noticeArea.contains(bottle.getX(), bottle.getY())) {
                nearestBottle = bottle;
                break;
            }
        }

        ForesterStateMachine.MovingState state = foresterStateMachine.updateState(
                movingState,
                noticeArea.contains(px, py) && !player.isInvisible(),
                pursueArea.contains(px, py),
                player.isStrong(), player.isInvisible(),
                nearestBottle != null,
                nearestBottle != null && nearestBottle.hasDrinker(this), stopTime
        );

//        Gdx.app.log("kifio", state.name());
        boolean wasChanged = !state.equals(movingState);

        if (wasChanged) {
            speechDuration = 3.1f;
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

                replaceAnimation(Forester.IDLE, Forester.RUN);
                break;
            case SCARED:
                speech = SpeechManager.getInstance().getForesterScaredSpeech();
                break;

            case PURSUE:
                speechColor = Forester.AGGRESSIVE_SPEECH_COLOR;

                if (wasChanged) {
                    speech = SpeechManager.getInstance().getForesterPursuitSpeech();
                }

                replaceAnimation(Forester.IDLE, Forester.RUN);
                break;
            case STOP:
                stopTime += delta;
                speechColor = Forester.DEFAULT_SPEECH_COLOR;

                if (wasChanged) {
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                    stop();
                }

                replaceAnimation(Forester.RUN, Forester.IDLE);
                break;

            case DISABLED:
                stopTime += delta;
                speechColor = Forester.DEFAULT_SPEECH_COLOR;
                speech = "...";

                replaceAnimation(Forester.RUN, Forester.IDLE);
                break;

            case RUN_TO_BOTTLE:
                if (wasChanged) {
                    // FIXME: getRunToBottleSpeech!
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                }
                replaceAnimation(Forester.IDLE, Forester.RUN);
                break;

            case DRINKING:
                if (wasChanged) {
                    // FIXME: getDrinkingSpeech!
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                }
                replaceAnimation(Forester.RUN, Forester.IDLE);
                break;
            case DRUNK:
                stopTime += delta;
                nearestBottle = null;
                if (wasChanged) {
                    // FIXME: getDrunkSpeech!
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                }
                break;
        }

        movingState = state;
    }

    private void replaceAnimation(String from, String to) {
        if (current != null && current.getPackFile().contains(from)) {
            current.setPackFile(current.getPackFile().replace(from, to));
        }
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
        if (lastKnownPlayerX == px && lastKnownPlayerY == py) return;

        int x = (int) Utils.mapCoordinate(getX());
        int y = (int) Utils.mapCoordinate(getY());

        if (px < x) {
            if (x < originalRightLimit) {
                setPath(originalRightLimit, y, forestGraph);
            } else {
                if (y - originalBottomLimit >= originalTopLimit - (y + GameScreen.tileSize)) {
                    setPath(x, originalBottomLimit, forestGraph);
                } else {
                    setPath(x, originalTopLimit - GameScreen.tileSize, forestGraph);
                }
            }
        } else if (px > x) {
            if (x > originalLeftLimit) {
                setPath(originalLeftLimit, y, forestGraph);
            } else {
                if (y - originalBottomLimit >= originalTopLimit - (y + GameScreen.tileSize)) {
                    setPath(x, originalBottomLimit, forestGraph);
                } else {
                    setPath(x, originalTopLimit - GameScreen.tileSize, forestGraph);
                }
            }
        } else if (py < y) {
            if (y < originalTopLimit - GameScreen.tileSize) {
                setPath(x, originalTopLimit - GameScreen.tileSize, forestGraph);
            } else {
                if (x - originalLeftLimit >= originalRightLimit - (x + GameScreen.tileSize)) {
                    setPath(originalLeftLimit, y, forestGraph);
                } else {
                    setPath(originalRightLimit, y, forestGraph);
                }
            }
        } else if (py > y) {
            if (y > originalBottomLimit) {
                setPath(x, originalBottomLimit, forestGraph);
            } else if (py - y == GameScreen.tileSize) {
                if (x - originalLeftLimit >= originalRightLimit - (x + GameScreen.tileSize)) {
                    setPath(originalLeftLimit, y, forestGraph);
                } else {
                    setPath(originalRightLimit, y, forestGraph);
                }
            }
        }
    }

    public void disable(Label label) {
        if (movingState != ForesterStateMachine.MovingState.DISABLED) {
            movingState = ForesterStateMachine.MovingState.DISABLED;
            stop();
            speechDuration = 3.1f;
        }
    }

    public void updatePath(final ForestGraph forestGraph, Player player) {
        int px = player.getOnLevelMapX();
        int py = player.getOnLevelMapY();

        int fx = (int) Utils.mapCoordinate(getX());
        int fy = (int) Utils.mapCoordinate(getY());

        switch (movingState) {
            case PATROL:
                if (fx == toX && fy == toY) {
                    setNewPath(forestGraph);
                }
                break;
            case SCARED:
                setScaredRoute(px, py, forestGraph);
                break;

            case PURSUE:
                setPathToPlayer(px, py, fx, fy,  player.isUnderTrees, forestGraph);
                break;
            case STOP:
            case DISABLED:
                break;
            case RUN_TO_BOTTLE:
                if (nearestBottle != null) {
                    setPath(nearestBottle.getX(), nearestBottle.getY(), forestGraph);
                    if (fx == (int) nearestBottle.getX() && fy == (int) nearestBottle.getY()) {
                        nearestBottle.addDrinker(this);
                    }
                }
                break;
        }

        lastKnownPlayerX = px;
        lastKnownPlayerY = py;
    }

    public SequenceAction getMoveActionsSequence() {
        SequenceAction seq = new SequenceAction();
        int fromX = (int) Utils.mapCoordinate(getX());
        int fromY = (int) Utils.mapCoordinate(getY());

        int count = path.getCount();
        int i = count > 1 ? 1 : 0;

        if (i == 0) {
            seq.addAction(getDelayAction(getDelayTime()));
        }

        for (; i < path.getCount(); i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
            fromX = (int) vec.x;
            fromY = (int) vec.y;
        }

        return seq;
    }

    public void setPathDirectly(Vector2 vector2) {
        path.clear();
        path.add(vector2);
    }

    private void setNewPath(ForestGraph forestGraph) {
        this.toX = (int) Utils.mapCoordinate(getX() == (int) originalToX ? (int) originalFromX : (int) originalToX);
        this.toY = (int) Utils.mapCoordinate(ThreadLocalRandom.current().nextInt(originalBottomLimit, originalTopLimit));
        setPath(toX, toY, forestGraph);
    }

    // Вычисляет путь лесника от a, до b.
    private void setPath(float tx, float ty, ForestGraph forestGraph) {
        if (forestGraph == null) return;

//         Провеярем, что имеющийся уже маршрут не ведет в ту же точку
        Array<Vector2> nodes = this.path.nodes;
        int size = nodes.size;
        if (!nodes.isEmpty() && nodes.get(size - 1).epsilonEquals(tx, ty)) {
            return;
        }

        stop();

        forestGraph.updatePath(Utils.mapCoordinate(getX()), Utils.mapCoordinate(getY()),
                Utils.mapCoordinate(tx), Utils.mapCoordinate(ty), this.path);

        addAction(getMoveActionsSequence());
    }

    private void setPathToPlayer(int px, int py, int fx, int fy, boolean isUnderTrees, ForestGraph forestGraph) {
        if (forestGraph == null) return;
        DefaultGraphPath<Vector2> path = new DefaultGraphPath<>();

        if (isUnderTrees) {
            Vector2 target = forestGraph.findNearest(px, py, fx, fy);
            forestGraph.updatePath(fx, fy, target.x, target.y, path);
        } else {
            forestGraph.updatePath(fx, fy, px, py, path);
        }

        for (int i = 0; i < path.getCount(); i++) {
            if (!this.path.nodes.contains(path.get(i), false)) {
                stop();
                this.path = path;
                addAction(getMoveActionsSequence());
                return;
            }
        }
    }

    public float getVelocity() {
        return GameScreen.tileSize * 4;
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
