package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.Utils;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

	private static final int NOTICE_AREA_SIDE = 5;
	private static final int PURSUE_AREA_SIDE = 9;

	private final String running;
	private final String idle;
	private final Rectangle noticeArea = new Rectangle(0, 0,
		NOTICE_AREA_SIDE * GameScreen.tileSize,
		NOTICE_AREA_SIDE * GameScreen.tileSize);

	private final Rectangle pursueArea = new Rectangle(0, 0,
		PURSUE_AREA_SIDE * GameScreen.tileSize,
		PURSUE_AREA_SIDE * GameScreen.tileSize);


	public void updateArea() {
		int x = (int) Utils.mapCoordinate(getX());
		int y = (int) Utils.mapCoordinate(getY());

		noticeArea.setX(x - (2 * GameScreen.tileSize));
		noticeArea.setY(y - (2 * GameScreen.tileSize));

		pursueArea.setX(x - (4 * GameScreen.tileSize));
		pursueArea.setY(y - (4 * GameScreen.tileSize));
	}

	public float speechDuration = 0f;
	private float pursueTime = 0f;	// TODO: Нужно определять время погони, чтобы менять реплики леснику.

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
		PATROL, PURSUE, STOP
	}

	private float originalFromX, originalToX, originalToY;
	private int originalBottomLimit, originalTopLimit;
	private MovingState movingState = MovingState.PATROL;
	private float stoppingTime = 0f;

	// Лесники начинают с патрулирования леса, поэтому у них две координаты
	public Forester(float originalFromX, float originalFromY, float originalToX, float originalToY, int index,
		int originalBottomLimit, int originalTopLimit) {
		super(originalFromX, originalFromY);
		this.originalFromX = originalFromX;
		this.originalToX = originalToX;
		this.originalToY = originalToY;
		this.originalBottomLimit = originalBottomLimit;
		this.originalTopLimit = originalTopLimit;

		running = String.format(Locale.getDefault(), "enemy_%d_run.txt", index);
		idle = String.format(Locale.getDefault(), "enemy_%d_idle.txt", index);
		setPath(originalToX, originalToY, null);
	}

	public void initPath(ForestGraph forestGraph) {
		setPath(originalToX, originalToY, forestGraph);
	}

	// Вычисляет путь лесника от a, до b.
	private void setPath(float tx, float ty, ForestGraph forestGraph) {
		if (forestGraph == null) return;
		stop();

		forestGraph.updatePath(Utils.mapCoordinate(getX()), Utils.mapCoordinate(getY()),
			Utils.mapCoordinate(tx), Utils.mapCoordinate(ty), this.path);

		addAction(getMoveActionsSequence(forestGraph));
	}

	public void updateMovementState(Player player, float delta, ForestGraph forestGraph) {

		float px = player.getX();
		float py = player.getY();

		if (noticeArea.contains(px, py)) {
			setPlayerNoticed();
		} else if (!pursueArea.contains(px, py) && movingState == MovingState.PURSUE) {
			stopPursuing();
		} else if (movingState == MovingState.STOP) {
			if (stoppingTime < 2f) {
				stoppingTime += delta;
			} else {
				stoppingTime = 0f;
				startPatroling();
				setNewPath(forestGraph);
			}
		}

		if (isPursuePlayer()) {
			setPath(player.bounds.x, player.bounds.y, forestGraph);
		}
	}

	private void setPlayerNoticed() {
		stop();
		movingState = MovingState.PURSUE;
	}

	private void stopPursuing() {
		stop();
		movingState = MovingState.STOP;
	}

	private void startPatroling() {
		stop();
		movingState = MovingState.PATROL;
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
				@Override public void run() {
					setNewPath(forestGraph);
				}
			}));
		}

		return seq;
	}

	private void setNewPath(ForestGraph forestGraph) {
		int toX = bounds.x == (int) originalToX ? (int) originalFromX : (int) originalToX;
		int toY = ThreadLocalRandom.current().nextInt(originalBottomLimit, originalTopLimit) * GameScreen.tileSize;

		setPath(toX, toY, forestGraph);
	}

	public boolean isPursuePlayer() {
		return movingState == MovingState.PURSUE;
	}

	public float getVelocity() {
		return 700f;
	}

	@Override protected float getDelayTime() {
		return 0.1f;
	}

	@Override protected String getIdlingState() {
		return running;
	}

	@Override protected String getRunningState() {
		return idle;
	}

	@Override public float getFrameDuration() {
		return 0.1f;
	}
}
