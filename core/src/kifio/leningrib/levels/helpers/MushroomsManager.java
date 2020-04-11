package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class MushroomsManager extends ObjectsManager<Mushroom> {

    private Array<Integer> removedMushrooms = new Array<>(4);

    public MushroomsManager() {
        gameObjects = new Array<>();
    }

    public void initMushrooms(Array<Mushroom> mushrooms) {
        gameObjects.addAll(mushrooms);
        speeches = new Label[gameObjects.size];
    }

    public void updateMushrooms(Player p, float cameraPositionY, boolean isPaused) {
        if (isPaused) return;
        int halfScreenHeight = Gdx.graphics.getHeight() / 2;

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        for (int index = 0; index < gameObjects.size; index++) {
            Mushroom m = gameObjects.get(index);

            if (m != null) {
                if (m.getY() >= cameraPositionY - halfScreenHeight
                        && m.getY() <= cameraPositionY + halfScreenHeight) {

                    if (m.bounds.overlaps(p.bounds)) {
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
        if (shouldAddSpeech(player) && speeches[index] == null) {
            String speech = m.getSpeech();
            String[] words = speech.split(" ");
            float w = SpeechManager.getInstance().getLabelWidth(words);
            float x = m.getX() - (0.5f * w) + (0.5f * GameScreen.tileSize);

            float yOffset = 1.2f * GameScreen.tileSize;
            if (words.length <= 4) {
                yOffset *= 0.6;
            }
            float y = m.getY() + yOffset;
            speeches[index] = SpeechManager.getInstance().getLabel(speech, x, y, w, m.getSpeechColor());
            speeches[index].addAction(getSpeechAction(ThreadLocalRandom.current().nextFloat() + 1f, index));
        }
    }

    private boolean shouldAddSpeech(Player player) {
        int var1 = player.getMushroomsCount() / 3;
        if (var1 == 0) {
            return false;
        } else {
            return ThreadLocalRandom.current().nextInt(Math.max(256, GameScreen.SPEECH_SEED / var1)) / 8 == 0;
        }
    }

    public Array<Mushroom> getMushrooms() {
        return gameObjects;
    }

    public Label[] getSpeeches() {
        return speeches;
    }

    private SequenceAction getSpeechAction(float duration, final int speechIndex) {
        SequenceAction seq = new SequenceAction();
        seq.addAction(Actions.delay(duration));
        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                speeches[speechIndex].remove();
                speeches[speechIndex] = null;
            }
        }));
        return seq;
    }
}
