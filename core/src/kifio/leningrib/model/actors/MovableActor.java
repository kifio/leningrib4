package kifio.leningrib.model.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayList;
import java.util.List;

import kifio.leningrib.screens.GameScreen;

public abstract class MovableActor extends Actor {

    public List<Vector2> path = new ArrayList<>();
    private Animation actorAnimation;
    private float elapsedTime = 0;

    public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при отрисовке фрейма размер пересчитывается

    // TODO: Сделать с помощью билдера
    public MovableActor(Vector2 xy, String packFile) {
        TextureAtlas playerAtlas = new TextureAtlas(packFile);
        this.actorAnimation = new Animation<>(1 / 15f, playerAtlas.getRegions());
        this.bounds = new Rectangle();
        setX(xy.x);
        setY(xy.y);
    }

    @Override
    public void act(float delta) {
        elapsedTime += delta;
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        TextureRegion texture = (TextureRegion) actorAnimation.getKeyFrame(elapsedTime, true);
        drawOnTile(batch, texture);
    }

    private void drawOnTile(Batch batch, TextureRegion texture) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(texture, getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
    }

    protected Action getMoveAction(float fromX, float fromY, float targetX, float targetY, float velocity) {
        double dx = (double) (targetX - fromX);
        double dy = (double) (targetY - fromY);
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        return Actions.moveTo(targetX, targetY, length / velocity);
    }

    protected Action getDelayAction(float duration) {
        return Actions.delay(duration);
    }

    protected abstract float getVelocity();

    public void stop() {
        clear();
        path.clear();
    }

    public SequenceAction getMoveActionsSequence() {
        SequenceAction seq = new SequenceAction();
        float fromX = getX();
        float fromY = getY();
        for (int i = 0; i < path.size(); i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y, getVelocity()));
            seq.addAction(getDelayAction(0.2f));
            fromX = vec.x;
            fromY = vec.y;
        }

        return seq;
    }
}