package kifio.leningrib.model.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
    private static final int INITIAL_BOTTLES_COUNT = 2;

    private float velocity = GameScreen.tileSize * 6 + (getMushroomsCount() / VELOCITY_DELIMETER);
    private int mushroomsCount = 0;
    private float effectTime = 0L;
    private float speechTime = 0f;
    private boolean shouldCheckStuckUnderTrees = false;
    private int bottlesCount = INITIAL_BOTTLES_COUNT;
    private long score = 0;
    private Mushroom mushroom;
    private ArrayList<Integer> movementDirections = new ArrayList<>();

    public float bottomThreshold = 0f;
    public boolean isUnderTrees = false;

    public Label label;

    public Player(float x, float y) {
        super(x, y);
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

    public void addMovementDirection(Integer movementDirection) {
        if (!movementDirections.contains(movementDirection)) {
            movementDirections.add(movementDirection);
            switch (movementDirection) {
                case Input.Keys.LEFT:
                    Gdx.app.log("kifio", "run left");
                    break;
                case Input.Keys.RIGHT:
                    Gdx.app.log("kifio", "run right");
                    break;
                case Input.Keys.UP:
                    Gdx.app.log("kifio", "run up");
                    break;
                case Input.Keys.DOWN:
                    Gdx.app.log("kifio", "run down");
                    break;
            }
            if (current != null && !current.getPackFile().equals(getRunningState())) {
                current = UIState.obtainUIState(getRunningState(), Player.this);
            }
        }
    }

    public void removeMovementDirection(Integer movementDirection) {
        movementDirections.remove(movementDirection);
        switch (movementDirection) {
            case Input.Keys.LEFT:
                Gdx.app.log("kifio", "stop run left");
                break;
            case Input.Keys.RIGHT:
                Gdx.app.log("kifio", "stop run right");
                break;
            case Input.Keys.UP:
                Gdx.app.log("kifio", "stop run up");
                break;
            case Input.Keys.DOWN:
                Gdx.app.log("kifio", "stop run down");
                break;
        }

        if (current != null && movementDirections.isEmpty()) {
            current = UIState.obtainUIState(getIdlingState(), Player.this);
        }
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
        if (m == null) return IDLE;
        return IDLE + "_" + m.getEffectName();
    }

    @Override
    protected String getRunningState() {
        Mushroom m = this.mushroom;
        if (m == null) return RUNING;
        return RUNING + "_" + m.getEffectName();
    }

    public void resetPlayerPath(ForestGraph forestGraph) {

        float x = getX();
        float y = getY();

        for (int i = movementDirections.size() - 1; i >= 0; i--) {
            int direction = movementDirections.get(i);
            switch (direction) {
                case Input.Keys.LEFT:
                    x = x - GameScreen.tileSize;
                    break;
                case Input.Keys.RIGHT:
                    x = x + GameScreen.tileSize;
                    break;
                case Input.Keys.UP:
                    y = y + GameScreen.tileSize;
                    break;
                case Input.Keys.DOWN:
                    y = y - GameScreen.tileSize;
                    break;
            }

            if (forestGraph.isNodeExists(x, y) && y > bottomThreshold) {
                Vector2 destination = new Vector2(x, y);
                if (!path.nodes.contains(destination, false)) {
                    Vector2 origin = new Vector2();
                    int count = path.getCount();
                    if (count == 0) {
                        origin.x = Utils.mapCoordinate(x);
                        origin.y = Utils.mapCoordinate(y);
                    } else {
                        Vector2 vec = path.get(count - 1);
                        origin.x = vec.x;
                        origin.y = vec.y;
                    }

                    path.add(destination);
                    addMoveActionsSequence(origin, destination);
                    return;
                }
            }
        }
    }

    private void addMoveActionsSequence(final Vector2 from, final Vector2  to) {
        if (current == null) {
            return;
        }

        SequenceAction action = new SequenceAction();
        action.addAction(getMoveAction(from.x, from.y, to.x, to.y));
        action.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                path.nodes.removeValue(to, true);
            }
        }));
        addAction(action);
    }

    @Override
    public SequenceAction getMoveActionsSequence() {
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
