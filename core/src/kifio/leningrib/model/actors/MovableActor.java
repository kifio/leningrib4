package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;

import kifio.leningrib.model.UIState;
import kifio.leningrib.screens.GameScreen;

public abstract class MovableActor extends Actor {

	public List<Vector2> path = new ArrayList<>();
	private float elapsedTime = 0;

	private UIState current =  UIState.obtainUIState(getIdlingState(), this);
	private float drawingWidth = GameScreen.tileSize / 1.5f; // FIXME
	private float drawingHeight = GameScreen.tileSize;
	private Runnable updateState = new Runnable() {
		@Override public void run() {

		}
	};

	public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при
    // отрисовке фрейма размер пересчитывается

	public MovableActor(Vector2 xy) {
		this.bounds = new Rectangle();
		setX(xy.x);
		setY(xy.y);
	}

	@Override public void act(float delta) {
		elapsedTime += delta;
		super.act(delta);
	}

	@Override public void draw(Batch batch, float alpha) {
		TextureRegion region = (TextureRegion) current.getAnimation().getKeyFrame(elapsedTime, true);
		bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
		batch.draw(region, getX(), getY(), getDrawingWidth(), getDrawingHeight());
	}

	protected Action getMoveAction(float fromX, float fromY, float targetX, float targetY, float velocity) {
		double dx = (double) (targetX - fromX);
		double dy = (double) (targetY - fromY);
		float length = (float) Math.sqrt(dx * dx + dy * dy);
		float calculatedDuration = length / velocity;
		Gdx.app.log("kifio", "calculatedDuration: " + calculatedDuration);
		return Actions.moveTo(targetX, targetY, 0.133f);
	}

	protected Action getDelayAction(float duration) {
		return Actions.delay(duration);
	}

	protected abstract float getVelocity();

	protected abstract float getDelayTime();

	protected abstract String getIdlingState();

	protected abstract String getRunningState();

	protected float getDrawingWidth() {
		return drawingWidth;
	}

	protected float getDrawingHeight() {
		return drawingHeight;
	}

	public abstract float getFrameDuration();

	public void stop() {
		clear();
		path.clear();
	}

	public SequenceAction getMoveActionsSequence() {
		SequenceAction seq = new SequenceAction();
		float fromX = getX();
		float fromY = getY();

		if (!current.getPackFile().equals(getRunningState())) {
			seq.addAction(Actions.run(new Runnable() {
				@Override public void run() {
					current = UIState.obtainUIState(getRunningState(), MovableActor.this);
				}
			}));
			seq.addAction(getDelayAction(getDelayTime()));
		}

		for (int i = 0; i < path.size(); i++) {
			Vector2 vec = path.get(i);
			seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y, getVelocity()));
			seq.addAction(getDelayAction(getDelayTime()));

			fromX = vec.x;
			fromY = vec.y;
		}

		seq.addAction(Actions.run(new Runnable() {
			@Override public void run() {
				current = UIState.obtainUIState(getIdlingState(), MovableActor.this);
			}
		}));
		seq.addAction(getDelayAction(getDelayTime()));
		return seq;
	}
}