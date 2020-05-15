package kifio.leningrib.model.actors.tutorial;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.concurrent.ThreadLocalRandom;

import kifio.leningrib.model.actors.MovableActor;
import kifio.leningrib.screens.GameScreen;

abstract class TutorialCharacter extends MovableActor {

    static final int SPEECHES_LENGTH = 5;
    Label label;
    int speechIndex = 0;

    private float accumulatedTime = 0f;

    TutorialCharacter(float x, float y) {
        super(x, y);
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        accumulatedTime += delta;
        label.setX(getX() - (GameScreen.tileSize));
        label.setY(getY() + 1.3f * GameScreen.tileSize);

        if ((int) accumulatedTime > 1) {
            int i;
            do {
                i = ThreadLocalRandom.current().nextInt(0, SPEECHES_LENGTH);
            } while (i == speechIndex);
            speechIndex = i;
            accumulatedTime = 0;
            setSpeech();
        }
    }

    abstract void setSpeech();
}
