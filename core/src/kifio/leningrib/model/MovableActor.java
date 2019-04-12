package kifio.leningrib.model;

import java.lang.*;

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

    public Rectangle tile;  // клетка, в которой находится актор
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
        this.tile = new Rectangle(x, y, (float) tileSize, (float) tileSize);
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
    public int getTileX(int tileSize) {
        return (int) getX() / tileSize;
    }

    public int getTileY(int tileSize) {
        return (int) getY() / tileSize;
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
    }

    public boolean checkIsStayed(float x, float y) {
        return tile.contains(x, y);
    }

    public boolean checkMoveLeft(float x, float y) {
        return (x > tile.x - tile.width && x < tile.x) && (y > tile.y && y < tile.y + tile.height);
    }

    public boolean checkMoveRight(float x, float y) {
        return (x > tile.x + tile.width && x < tile.x + 2 * tile.width) && (y > tile.y && y < tile.y + tile.height);
    }

    public boolean checkMoveUp(float x, float y) {
        return (y > tile.y + tile.height && y < tile.y + 2 * tile.height) && (x > tile.x && x < tile.x + tile.width);
    }

    public boolean checkMoveDown(float x, float y) {
        return (y < tile.y && y > tile.y - tile.height) && (x > tile.x && x < tile.x + tile.width);
    }

    public void moveLeft() {
        moveTo(tile.x - tile.width / 2, tile.y + tile.height / 2);
    }

    public void moveRight() {
        moveTo(tile.x + 1.5f * tile.width, tile.y + tile.height / 2);
    }

    public void moveUp() {
        moveTo(tile.x + tile.height / 2, tile.y + 1.5f * tile.height);
    }

    public void moveDown() {
        moveTo(tile.x + tile.height / 2, tile.y - tile.height / 2);
    }
}