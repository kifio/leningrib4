package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import kifio.leningrib.model.actors.MovableActor;

abstract class TutorialCharacter extends MovableActor {

    Label label;

    public TutorialCharacter(float x, float y) {
        super(x, y);
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
    }
}
