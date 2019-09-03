package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.actors.Mushroom.Effect;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle.txt";
    private static final String RUNING = "player_run.txt";

    private int mushroomsCount = 0;
    private long effectiveMushroomTakeTime = 0L;
    private Effect effect;

    public Player(float x, float y) {
        super(new Vector2(x, y));
    }

    public float getVelocity() {
        return 1000f;
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

    @Override
    protected float getDelayTime() {
        return 0.1f;
    }

    @Override
    public float getFrameDuration() {
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

        if (MathUtils.isEqual(fromX, toX) && MathUtils.isEqual(fromY, toY)) return;

        GraphPath<Vector2> path = forestGraph.getPath(fromX, fromY, toX, toY);

        if (path.getCount() > 0) {

            gameScreen.player.stop();

            // Первая точка пути совпадает с координатами игрока,
            // чтобы игрок не стоял на месте лишнее время ее из пути удаляем.
            for (int i = 1; i < path.getCount(); i++) {
                gameScreen.player.path.add(new Vector2(path.get(i)));
            }

            SequenceAction playerActionsSequence = gameScreen.player.getMoveActionsSequence();
            gameScreen.player.addAction(playerActionsSequence);
        }
    }

    @Override
    public SequenceAction getMoveActionsSequence() {
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

        for (int i = 0; i < path.size(); i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y, getVelocity()));
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
