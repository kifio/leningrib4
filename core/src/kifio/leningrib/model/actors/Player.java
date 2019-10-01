package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import generator.ConstantsConfig;
import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle";
    private static final String RUNING = "player_run";
	private final Color clothesColor = Color.valueOf("#DF6C43");
	private Color newColor = Color.valueOf("#FDA010");
    private float[] clothesHSV = new float[3];

	private int mushroomsCount = 0;
	private long effectiveMushroomTakeTime = 0L;
	private int effect;
	private int passedLevelsCount;

	public Player(float x, float y) {
		super(x, y);
		clothesHSV = clothesColor.toHsv(clothesHSV);
	}

	private float stateTime = 0f;

	@Override public void act(float delta) {
		super.act(delta);
		stateTime += delta;
		if (stateTime > 1 - passedLevelsCount * 0.1f) {
			updateClothesColor();
			replaceColorInTexture(UIState.obtainUIState(getIdlingState(), this), Color.rgba8888(clothesColor), newColor);
			replaceColorInTexture(UIState.obtainUIState(getRunningState(), this), Color.rgba8888(clothesColor), newColor);
			stateTime = 0f;
		}
		stateTime += delta;
	}

	private void updateClothesColor() {
		if (clothesHSV[0] >= 360) {
			clothesHSV[0] = 0;
		} else {
			clothesHSV[0] += 10;
		}
		newColor.fromHsv(clothesHSV);
	}

	@Override public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
	}

	public float getVelocity() {
		return 1200f;
	}

	public void increaseMushroomCount() {
		this.mushroomsCount++;
	}

	public void onEffectiveMushroomTake(Mushroom mushroom) {
		effectiveMushroomTakeTime = System.currentTimeMillis();
		effect = mushroom.getEffect();
	}

	public boolean updateEffectState() {
//		if (System.currentTimeMillis() - effectiveMushroomTakeTime >= effect.getEffectTime()) {
//			effect = null;
//			effectiveMushroomTakeTime = 0L;
//			return false;
//		}
		return true;
	}

    public int getMushroomsCount() {
        return mushroomsCount;
    }

	@Override protected float getDelayTime() {
		return 0.1f;
	}

	@Override public float getFrameDuration() {
		return 0.1f;
	}

    @Override
    protected String getIdlingState() {
        return IDLE;
    }

    @Override
    protected String getRunningState() {
        return RUNING;
    }

	public void resetPlayerPath(float x, float y, ForestGraph forestGraph, GameScreen gameScreen) {

		float fromX = Utils.mapCoordinate(getX());
		float fromY = Utils.mapCoordinate(getY());
		float toX = Utils.mapCoordinate(x);
		float toY = Utils.mapCoordinate(y);

		if (!forestGraph.isNodeExists(toX, toY)) { return; }
		if (MathUtils.isEqual(fromX, toX) && MathUtils.isEqual(fromY, toY)) { return; }

		stop();
		forestGraph.updatePath(fromX, fromY, toX, toY, path);

		if (path.getCount() > 0) {
			addAction(getMoveActionsSequence());
		}
	}

	private SequenceAction getMoveActionsSequence() {
		SequenceAction seq = new SequenceAction();
		float fromX = getX();
		float fromY = getY();

		if (!current.getPackFile().equals(getRunningState())) {
			seq.addAction(Actions.run(new Runnable() {
				@Override public void run() {
					current = UIState.obtainUIState(getRunningState(), Player.this);
				}
			}));
			seq.addAction(getDelayAction(getDelayTime()));
		}

		int count = path.getCount();
		int i = count > 1 ? 1 : 0;

		for (; i < count; i++) {
			Vector2 vec = path.get(i);
			seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
			seq.addAction(getDelayAction(getDelayTime()));

			fromX = vec.x;
			fromY = vec.y;
		}

		seq.addAction(Actions.run(new Runnable() {
			@Override public void run() {
				current = UIState.obtainUIState(getIdlingState(), Player.this);
			}
		}));
		seq.addAction(getDelayAction(getDelayTime()));
		return seq;
	}

	public void resetPosition(ConstantsConfig constantsConfig) {
		if (getY() >= (constantsConfig.getLevelHeight() - 1) * GameScreen.tileSize) {
			setY(0);
		} else if (getX() >= (constantsConfig.getLevelWidth() - 1) * GameScreen.tileSize) {
			setX(0);
		}
		passedLevelsCount++;
	}
}
