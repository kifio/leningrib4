package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
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

    private static final int NOTICE_AREA_SIDE = 5;
    private static final int PURSUE_AREA_SIDE = 9;
    private static final float MAXIMUM_DISABLING_TIME = 3f;
    private static final String IDLE = "idle";
    private static final String RUN = "run";

    private final String running;
    private final String idle;
    private final Rectangle noticeArea = new Rectangle(0, 0,
            NOTICE_AREA_SIDE * GameScreen.tileSize,
            NOTICE_AREA_SIDE * GameScreen.tileSize);

    private final Rectangle pursueArea = new Rectangle(0, 0,
            PURSUE_AREA_SIDE * GameScreen.tileSize,
            PURSUE_AREA_SIDE * GameScreen.tileSize);

    // Не обновляем путь для лесника, если позиция персонажа не изменилась
    private int lastKnownPlayerX, lastKnownPlayerY;

    public void updateArea() {
        int x = (int) Utils.mapCoordinate(getX());
        int y = (int) Utils.mapCoordinate(getY());

        noticeArea.setX(x - (2 * GameScreen.tileSize));
        noticeArea.setY(y - (2 * GameScreen.tileSize));

        pursueArea.setX(x - (4 * GameScreen.tileSize));
        pursueArea.setY(y - (4 * GameScreen.tileSize));
    }

    public float speechDuration = 0f;
    private float pursueTime = 0f;    // TODO: Нужно определять время погони, чтобы менять реплики леснику.
    private float disablingTime = 0f;    // TODO: Нужно определять время погони, чтобы менять реплики леснику.

    public Rectangle getNoticeArea() {
        return noticeArea;
    }

    public Rectangle getPursueArea() {
        return pursueArea;
    }

    public void setPathDirectly(Vector2 vector2) {
        path.clear();
        path.add(vector2);
    }

    private enum MovingState {
        PATROL, PURSUE, STOP, SCARED, DISABLED
    }

    private float originalFromX, originalToX, originalToY;
    private int originalBottomLimit, originalTopLimit, originalLeftLimit, originalRightLimit;
    private MovingState movingState = MovingState.PATROL;
    private float stoppingTime = 0f;

    // Лесники начинают с патрулирования леса, поэтому у них две координаты
    public Forester(float originalFromX, float originalFromY, float originalToX, float originalToY, int index,
                    int originalBottomLimit, int originalTopLimit, int originalLeftLimit, int originalRightLimit) {
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
        setPath(originalToX, originalToY, null);
    }

    public void initPath(ForestGraph forestGraph) {
        setPath(originalToX, originalToY, forestGraph);
    }

    public void updateMovementState(Player player,
                                    Label label,
                                    float delta,
                                    ForestGraph forestGraph) {
        String speech = "";
        int px = player.getOnLevelMapX();
        int py = player.getOnLevelMapY();

        switch (movingState) {
            case PATROL:
                if (noticeArea.contains(px, py)) {
                    if (player.isInvisible()) {
                        speech = SpeechManager.getInstance().getForesterInvisiblePlayerSpeech();
                    } else if (player.isStrong()) {
                        speech = SpeechManager.getInstance().getForesterScaredSpeech();
                        setScared(px, py, forestGraph, true);
                    } else {
                        speech = SpeechManager.getInstance().getForesterAlarmSpeech();
                        setPlayerNoticed();
                        setPathToPlayer(px, py, forestGraph);
                    }
                    speechDuration = 0f;
                } else {
                    speech = getPatrolSpeech();
                }
                break;
            case PURSUE:
                if (player.isStrong()) {
                    speech = SpeechManager.getInstance().getForesterScaredSpeech();
                    setScared(px, py, forestGraph, false);
                    speechDuration = 0f;
                } else if (player.isInvisible() || !pursueArea.contains(px, py)) {
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                    stopPursuing();
                    speechDuration = 0f;
                } else {
                    speech = SpeechManager.getInstance().getForesterPursuitSpeech();
                    setPathToPlayer(px, py, forestGraph);
                }
                break;
            case STOP:
                if (current.getPackFile().contains(RUN)) {
                    current.setPackFile(current.getPackFile().replace(RUN, IDLE));
                }
                if (noticeArea.contains(px, py) && player.isStrong()) {
                    speech = SpeechManager.getInstance().getForesterScaredSpeech();
                    setScared(px, py, forestGraph, true);
                    speechDuration = 0f;
                } else {
                    if (stoppingTime < 2f) {
                        stoppingTime += delta;
                    } else {
                        stoppingTime = 0f;
                        speechDuration = 0f;
                        startPatrol();
                        setNewPath(forestGraph);
                        speech = SpeechManager.getInstance().getForesterPatrolSpeech();
                        if (current.getPackFile().contains(IDLE)) {
                            current.setPackFile(current.getPackFile().replace(IDLE, RUN));
                        }
                    }
                }
                break;
            case SCARED:
                if (player.isInvisible() || !pursueArea.contains(px, py)) {
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                    stopPursuing();
                    speechDuration = 0f;
                } else if (!player.isStrong()) {
                    speech = SpeechManager.getInstance().getForesterPursuitSpeech();
                    setPlayerNoticed();
                    speechDuration = 0f;
                } else {
                    speech = SpeechManager.getInstance().getForesterScaredSpeech();
                    setScared(px, py, forestGraph, false);
                }
                break;
            case DISABLED:
                speech = "...";
                if (current.getPackFile().contains(RUN)) {
                    current.setPackFile(current.getPackFile().replace(RUN, IDLE));
                }
                if (disablingTime > MAXIMUM_DISABLING_TIME) {
                    stoppingTime = disablingTime;
                    disablingTime = 0f;
                    speech = SpeechManager.getInstance().getForesterStopSpeech();
                    movingState = MovingState.STOP;
                    speechDuration = 0f;
                } else {
                    disablingTime += delta;
                }
                break;
        }

        if (isShouldRemoveSpeech()) {
            label.setText(null);
        } else if (isShouldResetSpeech()) {
            label.setText(speech);
        }

        updateSpeechDuration(delta);
        lastKnownPlayerX = px;
        lastKnownPlayerY = py;
    }

    private boolean isShouldResetSpeech() {
        return Float.compare(speechDuration, 0) == 0;
    }

    private boolean isShouldRemoveSpeech() {
        return speechDuration > 3f;
    }

    private void updateSpeechDuration(float delta) {
        if (speechDuration > 3.5f) {
            speechDuration = 0f;
            return;
        }
        speechDuration += delta;
    }

    private String getPatrolSpeech() {
        return ThreadLocalRandom.current().nextBoolean()
                ? SpeechManager.getInstance().getForesterPatrolSpeech()
                : "";
    }

    private void setScaredRoute(int px, int py, ForestGraph forestGraph) {

        if (!pursueArea.contains(px, py)) return;

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
            }
        }

    }

    // forceUpdate нужен на случай, если игрок за пределами видимости лесника и его координаты не меняются.
    // В таком случае маршрут побега должен быть перестроен
    private void setScared(int px, int py, ForestGraph forestGraph, boolean forceUpdate) {
        if (forceUpdate || (lastKnownPlayerX != px || lastKnownPlayerY != py)) {
            setScaredRoute(px, py, forestGraph);
        }
        movingState = MovingState.SCARED;
    }

    private void setPlayerNoticed() {
        stop();
        movingState = MovingState.PURSUE;
    }

    private void stopPursuing() {
        stop();
        movingState = MovingState.STOP;
    }

    private void startPatrol() {
        stop();
        movingState = MovingState.PATROL;
    }

    public boolean isDisabled() {
        return movingState == MovingState.DISABLED;
    }

    public boolean isScared() {
        return movingState == MovingState.SCARED;
    }

    public void disable(Label label) {
        if (movingState != MovingState.DISABLED) {
            movingState = MovingState.DISABLED;
            label.setText("...");
            clearActions();
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
        if (movingState.equals(MovingState.PATROL)) {
            seq.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    setNewPath(forestGraph);
                }
            }));
        }

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

        if (this.path.getCount() == 0) {
            Gdx.app.log("kifio", "path is empty");
        }

        addAction(getMoveActionsSequence(forestGraph));
    }

    private void setPathToPlayer(int px, int py, ForestGraph forestGraph) {
        if (forestGraph == null) return;
        stop();

        if (lastKnownPlayerX != px || lastKnownPlayerY != py) {
            forestGraph.updatePath(
                    Utils.mapCoordinate(getX()),
                    Utils.mapCoordinate(getY()),
                    px, py,
                    this.path);
        }

        addAction(getMoveActionsSequence(forestGraph));
    }

    public float getVelocity() {
        return 500f;
    }

    public float getNewSpeechX() {
        return getX() - GameScreen.tileSize / 2f;
    }

    public float getNewSpeechY() {
        return getY() + GameScreen.tileSize * 1.5f;
    }

    @Override
    protected float getDelayTime() {
        return 0.1f;
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
