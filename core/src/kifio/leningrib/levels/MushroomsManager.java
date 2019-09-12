package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.Utils;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

class MushroomsManager extends ObjectsManager<Mushroom> {

    private Array<Mushroom> removedMushrooms = new Array<>(4);

    MushroomsManager() {
        gameObjects = new Array<>();
    }
    
    void initMushrooms(ConstantsConfig config, Array<Actor> trees) {
        int levelHeight = config.getLevelHeight();
        int levelWidth = config.getLevelWidth();

        for (int i = 1; i < levelHeight; i++) {
            int x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2));
            int y = GameScreen.tileSize * i;
            if (!Utils.isOverlapsWithActor(trees, x, y)) {
                gameObjects.add(new Mushroom(x, y));
            }
        }

        speeches = new Label[gameObjects.size];
    }

    void initMushrooms(Array<Mushroom> mushrooms) {
        this.gameObjects.addAll(mushrooms);
    }

    void updateMushrooms(Player p, float cameraPositionY) {

        int halfScreenHeight = Gdx.graphics.getHeight() / 2;

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        for (int index = 0; index < gameObjects.size; index++) {
            Mushroom m = gameObjects.get(index);

            if (m.getY() >= cameraPositionY - halfScreenHeight
                && m.getY() <= cameraPositionY + halfScreenHeight) {

                if (m.bounds.overlaps(p.bounds)) {
                    m.setEaten();
                    m.clear();
                    m.remove();

                    removedMushrooms.add(m);

                    if (speeches[index] != null) {
                        speeches[index].remove();
                        speeches[index] = null;
                    }

                    if (m.getEffect() != null) {
                        p.onEffectiveMushroomTake(m);
                    }

                    p.increaseMushroomCount();
                } else {
                    addMushroomSpeech(p, m, index);
                }
            }
        }

        gameObjects.removeAll(removedMushrooms, false);
        removedMushrooms.clear();
    }

    private void addMushroomSpeech(Player player, Mushroom m, int index) {
        // С некоторой вероятностью добавляем новую речь
        if (shouldAddSpeech(player) && speeches[index] == null) {
            String speech = SpeechManager.getInstance().getRandomMushroomSpeech();
            float x = m.getX() - GameScreen.tileSize / 2f;
            float y = m.getY() + GameScreen.tileSize * 1.5f;
            speeches[index] = SpeechManager.getInstance().getLabel(speech, x, y, GameScreen.tileSize * 2);
            speeches[index].addAction(getSpeechAction(ThreadLocalRandom.current().nextFloat() + 1f, index));
        }
    }

    private boolean shouldAddSpeech(Player player) {
        int var1 = player.getMushroomsCount() / 5;
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

    private SequenceAction getSpeechAction(float duration, final int i) {
        SequenceAction seq = new SequenceAction();
        seq.addAction(Actions.delay(duration));
        seq.addAction(Actions.run(new Runnable() {
            @Override public void run() {
                speeches[i] = null;
            }
        }));
        seq.addAction(Actions.removeActor());
        return seq;
    }
}
