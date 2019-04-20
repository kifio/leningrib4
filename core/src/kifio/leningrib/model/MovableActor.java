package kifio.leningrib.model;

import java.lang.*;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.math.Rectangle;

public class MovableActor extends Actor {

    private static final float VELOCITY = 1000f;

    private Pool<MoveToAction> actionPool = new Pool<MoveToAction>() {
        protected MoveToAction newObject() {
            return new MoveToAction();
        }
    };

    private Animation actorAnimation;
    private float elapsedTime = 0;
    private int tileSize;  // нужен для того, чтоб милипиздрические текстурки растягивать до приемлимых значений

    private float tx;
    private float ty;

//    public Rectangle tile;  // клетка, в которой находится актор
    public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при отрисовке фрейма размер пересчитывается

    // TODO: Сделать с помощью билдера
    public MovableActor(float x, float y, int tileSize, String packFile) {
        TextureAtlas playerAtlas = new TextureAtlas(packFile);
        this.actorAnimation = new Animation<>(1 / 15f, playerAtlas.getRegions());
        this.tileSize = tileSize;
        this.bounds = new Rectangle();
        setX(x); setY(y);
        tx = x; ty = y;
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
        bounds.set(getX(), getY(), tileSize, tileSize);
        batch.draw(texture, getX(), getY(), tileSize, tileSize);
    }

    public void moveTo(float targetX, float targetY) {
        MoveToAction moveAction = actionPool.obtain();
        moveAction.setPool(actionPool);

        double dx = (double) (targetX - getX());
        double dy = (double) (targetY - getY());
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        moveAction.reset();
        moveAction.setDuration(length / VELOCITY);
        moveAction.setPosition(targetX, targetY);

        addAction(moveAction);
        
        tx = targetX; ty = targetY;
    }

    public boolean checkIsStayed(float x, float y) {
        // Пользователь ткнул в клетку, не левее и не ниже клетки с персонажем (условия 1 и 2)
        // Пользователь ткнул в клетку, не правее и не выше клетки с персонажем (условия 3 и 4)
        return x > tx && y > ty && Math.abs(x - tx) <= tileSize / 2 && Math.abs(y - ty) <= tileSize / 2;
    }

    public boolean checkMoveLeft(float x, float y) {
        return (x < tx  && x > tx - (tileSize)) && (y > ty && y < ty + (tileSize));
    }

    public boolean checkMoveRight(float x, float y) {
        return (x > tx + (tileSize) && x < tx + 2 * (tileSize)) && (y > ty && y < ty + (tileSize));
    }

    public boolean checkMoveUp(float x, float y) {
        return (y > ty + (tileSize) && y < ty + 2 * (tileSize)) && (x > tx && x < tx + (tileSize));
    }

    public boolean checkMoveDown(float x, float y) {
        return (y < ty && y > ty - (tileSize)) && (x > tx && x < tx + (tileSize));
    }

    public void moveLeft() {
        moveTo(tx - tileSize, ty);
    }

    public void moveRight() {
        moveTo(tx + tileSize, ty);
    }

    public void moveUp() {
        moveTo(tx, ty + tileSize);
    }

    public void moveDown() {
        moveTo(tx, ty - tileSize);
    }
}