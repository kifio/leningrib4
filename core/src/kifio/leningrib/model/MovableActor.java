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
    private int scale;  // нужен для того, чтоб милипиздрические текстурки растягивать до приемлимых значений
    private int tileSize;  // нужен для того, чтоб милипиздрические текстурки растягивать до приемлимых значений

    private float tx;
    private float ty;

//    public Rectangle tile;  // клетка, в которой находится актор
    public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при отрисовке фрейма размер пересчитывается

    // TODO: Избавиться от параметра scale, рисовать персонажа в центре квадрата, аки гриб
    public MovableActor(int scale, int tileSize, String packFile) {
        this(0f, 0f, scale, tileSize, packFile);
    }

    // TODO: Сделать с помощью билдера
    public MovableActor(float x, float y, int scale, int tileSize, String packFile) {
        TextureAtlas playerAtlas = new TextureAtlas(packFile);
        this.actorAnimation = new Animation<>(1 / 15f, playerAtlas.getRegions());
        this.scale = scale;
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
        if (scale != -1) drawScaled(batch,texture);
        else drawOnTile(batch, texture);
    }

    private void drawScaled(Batch batch, TextureRegion texture) {
        bounds.set(getX(), getY(), texture.getRegionWidth() * scale, texture.getRegionHeight() * scale);
        batch.draw(texture, getX(), getY(), texture.getRegionWidth() * scale, texture.getRegionHeight() * scale);
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
        return Math.abs(x - tx) <= tileSize / 2 && Math.abs(y - ty) <= tileSize / 2;
    }

    public boolean checkMoveLeft(float x, float y) {
        return (x < tx - (tileSize / 2) && x > tx - 3 * (tileSize / 2))
                && (y > ty - (tileSize / 2) && y < ty + (tileSize / 2));
    }

    public boolean checkMoveRight(float x, float y) {
        return (x > tx + (tileSize / 2) && x < tx + 3 * (tileSize / 2))
                && (y > ty - (tileSize / 2) && y < ty + (tileSize / 2));
    }

    public boolean checkMoveUp(float x, float y) {
        return (y > ty + (tileSize / 2) && y < ty + 3 * (tileSize / 2))
                && (x > tx - (tileSize / 2) && x < tx + (tileSize / 2));
    }

    public boolean checkMoveDown(float x, float y) {
        return (y < ty - (tileSize / 2) && y > ty - 3 * (tileSize / 2))
                && (x > tx - (tileSize / 2) && x < tx + (tileSize / 2));
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