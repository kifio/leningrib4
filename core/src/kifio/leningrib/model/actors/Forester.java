package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;

import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private Vector2 from, to;
    private Rectangle patrolRectangle = new Rectangle();
    private LinkedList<Vector2> path = new LinkedList<>();

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

    private void resetPath() {
        if (to.y == from.y) {
            if (tx < to.x) {
                int from = (int) this.tx;
                int to = (int) this.to.x;
                while (from < to) {
                    from += GameScreen.tileSize;
                    path.add(new Vector2(from, ty));
                }
            } else {
                int from = (int) this.tx;
                int to = (int) this.from.x;
                while (from > to) {
                    from -= GameScreen.tileSize;
                    path.add(new Vector2(from, ty));
                }
            }
        }
    }

    public void moveToNextPatrolPoint() {
        if (path.isEmpty()) resetPath();
        Vector2 target = path.pop();
        moveTo(target.x, target.y);
    }
}
