package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.actors.Mushroom.Effect;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;
import kifio.leningrib.view.WorldRenderer;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle.txt";
    private static final String RUNING = "player_run.txt";
    private static final Color CLOTHES_COLOR = Color.valueOf("#FDA010");

	private int mushroomsCount = 0;
	private long effectiveMushroomTakeTime = 0L;
	private Effect effect;

	public Player(float x, float y, WorldRenderer worldRenderer) {
		super(x, y);
		vertexShader = Gdx.files.internal("common_vertex.glsl").readString();
		fragmentShader = Gdx.files.internal("player_fragment.glsl").readString();
		shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
//		shaderProgram.setUniformMatrix("u_projTrans", worldRenderer.getMatrix());
	}

	@Override public void draw(Batch batch, float alpha) {
//		batch.setShader(shaderProgram);
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
		if (System.currentTimeMillis() - effectiveMushroomTakeTime >= effect.getEffectTime()) {
			effect = null;
			effectiveMushroomTakeTime = 0L;
			return false;
		}
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

		float fromX = Utils.mapCoordinate(gameScreen.player.getX());
		float fromY = Utils.mapCoordinate(gameScreen.player.getY());
		float toX = Utils.mapCoordinate(x);
		float toY = Utils.mapCoordinate(y);

		if (!forestGraph.isNodeExists(toX, toY)) { return; }
		if (MathUtils.isEqual(fromX, toX) && MathUtils.isEqual(fromY, toY)) { return; }

		gameScreen.player.stop();
		forestGraph.updatePath(fromX, fromY, toX, toY, path);

		if (path.getCount() > 0) {
			SequenceAction playerActionsSequence = gameScreen.player.getMoveActionsSequence();
			gameScreen.player.addAction(playerActionsSequence);
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
}
