package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.LinkedList;

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

    private void resetPath(int count) {
        if (to.y == from.y) {
            if (tx < to.x && movingState == MovingState.FORWARD) {
                int from = (int) this.tx;
                int to = (int) this.to.x;
                while (from < to) {
                    from += GameScreen.tileSize;
                    path.add(new Vector2(from, ty));
                    count--;
                    if (count == 0) return;
                }
                movingState = MovingState.BACK;
            } else  {
                int from = (int) this.tx;
                int to = (int) this.from.x;
                while (from > to) {
                    from -= GameScreen.tileSize;
                    path.add(new Vector2(from, ty));
                    count--;
                    if (count == 0) return;
                }
                movingState = MovingState.FORWARD;
            }
        }
    }

    public void moveToNextPatrolPoint() {
        if (path.isEmpty()) resetPath(1000);
        Vector2 target = path.pop();
        moveTo(target.x, target.y);
    }

    // Добавляет в путь патрулирования лесника столько-же действий, сколько совешит игрок
    public SequenceAction getMoveActionsSequence(int count) {
        SequenceAction seq = new SequenceAction();

        while (count > 0) {
            if (path.isEmpty()) resetPath(count);
            count -= path.size();

            float x = getX();
            float y = getY();

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
