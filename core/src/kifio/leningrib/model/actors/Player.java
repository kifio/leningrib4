package kifio.leningrib.model.actors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;

import generator.Config;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle";
    private static final String RUNING = "player_run";

    public static final float EFFECT_ALERT_TIME = 2f; // Время, спустя котрое персонаж начинает мигать
    public static final float EFFECT_ALERT_INTERVAL = 0.2f; // Интервал мигания

    private int newColor;
    private Color tmpColor = Color.valueOf("#FDA010");
    private float[] clothesHSV = new float[3];

    private int mushroomsCount = 0;
    private float effectTime = 0L;
    private Mushroom mushroom;
    private int passedLevelsCount;

    private boolean shouldCheckStuckUnderTrees = false;
    public boolean isUnderTrees = false;

    public Player(float x, float y) {
        super(x, y);
        clothesHSV = tmpColor.toHsv(clothesHSV);
    }

    private float stateTime = 0f;

    @Override
    public void act(float delta) {
        super.act(delta);

//        if (this.mushroom != null) {
//            updateClothesColor(delta);
//        }

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
        newColor = Color.argb8888(tmpColor.fromHsv(clothesHSV));
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
    }

    public float getVelocity() {
        return (GameScreen.tileSize * 6) * getVelocityCoeff();
    }

    public void increaseMushroomCount() {
        this.mushroomsCount++;
    }

    public void onEffectiveMushroomTake(Mushroom mushroom) {
        effectTime = 0f;
        setEffectTexture(mushroom);
        this.mushroom = mushroom;
        shouldCheckStuckUnderTrees = mushroom.isDexterityMushroom();
    }

    private void updateEffectState(float delta) {
        if (this.mushroom == null) return;

        if (effectTime >= this.mushroom.getEffectTime()) {
            effectTime = 0f;
            clearEffectTexture();
            this.mushroom = null;
        } else {
            if (effectTime >= EFFECT_ALERT_TIME) {
                float t = effectTime - EFFECT_ALERT_TIME;
                int intervalIndex = (int) (t / EFFECT_ALERT_INTERVAL);
                if (intervalIndex % 2 == 0) {
                    clearEffectTexture();
                } else {
                    setEffectTexture(this.mushroom);
                }
            }

            effectTime += delta;
        }
    }

    private void setEffectTexture(Mushroom mushroom) {
        String effectName = mushroom.getEffectName();
        String packFile;

        if (this.mushroom == null) {
            // Игрок взял новый гриб
            packFile = current.getPackFile() + "_" + effectName;
        } else {
            String currentEffect = this.mushroom.getEffectName();
            packFile = current.getPackFile();

            // Игрок взял новый гриб, когда у него был старый гриб
            if (packFile.endsWith(currentEffect)) {
                packFile = packFile.replace(currentEffect, effectName);
            } else {
                // У игрока есть гриб, но его эффект подходит к коцну. Игрок мигает.
                packFile = packFile + "_" + effectName;
            }
        }
        current.setPackFile(packFile.toLowerCase());
    }

    private void clearEffectTexture() {
        String effectName = mushroom.getEffectName();
        if (current.getPackFile().contains(effectName)) {
            String packFile = current.getPackFile().replace("_" + effectName, "");
            current.setPackFile(packFile.toLowerCase());
        }
    }

    //  If player have took speed mushroom
    private float getVelocityCoeff() {
        if (this.mushroom == null) return 1f;
        else return this.mushroom.getSpeedMultiplier();
    }

    public int getPassedLevelsCount() {
        return passedLevelsCount;
    }

    public int getMushroomsCount() {
        return mushroomsCount;
    }

    @Override
    protected float getDelayTime() {
        return 0.0f;
    }

    @Override
    public float getFrameDuration() {
        return 0.1f;
    }

    @Override
    protected String getIdlingState() {
        Mushroom m = this.mushroom;
        if (m == null) return IDLE;
        return IDLE + "_" + m.getEffectName();
    }

    @Override
    protected String getRunningState() {
        Mushroom m = this.mushroom;
        if (m == null) return RUNING;
        return RUNING + "_" + m.getEffectName();
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

    public void resetPosition(Config constantsConfig) {
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
//        return true;
        return mushroom != null && mushroom.isStrengthMushroom();
    }

    public boolean isDexterous() {
        return mushroom != null && mushroom.isDexterityMushroom();
    }

    public int getOnLevelMapX() {
        return (int) Utils.mapCoordinate(bounds.x);
    }

    public int getOnLevelMapY() {
        return (int) Utils.mapCoordinate(bounds.y);
    }

    public void checkStuckUnderTrees(GameScreen gameScreen, TreesManager treesManager) {
        if (!shouldCheckStuckUnderTrees) {
            return;
        }

        boolean isDexterous = isDexterous();

        float x = gameScreen.player.getOnLevelMapX();
        float y = gameScreen.player.getOnLevelMapY();

        Array<TreePart> obstacleTrees = treesManager.getInnerBordersTrees();

        for (TreePart t : obstacleTrees) {
            if (t.position.epsilonEquals(x, y)) {
                if (isDexterous) {
                    isUnderTrees = true;
                    return;
                } else {
                    gameScreen.gameOver = true;
                    gameScreen.player.stop();
                    return;
                }
            }
        }

        // Если дошли до конца массива, значит игрок не скрылся за деревьями.
        if (!isDexterous) {
            shouldCheckStuckUnderTrees = false;
            isUnderTrees = false;
        }
    }
}
