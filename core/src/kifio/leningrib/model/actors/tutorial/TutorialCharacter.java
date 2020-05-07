package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import kifio.leningrib.model.actors.MovableActor;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.screens.GameScreen;

abstract class TutorialCharacter extends MovableActor {

    Player player = null;
    Rectangle room;
    Label label;
    boolean isDialogActive = false;

    public TutorialCharacter(float x, float y) {
        super(x, y);
    }

    public Rectangle getRoom() {
        return room;
    }

    public Player getPlayer() {
        return player;
    }

    public Label getLabel() {
        return label;
    }

    public abstract void setPlayer(Player player);

    public abstract boolean shouldStartDialog();

    @Override
    public void draw(Batch batch, float alpha) {
        bounds.set(getX(), getY(), GameScreen.tileSize, GameScreen.tileSize);
        batch.draw(getTextureRegion(), getX(), getY(), getDrawingWidth(), getDrawingHeight());
    }
}
