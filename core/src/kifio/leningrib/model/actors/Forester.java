package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.utils.Array;
import java.util.Arrays;
import java.util.Locale;
import kifio.leningrib.Utils;
import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private static final int AREA_SIDE = 5;

    private final String running;
    private final String idle;
    private final Vector2[] area = new Vector2[AREA_SIDE * AREA_SIDE];

    public void updateArea() {
        int x = (int) Utils.mapCoordinate(getX());
        int y = (int) Utils.mapCoordinate(getY());

        x /= GameScreen.tileSize;
        y /= GameScreen.tileSize;

        x -= 2;
        y -= 3;

        for (int i = 0; i < area.length; i++) {
            area[i].x = x * GameScreen.tileSize;
            area[i].y = y * GameScreen.tileSize;

            x++;
            if ((i + 1) >= (AREA_SIDE - 1) && (i + 1) % AREA_SIDE == 0) {
                y++;
                x -= AREA_SIDE;
            }
        }
    }

    public Vector2[] getArea() {
        return area;
    }

    private enum MovingState {
        FORWARD, BACK, PURSUE, STOP
    }

    // Количество клеток на расстоянии которых лесник замечает игрока
    private static final float DISTANCE_COEFFICIENT = 4f;

    private Vector2 from, to;
    private Array<Vector2> patrolRectangle = null;
    private int routePointFrom, routePointTo;
    private MovingState movingState = MovingState.FORWARD;
    private float stoppingTime = 0f;

    // Лесники начинают с патрулирования леса, поэтому у них две координаты
    public Forester(Vector2 from, Vector2 to, int index) {
        super(from);
        this.from = from;
        this.to = to;
        setPatrolRoute(from, to);
        running = String.format(Locale.getDefault(), "enemy_%d_run.txt", index);
        idle = String.format(Locale.getDefault(),"enemy_%d_idle.txt", index);
        for (int i = 0; i < area.length; i++) area[i] = new Vector2();
    }

//    public Forester(Array<Vector2> routePoints) {
//        super(routePoints.get(0));
//        this.patrolRectangle = routePoints;
//        routePointFrom = 0;
//        routePointTo = 1;
//        setPatrolRoute(routePoints.get(routePointFrom), routePoints.get(routePointTo));
//    }

    private void resetPatrolRoute() {
        stop();
        if (movingState == MovingState.FORWARD) {
            if (patrolRectangle != null) {
                if (routePointTo == patrolRectangle.size - 1) {
                    routePointTo = 0;
                    routePointFrom = patrolRectangle.size - 1; // routePointTo++;
                } else if (routePointTo == 0) {
                    routePointTo++;
                    routePointFrom = 0; // routePointTo++;
                } else {
                    routePointTo++;
                    routePointFrom++;
                }
                setPatrolRoute(patrolRectangle.get(routePointFrom), patrolRectangle.get(routePointTo));
            } else {
                movingState = MovingState.BACK;
                setPatrolRoute(to, from);
            }
        } else if (movingState == MovingState.BACK) {
            movingState = MovingState.FORWARD;
            setPatrolRoute(from, to);
        }
    }

    public void setPatrolRoute(Vector2 from, Vector2 to) {
        SequenceAction seq = new SequenceAction();

        if (from == null || to == null) {
            Gdx.app.log("kifio", "something wrong!");
        }

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

        float px = player.getX();
        float py = player.getY();

        float fx = getX() + (GameScreen.tileSize / 2f);
        float fy = getY() - (GameScreen.tileSize / 2f);

        if (Utils.isOverlapsWithVector(area, (int) px, (int) py)) {
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

//    private Color commonColor = new Color(1f, 1f, 1f, 1f);

//    @Override
//    public void draw(Batch batch, float alpha) {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        super.draw(batch, alpha);
//        batch.setColor(commonColor);
//    }

    public boolean isPursuePlayer() {
        return movingState == MovingState.PURSUE;
    }

    public float getVelocity() {
        return 500f;
    }

    @Override
    protected float getDelayTime() {
        return 0.1f;
    }

    @Override protected String getIdlingState() {
        return running;
    }

    @Override protected String getRunningState() {
        return idle;
    }

    @Override
    public float getFrameDuration() {
        return 0.1f;
    }
}
