package kifio.leningrib.model.actors;

import java.lang.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.math.Rectangle;

import kifio.leningrib.screens.GameScreen;

public class MovableActor extends Actor {

    protected static final float VELOCITY = 2000f;

    private Animation actorAnimation;
    private float elapsedTime = 0;

    // TOOD: заменить на вектор
    protected float tx;
    protected float ty;

//    public Rectangle tile;  // клетка, в которой находится актор
    public Rectangle bounds;    // квадрат вокруг текстрки. т.к. текстурки в анимации могут быть разного размера, при отрисовке фрейма размер пересчитывается

    // TODO: Сделать с помощью билдера
    public MovableActor(Vector2 xy, String packFile) {
        TextureAtlas playerAtlas = new TextureAtlas(packFile);
        this.actorAnimation = new Animation<>(1 / 15f, playerAtlas.getRegions());
        this.bounds = new Rectangle();
        setX(xy.x); setY(xy.y);
        tx = xy.x; ty = xy.y;
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

    public void moveTo(float targetX, float targetY) {
        addAction(getMoveAction(getX(), getY(), targetX, targetY));
    }

    protected Action getMoveAction(float fromX, float fromY, float targetX, float targetY) {
        double dx = (double) (targetX - fromX);
        double dy = (double) (targetY - fromY);
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        Action action = Actions.moveTo(targetX, targetY, length / VELOCITY);
        tx = targetX; ty = targetY;
        return action;
    }

    protected Action getDelayAction(float duration) {
        return Actions.delay(duration);
    }

    public boolean checkIsStayed(float x, float y) {
        // Пользователь ткнул в клетку, не левее и не ниже клетки с персонажем (условия 1 и 2)
        // Пользователь ткнул в клетку, не правее и не выше клетки с персонажем (условия 3 и 4)
        return x > tx && y > ty && x < tx + GameScreen.tileSize && y < ty + GameScreen.tileSize;
    }

    public boolean checkMoveLeft(float x, float y) {
        return (x < tx  && x > tx - (GameScreen.tileSize)) && (y > ty && y < ty + (GameScreen.tileSize));
    }

    public boolean checkMoveRight(float x, float y) {
        return (x > tx + (GameScreen.tileSize) && x < tx + 2 * (GameScreen.tileSize)) && (y > ty && y < ty + (GameScreen.tileSize));
    }

    public boolean checkMoveUp(float x, float y) {
        return (y > ty + (GameScreen.tileSize) && y < ty + 2 * (GameScreen.tileSize)) && (x > tx && x < tx + (GameScreen.tileSize));
    }

    public boolean checkMoveDown(float x, float y) {
        return (y < ty && y > ty - (GameScreen.tileSize)) && (x > tx && x < tx + (GameScreen.tileSize));
    }

    public void moveLeft() {
        moveTo(tx - GameScreen.tileSize, ty);
    }

    public void moveRight() {
        moveTo(tx + GameScreen.tileSize, ty);
    }

    public void moveUp() {
        moveTo(tx, ty + GameScreen.tileSize);
    }

    public void moveDown() {
        moveTo(tx, ty - GameScreen.tileSize);
    }
}