package kifio.leningrib.model.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import generator.ConstantsConfig;
import kifio.leningrib.Utils;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle";
    private static final String RUNING = "player_run";
    private static final String INVISIBLE = "_invisible";
    private int newColor;
    private Color tmpColor = Color.valueOf("#FDA010");
    private float[] clothesHSV = new float[3];

    private int mushroomsCount = 0;
    private float effectTime = 0L;
    private Mushroom mushroom;
    private int passedLevelsCount;

    public Player(float x, float y) {
        super(x, y);
        clothesHSV = tmpColor.toHsv(clothesHSV);
    }

    private float stateTime = 0f;

    @Override
    public void act(float delta) {
        super.act(delta);

        if (this.mushroom == null) {
            updateClothesColor(delta);
        }

        updateEffectState(delta);
    }

    private void updateClothesColor(float delta) {
        if (stateTime > 0 && this.passedLevelsCount == 0 && stateTime > 1 - passedLevelsCount * 0.1f) {
            updateClothesColor();
            replaceColorInTexture(UIState.obtainUIState(getIdlingState(), this), 0xFDA010FF, newColor);
            replaceColorInTexture(UIState.obtainUIState(getRunningState(), this), 0xFDA010FF, newColor);
            stateTime = 0f;
        }
        stateTime += delta;
    }

    private void updateClothesColor() {
        if (clothesHSV[0] >= 360) {
            clothesHSV[0] = 0;
        } else {
            clothesHSV[0] += 10;
        }
        Gdx.app.log("kifio", "newColor: " + newColor);
        newColor = Color.argb8888(tmpColor.fromHsv(clothesHSV));
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
    }

    public float getVelocity() {
        return 1200f * getVelocityCoeff();
    }

    public void increaseMushroomCount() {
        this.mushroomsCount++;
    }

    public void onEffectiveMushroomTake(Mushroom mushroom) {
        effectTime = 0f;
        this.mushroom = mushroom;

        if (mushroom.isInvisibilityMushroom() && !current.getPackFile().contains(INVISIBLE)) {
            current.setPackFile(current.getPackFile() + INVISIBLE);
        }
    }

    private void updateEffectState(float delta) {

        if (this.mushroom == null) return;

        if (effectTime >= this.mushroom.getEffectTime()) {
            Gdx.app.log("kifio", "Mushroom effect ended in " + effectTime + " sec");

            if (current.getPackFile().contains(INVISIBLE)) {
                current.setPackFile(current.getPackFile().replace(INVISIBLE, ""));
            }

            effectTime = 0f;
            this.mushroom = null;
        } else {
            Gdx.app.log("kifio", "Have mushroom effect: " + this.mushroom.getEffect());
            effectTime += delta;
        }
    }

    //  If player have took speed mushroom
    private float getVelocityCoeff() {
        if (this.mushroom == null) return 1f;
        else return this.mushroom.getSpeedModificator();
    }

    public int getPassedLevelsCount() {
        return passedLevelsCount;
    }

    public int getMushroomsCount() {
        return mushroomsCount;
    }

    @Override
    protected float getDelayTime() {
        return 0.1f;
    }

    @Override
    public float getFrameDuration() {
        return 0.1f;
    }

    @Override
    protected String getIdlingState() {
        Mushroom m = this.mushroom;
        if (m != null && m.isInvisibilityMushroom()) {
            return IDLE + INVISIBLE;
        } else {
            return IDLE;
        }
    }

    @Override
    protected String getRunningState() {
        Mushroom m = this.mushroom;
        if (m != null && m.isInvisibilityMushroom()) {
            return RUNING + INVISIBLE;
        } else {
            return RUNING;
        }
    }

    public void resetPlayerPath(float x, float y, ForestGraph forestGraph, GameScreen gameScreen) {

        float fromX = Utils.mapCoordinate(getX());
        float fromY = Utils.mapCoordinate(getY());
        float toX = Utils.mapCoordinate(x);
        float toY = Utils.mapCoordinate(y);

        if (!forestGraph.isNodeExists(toX, toY)) {
            return;
        }
        if (MathUtils.isEqual(fromX, toX) && MathUtils.isEqual(fromY, toY)) {
            return;
        }

        stop();
        forestGraph.updatePath(fromX, fromY, toX, toY, path);

        if (path.getCount() > 0) {
            addAction(getMoveActionsSequence());
        }
    }

    private SequenceAction getMoveActionsSequence() {
        SequenceAction seq = new SequenceAction();
        float fromX = getX();
        float fromY = getY();

        if (!current.getPackFile().equals(getRunningState())) {
            seq.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    current = UIState.obtainUIState(getRunningState(), Player.this);
                }
            }));
            seq.addAction(getDelayAction(getDelayTime()));
        }

        int count = path.getCount();
        int i = count > 1 ? 1 : 0;

        for (; i < count; i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
            seq.addAction(getDelayAction(getDelayTime()));

            fromX = vec.x;
            fromY = vec.y;
        }

        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                current = UIState.obtainUIState(getIdlingState(), Player.this);
            }
        }));
        seq.addAction(getDelayAction(getDelayTime()));
        return seq;
    }

    public void resetPosition(ConstantsConfig constantsConfig) {
        if (getY() >= (constantsConfig.getLevelHeight() - 1) * GameScreen.tileSize) {
            setY(0);
        } else if (getX() >= (constantsConfig.getLevelWidth() - 1) * GameScreen.tileSize) {
            setX(0);
        }
        passedLevelsCount++;
    }

    public boolean isInvisible() {
        return mushroom != null && mushroom.isInvisibilityMushroom();
    }

    public boolean isStrong() {
        return mushroom != null && mushroom.isStrengthMushroom();
    }

    public boolean isDexterous() {
        return mushroom != null && mushroom.isDexterityMushroom();
    }
}
