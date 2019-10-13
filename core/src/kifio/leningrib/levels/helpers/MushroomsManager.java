package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.Gdx;
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

    public void updateMushrooms(Player p, float cameraPositionY) {

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

//                        if (m.getEffect() != 0) {
                            p.onEffectiveMushroomTake(m);
//                        }

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
        if (speeches[index] == null) {
            float x = m.getX() - GameScreen.tileSize / 2f;
            float y = m.getY() + GameScreen.tileSize;
            speeches[index] = SpeechManager.getInstance().getLabel("", x, y, GameScreen.tileSize * 2, m.getEffect());
        }

        if (shouldAddSpeech(player) && speeches[index].textEquals("")) {
            String speech = getSpeech(m.getEffect());
            speeches[index].setText(speech);
            speeches[index].addAction(getSpeechAction(ThreadLocalRandom.current().nextFloat() + 1f, index));
        }
    }

    private String getSpeech(int effect) {
        switch (effect) {
            case Mushroom.STRENGTH:
                return SpeechManager.getInstance().getPowerMushroomSpeech();
            case Mushroom.SPEED:
                return SpeechManager.getInstance().getSpeedMushroomSpeech();
            case Mushroom.DEXTERITY:
                return SpeechManager.getInstance().getDexterityMushroomSpeech();
            case Mushroom.INVISIBILITY:
                return SpeechManager.getInstance().getInvisibilityMushroomSpeech();
            default:
                return SpeechManager.getInstance().getRandomMushroomSpeech();
        }
    }

    private boolean shouldAddSpeech(Player player) {
        int var1 = 1; // player.getMushroomsCount() / 5;
        if (var1 == 0) {
            return false;
        } else {
            return true;
            // return ThreadLocalRandom.current().nextInt(Math.max(256, GameScreen.SPEECH_SEED / var1)) / 8 == 0;
        }
    }

    public Array<Mushroom> getMushrooms() {
        return gameObjects;
    }

    public Label[] getSpeeches() {
        return speeches;
    }

    private SequenceAction getSpeechAction(float duration, final int i) {
        SequenceAction seq = new SequenceAction();
        seq.addAction(Actions.delay(duration));
        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                speeches[i].setText(null);
            }
        }));
        return seq;
    }
}
