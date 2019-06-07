package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private enum MovingState {
        FORWARD, BACK, PURSUE, STOP
    }

    // Количество клеток на расстоянии которых лесник замечает игрока
    private static final float DISTANCE_COEFFICIENT = 2f;

    private Vector2 from, to;
    private Rectangle patrolRectangle = new Rectangle();
    private MovingState movingState = MovingState.FORWARD;
    private float stoppingTime = 0f;

    // Лесники начинают с патрулирования леса, поэтому у них две координаты
    public Forester(Vector2 from, Vector2 to, String packFile) {
        super(from, packFile);
        this.from = from;
        this.to = to;
        setPatrolRoute(from, to);
    }

    public void resetPatrolRoute() {
        stop();
        if (movingState == MovingState.FORWARD) {
            movingState = MovingState.BACK;
            setPatrolRoute(to, from);
        } else if (movingState == MovingState.BACK) {
            movingState = MovingState.FORWARD;
            setPatrolRoute(from, to);
        }
    }

    public void setPatrolRoute(Vector2 from, Vector2 to) {
        SequenceAction seq = new SequenceAction();

        float dy = Math.abs(to.y - from.y);
        float dx = Math.abs(to.x - from.x);
        int steps = 0;

        if (dy > dx) {
            steps = (int) (dy / GameScreen.tileSize);
            if (to.y < from.y) {
                for (int i = 0; i <= steps; i++) {
                    path.add(new Vector2(from.x, (from.y - GameScreen.tileSize * i)));
                }
            } else {
                for (int i = 0; i <= steps; i++) {
                    path.add(new Vector2(from.x, (from.y + GameScreen.tileSize * i)));
                }
            }
        } else {
            steps = (int) (dx / GameScreen.tileSize);
            if (to.x < from.x) {
                for (int i = 0; i <= steps; i++) {
                    path.add(new Vector2((from.x - GameScreen.tileSize * i), from.y));
                }
            } else {
                for (int i = 0; i <= steps; i++) {
                    path.add(new Vector2((from.x + GameScreen.tileSize * i), from.y));
                }
            }
        }

        float fromX = getX();
        float fromY = getY();

        for (int i = 0; i < path.size(); i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y, getVelocity()));
            seq.addAction(getDelayAction(0.2f));
            fromX = vec.x;
            fromY = vec.y;
        }

        seq.addAction(getCompleteAction());
        addAction(seq);
    }


    private Action getCompleteAction() {
        return new Action() {
            @Override
            public boolean act(float delta) {
                resetPatrolRoute();
                return false;
            }
        };
    }

    public void updateMoving(Player player, float delta) {

        float px = player.getX() + (GameScreen.tileSize / 2f);
        float py = player.getY() - (GameScreen.tileSize / 2f);

        float fx = getX() + (GameScreen.tileSize / 2f);
        float fy = getY() - (GameScreen.tileSize / 2f);

        if (Math.abs(fx - px) < (DISTANCE_COEFFICIENT * GameScreen.tileSize)
                && Math.abs(fy - py) < (DISTANCE_COEFFICIENT * GameScreen.tileSize)) {
            setPlayerNoticed();
        } else if (movingState == MovingState.PURSUE) {
            stopPursuing();
        } else if (movingState == MovingState.STOP) {
            if (stoppingTime < 2f) {
                stoppingTime += delta;
            } else {
                startPatrol(new Vector2(fx, fy));
            }
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

    private void startPatrol(Vector2 from) {
        stoppingTime = 0f;
        movingState = MovingState.FORWARD;
        setPatrolRoute(from, to);
    }

    private Color foresterFoundLeninColor = new Color(.5f, 1f, .5f, 1f);
    private Color commonColor = new Color(1f, 1f, 1f, 1f);

    @Override
    public void draw(Batch batch, float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (movingState == MovingState.PURSUE) batch.setColor(foresterFoundLeninColor);
        super.draw(batch, alpha);
        batch.setColor(commonColor);
    }

    public boolean isPursuePlayer() {
        return movingState == MovingState.PURSUE;
    }

    public float getVelocity() {
        return 500f;
    }

    @Override
    protected float getFrameDuration() {
        return 1 / 15f;
    }
}
