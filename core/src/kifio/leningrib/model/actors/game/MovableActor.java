package kifio.leningrib.model.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.screens.GameScreen;

public abstract class MovableActor extends Actor {

	public static float VELOCITY_DELIMETER = 25f;
	// Путь в который будет записываться найденный путь
	protected DefaultGraphPath<Vector2> path = new DefaultGraphPath<>();
	public float velocityMultiplier = 2f;

	private float elapsedTime = 0;
	private float previousX = -1;
	public boolean goLeft = false;
	private float lastChangeDirectionTime = 0f;
	public boolean isPaused = true;

	// TextureRegion sizes
	private int x = 16;
	private int y = 0;
	private int w = 16;
	private int h = 24;

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
		if (isPaused) return;
		elapsedTime += delta;
		float x = getX();
		if (!MathUtils.isEqual(previousX, x) && elapsedTime - lastChangeDirectionTime > 0.2F) {
			goLeft = previousX > x;
			lastChangeDirectionTime = elapsedTime;
		}
		previousX = (int) getX();
		super.act(delta);
	}

	@Override public void draw(Batch batch, float alpha) {
		float x = getX();
		bounds.set(x, getY(), GameScreen.tileSize, GameScreen.tileSize);
		drawActor(batch, x);
	}

	private void drawActor(Batch batch, float x) {
		if (!goLeft) {
			batch.draw(getTextureRegion(), x, getY(), getDrawingWidth(), getDrawingHeight());
		} else {
			batch.draw(getTextureRegion(), x + getDrawingWidth(), getY(), -getDrawingWidth(), getDrawingHeight());
		}
	}

	protected Action getMoveAction(float fromX, float fromY, float targetX, float targetY) {
		float calculatedDuration = GameScreen.tileSize / getVelocity();

		return Actions.moveTo(targetX, targetY, calculatedDuration);
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

	public SequenceAction getMoveActionsSequence() {
		SequenceAction seq = new SequenceAction();
		int fromX = (int) Utils.mapCoordinate(getX());
		int fromY = (int) Utils.mapCoordinate(getY());

		int count = path.getCount();
		int i = count > 1 ? 1 : 0;

		for (; i < path.getCount(); i++) {
			Vector2 vec = path.get(i);
			seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
			fromX = (int) vec.x;
			fromY = (int) vec.y;
		}

		return seq;
	}

    public boolean hasStableSpeech() {
		return false;
	}
}
