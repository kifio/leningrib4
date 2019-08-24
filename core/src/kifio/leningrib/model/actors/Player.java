package kifio.leningrib.model.actors;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import kifio.leningrib.Utils;
import kifio.leningrib.model.actors.Mushroom.Effect;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle.txt";
    private static final String RUNING = "player_run.txt";

    private String mushroomsCount = "0";
    private long effectiveMushroomTakeTime = 0L;
    private Effect effect;

    public Player(float x, float y) {
        super(new Vector2(x, y));
    }

    public float getVelocity() {
        return 1000f;
    }

    public void increaseMushroomCount() {
        int mushroomsCount = Integer.parseInt(this.mushroomsCount);
        mushroomsCount++;
        this.mushroomsCount = Integer.toString(mushroomsCount);
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

    public String getMushroomsCount() {
        return mushroomsCount;
    }

    @Override
    protected float getDelayTime() {
        return 0.0f;
    }

    @Override
    public float getFrameDuration() {
        return 0.4f;
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
        GraphPath<Vector2> path = forestGraph.getPath(
            Utils.mapCoordinate(gameScreen.player.getX()),
            Utils.mapCoordinate(gameScreen.player.getY()),
            Utils.mapCoordinate(x),
            Utils.mapCoordinate(y));

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
