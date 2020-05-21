package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.actors.game.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class MushroomsManager extends ObjectsManager<Mushroom> {

    private static int SPEECH_SEED = 768;
    private float velocityMultiplier = 1;
    private boolean isSmall = true;

    private Array<Integer> removedMushrooms = new Array<>(4);


    public MushroomsManager(boolean isSmall) {
        gameObjects = new Array<>();
        this.isSmall = isSmall;
    }

    public void addMushrooms(Array<Mushroom> mushrooms) {
        gameObjects.addAll(mushrooms);
        Label[] oldSpeeches = speeches;
        speeches = new Label[gameObjects.size];

        for (int i = 0; i < gameObjects.size; i++) {
            if (oldSpeeches != null && i < oldSpeeches.length) {
                speeches[i] = oldSpeeches[i];
                oldSpeeches[i] = null;
            }
        }
    }

    public void updateMushrooms(Player p, float cameraPositionY, boolean isPaused) {
        if (isPaused) return;
        int halfScreenHeight = Gdx.graphics.getHeight() / 2;

        if (p.getMushroomsCount() > 5) velocityMultiplier = 2.5f;
        if (p.getMushroomsCount() > 15) velocityMultiplier = 3f;
        if (p.getMushroomsCount() > 25) velocityMultiplier = 3.5f;
        if (p.getMushroomsCount() > 35) velocityMultiplier = 4f;
        if (p.getMushroomsCount() > 45) velocityMultiplier = 4.5f;
        if (p.getMushroomsCount() > 60) velocityMultiplier = 5f;

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        for (int index = 0; index < gameObjects.size; index++) {
            Mushroom m = gameObjects.get(index);

            if (m != null) {
                m.velocityMultiplier = velocityMultiplier;
                if (m.getY() >= cameraPositionY - halfScreenHeight
                        && m.getY() <= cameraPositionY + halfScreenHeight) {

                    if (m.bounds.overlaps(p.bounds)) {
                        Sound sound = ResourcesManager.getMushroomTakeSoundEffect();
                        if (sound != null) sound.play(0.5f);
                        m.setEaten();
                        m.clear();
                        m.remove();

                        removedMushrooms.add(index);

                        if (speeches[index] != null) {
                            speeches[index].remove();
                            speeches[index] = null;
                        }

                        if (m.getEffectName() != null) {
                            p.onEffectiveMushroomTake(m);
                        }

                        p.increaseMushroomCount();
                    } else {
                        addMushroomSpeech(p, m, index);
                    }
                }
            }
        }

        for (Integer index : removedMushrooms) {
            gameObjects.set(index, null);
        }

        removedMushrooms.clear();
    }

    private void addMushroomSpeech(Player player, Mushroom m, int index) {
        // С некоторой вероятностью добавляем новую речь
        if ((shouldAddSpeech(player) || m.hasStableSpeech()) && speeches[index] == null) {
            String speech = m.getSpeech();
            float x = m.getX() + (0.5f * GameScreen.tileSize) - (1.3f * GameScreen.tileSize);
            float yOffset = 1f * GameScreen.tileSize;
            float y = m.getY() + yOffset;
            speeches[index] = LabelManager.getInstance().getLabel(speech, isSmall, x, y, m.getSpeechColor());
            if (!m.hasStableSpeech()) {
                speeches[index].addAction(
                        getSpeechAction(ThreadLocalRandom.current().nextFloat() + 1f, index)
                );
            }
        }
    }

    private boolean shouldAddSpeech(Player player) {
        int count = player.getMushroomsCount();
        if (count < 1) {
            return false;
        } else {
            return ThreadLocalRandom.current().nextInt(Math.max(256, SPEECH_SEED / count)) / 8 == 0;
        }
    }

    private SequenceAction getSpeechAction(float duration, final int speechIndex) {
        SequenceAction seq = new SequenceAction();
        seq.addAction(Actions.delay(duration));
        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                speeches[speechIndex].clear();
                speeches[speechIndex].remove();
                speeches[speechIndex] = null;
            }
        }));
        return seq;
    }
}
