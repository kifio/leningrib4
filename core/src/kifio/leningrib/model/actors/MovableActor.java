package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayList;
import java.util.List;

import kifio.leningrib.model.UIState;
import kifio.leningrib.screens.GameScreen;

public abstract class MovableActor extends Actor {

	public List<Vector2> path = new ArrayList<>();
	private float elapsedTime = 0;

	private UIState current = null;

	// TODO: Исправить размеры регионов в атласе
	private static final String IDLE = "player_idle.txt";
	private static final String RUNING = "player_run.txt";

	public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при
    // отрисовке фрейма размер пересчитывается

	public MovableActor(Vector2 xy, String packFile) {
		this.bounds = new Rectangle();
		updateUIState();
		setX(xy.x);
		setY(xy.y);
	}

	public void updateUIState() {
		if (current == null || current.getPackFile().equals(RUNING)) {
			current = UIState.retainUIState(IDLE, this);
		} else if (current.getPackFile().equals(IDLE)) {
			current = UIState.retainUIState(RUNING, this);
		}
	}

	@Override public void act(float delta) {
		elapsedTime += delta;
		super.act(delta);
	}

	@Override public void draw(Batch batch, float alpha) {
		drawOnTile(batch, (TextureRegion) current.getAnimation().getKeyFrame(elapsedTime, true));
	}

	private void drawOnTile(Batch batch, TextureRegion texture) {
		bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
		batch.draw(texture, getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
	}

	protected Action getMoveAction(float fromX, float fromY, float targetX, float targetY, float velocity) {
		double dx = (double) (targetX - fromX);
		double dy = (double) (targetY - fromY);
		float length = (float) Math.sqrt(dx * dx + dy * dy);
		return Actions.moveTo(targetX, targetY, length / velocity);
	}

	protected Action getDelayAction(float duration) {
		return Actions.delay(duration);
	}

	protected abstract float getVelocity();

	protected abstract float getDelayTime();

	public abstract float getFrameDuration();

	public void stop() {
		clear();
		path.clear();
	}

	public SequenceAction getMoveActionsSequence() {
		SequenceAction seq = new SequenceAction();
		float fromX = getX();
		float fromY = getY();
		for (int i = 0; i < path.size(); i++) {
			Vector2 vec = path.get(i);

			seq.addAction(Actions.run(new Runnable() {
				@Override public void run() {
					updateUIState();
				}
			}));

			seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y, getVelocity()));
			seq.addAction(getDelayAction(getDelayTime()));

			seq.addAction(Actions.run(new Runnable() {
				@Override public void run() {
					updateUIState();
				}
			}));

			fromX = vec.x;
			fromY = vec.y;
		}

		return seq;
	}
}