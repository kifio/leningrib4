package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import kifio.leningrib.model.UIState;
import kifio.leningrib.screens.GameScreen;

public abstract class MovableActor extends Actor {

	// Путь в который будет записываться найденный путь
	protected GraphPath<Vector2> path = new DefaultGraphPath<>();

	private float elapsedTime = 0;
	private float previousX = -1;
	private boolean goLeft = false;

	protected UIState current = null;

	private float drawingWidth = GameScreen.tileSize;
	private float drawingHeight = GameScreen.tileSize * 1.5f;

	public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при
    // отрисовке фрейма размер пересчитывается

	public MovableActor(float x, float y) {
		this.bounds = new Rectangle();
		setX(x);
		setY(y);
	}

	@Override public void act(float delta) {
		elapsedTime += delta;
		float x = getX();
		if (!MathUtils.isEqual(previousX, x)) goLeft = previousX > x;
		previousX = (int) getX();
		super.act(delta);
	}

	@Override public void draw(Batch batch, float alpha) {
		float x = getX();
		bounds.set(x, getY(), GameScreen.tileSize, GameScreen.tileSize);

		if (!goLeft) {
			batch.draw(getTextureRegion(), x, getY(), getDrawingWidth(), getDrawingHeight());
		} else {
			batch.draw(getTextureRegion(), x + getDrawingWidth(), getY(), -getDrawingWidth(), getDrawingHeight());
		}
	}

	protected Action getMoveAction(float fromX, float fromY, float targetX, float targetY) {
		double dx = (double) (targetX - fromX);
		double dy = (double) (targetY - fromY);
		float length = (float) Math.sqrt(dx * dx + dy * dy);
		float calculatedDuration = length / getVelocity();
		return Actions.moveTo(targetX, targetY, calculatedDuration);
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

	protected TextureRegion getTextureRegion() {
		if (current == null) current = UIState.obtainUIState(getIdlingState(), this);
		return (TextureRegion) current.getAnimation().getKeyFrame(elapsedTime, true);
	}

	public GraphPath<Vector2> getPath() {
		return path;
	}
}