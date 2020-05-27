package kifio.leningrib.model.actors.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import org.jetbrains.annotations.Nullable;

import kifio.leningrib.LGCGame;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.model.UIState;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class Player extends MovableActor {

    private static final String IDLE = "player_idle";
    private static final String RUNING = "player_run";

    private float velocity = GameScreen.tileSize * 6 + (getMushroomsCount() / VELOCITY_DELIMETER);
    private int mushroomsCount = 0;
    private float effectTime = 0L;
    private Mushroom mushroom;
    private float speechTime = 0f;

    private boolean shouldCheckStuckUnderTrees = false;
    public boolean isUnderTrees = false;
    private int bottlesCount = 0;
    private long score = 0;
    private int gumsCount = 0;

    public Label label;

    public Player(float x, float y) {
        this(x, y, 0 ,0);
    }

    public Player(float x, float y, int bottlesCount, int gumsCount) {
        super(x, y);
        this.bottlesCount = bottlesCount;
        this.gumsCount = gumsCount;
        label = LabelManager.getInstance().getLabel(null, x,
                y + 1.3f * GameScreen.tileSize, Color.WHITE);
    }

    @Override
    public void act(float delta) {
        if (isPaused) return;
        super.act(delta);
        if (!label.textEquals("")) {
            label.setX(getX() - (GameScreen.tileSize));
            label.setY(getY() + 1.3f * GameScreen.tileSize);
            if (speechTime > 2f) {
                label.setText(null);
                speechTime = 0f;
            } else {
                speechTime += delta;
            }
        }
        updateEffectState(delta);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
    }

    public float getVelocity() {
        if (mushroom == null) return velocity;
        else return velocity * (this.mushroom.getSpeedMultiplier());
    }

    public void increaseMushroomCount() {
        this.mushroomsCount++;
        this.score++;
        if (mushroomsCount == 1) {
            label.setText(LabelManager.getInstance().getWonderingSpeech());
        }
    }

    public void resetMushroomCount() {
        this.mushroomsCount = 0;
        label.setText(LabelManager.getInstance().getWonderingSpeech());
    }

    public void increaseBottlesCount() {
        this.bottlesCount++;
    }

    public void decreaseBottlesCount() {
        if (bottlesCount > 0) {
            this.bottlesCount--;
        }
    }

    public int getBottlesCount() {
        return bottlesCount;
    }

    public void decreaseGumsCount() {
        if (gumsCount > 0) {
            this.gumsCount--;
        }
    }

    public int getGumsCount() {
        return gumsCount;
    }

    public long getScore() {
        return score;
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
            if (effectTime >= mushroom.getEffectAlertTime()) {
                float t = effectTime - mushroom.getEffectAlertTime();
                int intervalIndex = (int) (t / Mushroom.getEffectAlertInterval());
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

    public void clearEffect() {
        if (mushroom == null) return;
        clearEffectTexture();
        mushroom = null;
    }

//    public int getPassedLevelsCount() {
//        return passedLevelsCount;
//    }

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

    public void resetPlayerPath(float x, float y, ForestGraph forestGraph, @Nullable Runnable callback) {
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

        if (path.getCount() > 0 && current != null) {
            SequenceAction action = getMoveActionsSequence();
            if (callback != null) action.addAction(Actions.run(callback));
            addAction(action);
        }
    }

    @Override public SequenceAction getMoveActionsSequence() {
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
        }

        int count = path.getCount();
        int i = count > 1 ? 1 : 0;

        for (; i < count; i++) {
            Vector2 vec = path.get(i);
            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));

            fromX = vec.x;
            fromY = vec.y;
        }

        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                current = UIState.obtainUIState(getIdlingState(), Player.this);
            }
        }));
        return seq;
    }

    public void resetPosition() {
        setY(GameScreen.tileSize + 1);
    }

    public void updateLabel(int lutIndex) {
        if (!label.textEquals("")) return;

        if (lutIndex == 10) {
            label.setText("ЭТО ЧЕ ХОТЬ ТАКОЕ-ТО");
        }

        if (lutIndex == 19 || lutIndex == 20) {
            label.setText("Смеркалось..");
        }
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
                    gameScreen.showGameOver();
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
