package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

    private enum MovingState {
        FORWARD, BACK, PURSUE
    }

    private Vector2 from, to;
    private Rectangle patrolRectangle = new Rectangle();
    private MovingState movingState = MovingState.FORWARD;

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
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
            seq.addAction(getDelayAction(0.2f));
            fromX = vec.x;
            fromY = vec.y;
        }

        seq.addAction(getCompleteAction());
        addAction(seq);
    }

    // Возвращает прямоугольник, в котором двигается персонаж
    public Rectangle getPatrolRectangle() {
        float width = to.x - from.x + GameScreen.tileSize;
        float height = GameScreen.tileSize;
        patrolRectangle.set(from.x, from.y, width, height);
        return patrolRectangle;
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

    public void checkPlayerNoticed(Player player) {
        float fl = getX();
        float fr = getX() + GameScreen.tileSize;

        float pl = player.getX();
        float pr = player.getX() + GameScreen.tileSize;

        if ((fl > pl && fl < pr) || (fr > pl && fr < pr)) {
            setPlayerNoticed();
        }
    }

    private void setPlayerNoticed() {
        movingState = MovingState.PURSUE;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
//         Включаем поддержку прозрачности
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//        TODO: рисовать лесника с цветной маской поверх текстуры, если игрок в поле зрения лесника
    }
}
