package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.LinkedList;

import javax.rmi.CORBA.Util;

import kifio.leningrib.Utils;
import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private enum MovingState {
        FORWARD, BACK, PURSUE
    }

    private Vector2 from, to;
    private Rectangle patrolRectangle = new Rectangle();
    private LinkedList<Vector2> path = new LinkedList<>();
    private MovingState movingState = MovingState.FORWARD;

    // Лесники начинают с патрулирования леса, поэтому у них две координаты
    public Forester(Vector2 from, Vector2 to, String packFile) {
        super(from, packFile);
        this.from = from;
        this.to = to;
    }

    // Возвращает прямоугольник, в котором двигается персонаж
    public Rectangle getPatrolRectangle() {
        float width = to.x - from.x + GameScreen.tileSize;
        float height = GameScreen.tileSize;
        patrolRectangle.set(from.x, from.y, width, height);
        return patrolRectangle;
    }

    private void resetPath(float tx, float ty, int count) {
        if (to.y == from.y) {
            if (tx < to.x && movingState == MovingState.FORWARD) {
                int from = (int) tx;
                int to = (int) this.to.x;

                while (true) {
                    from += GameScreen.tileSize;
                    path.add(new Vector2(from, ty));
                    count--;
                    if (count == 0) {
                        if (from == to) {
                            movingState = MovingState.BACK;
                        }
                        return;
                    }
                    if (from == to) {
                        movingState = MovingState.BACK;
                        return;
                    }
                }
            } else  {
                int from = (int) tx;
                int to = (int) this.from.x;

                while (true) {
                    from -= GameScreen.tileSize;
                    path.add(new Vector2(from, ty));
                    count--;
                    if (count == 0) {
                        if (from == to) {
                            movingState = MovingState.FORWARD;
                        }
                        return;
                    }
                    if (from == to) {
                        movingState = MovingState.FORWARD;
                        return;
                    }
                }
            }
        }
    }

    public void moveToNextPatrolPoint() {

        float x = Utils.mapCoordinate(getX());
        float y = Utils.mapCoordinate(getY());
        if (path.isEmpty()) resetPath(x, y,1000);
        Vector2 target = path.pop();
        moveTo(target.x, target.y);
    }

    // FIXME: лесник иногда добегает до предпоследнего квадрата, затем разворачивается и убегает
    // Добавляет в путь патрулирования лесника столько-же действий, сколько совешит игрок
    public SequenceAction getMoveActionsSequence(int count) {
        SequenceAction seq = new SequenceAction();

        float x = Utils.mapCoordinate(getX());
        float y = Utils.mapCoordinate(getY());

        while (count > 0) {
            if (path.isEmpty()) resetPath(x, y, count);
            count -= path.size();

            while (!path.isEmpty()) {
                Vector2 target = path.pop();
                seq.addAction(getMoveAction(x, y, target.x, target.y));
                seq.addAction(getDelayAction(0.2f));
                x = target.x;
                y = target.y;
            }
        }

        return seq;
    }

    public void stop() {
        clearActions();
        path.clear();
    }
}
