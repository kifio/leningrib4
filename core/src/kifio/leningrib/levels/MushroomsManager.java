package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import generator.ConstantsConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import kifio.leningrib.Utils;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

class MushroomsManager extends ObjectsManager<Mushroom> {

    private static final String ZERO = "3";
    private Set<Mushroom> removedMushrooms = new HashSet<>();

    MushroomsManager(Random random) {
        this.random = random;
        gameObjects = new ArrayList<>();
    }

    private Label[] mushroomsSpeeches;

    void initMushrooms(ConstantsConfig config, List<Actor> trees) {
        int levelHeight = config.getLevelHeight();
        int levelWidth = config.getLevelWidth();

        for (int i = 1; i < levelHeight; i++) {
            boolean b = true;
            if (b) {
                int x = GameScreen.tileSize * (1 + random.nextInt(levelWidth - 2));
                int y = GameScreen.tileSize * i;
                if (!Utils.isOverlapsWithActor(trees, x, y)) {
                    gameObjects.add(new Mushroom(x, y, random));
                }
            }
        }

        mushroomsSpeeches = new Label[gameObjects.size()];
    }

    void initMushrooms(List<Mushroom> mushrooms) {
        this.gameObjects.addAll(mushrooms);
    }

    void updateMushrooms(Player p, float cameraPositionY) {

        int halfScreenHeight = Gdx.graphics.getHeight() / 2;

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        for (int index = 0; index < gameObjects.size(); index++) {
            Mushroom m = gameObjects.get(index);

            if (m.getY() >= cameraPositionY - halfScreenHeight
                && m.getY() <= cameraPositionY + halfScreenHeight) {

                if (m.bounds.overlaps(p.bounds)) {
                    m.setEaten();
                    m.clear();
                    m.remove();

                    removedMushrooms.add(m);

                    if (mushroomsSpeeches[index] != null) {
                        mushroomsSpeeches[index].remove();
                        mushroomsSpeeches[index] = null;
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

        gameObjects.removeAll(removedMushrooms);
        removedMushrooms.clear();
    }

    private void addMushroomSpeech(Player player, Mushroom m, int index) {
        // С некоторой вероятностью добавляем новую речь
        if (shouldAddSpeech(player) && mushroomsSpeeches[index] == null) {
            String speech = SpeechManager.getInstance().getRandomMushroomSpeech();
            float x = m.getX() - GameScreen.tileSize / 2f;
            float y = m.getY() + GameScreen.tileSize * 1.5f;
            mushroomsSpeeches[index] = SpeechManager.getInstance().getLabel(speech, x, y, GameScreen.tileSize * 2);
            mushroomsSpeeches[index].addAction(getSpeechAction(random.nextFloat() + 1f, index));
        }
    }

    private boolean shouldAddSpeech(Player player) {
        int var1 = player.getMushroomsCount() / 5;
        if (var1 == 0) {
            return false;
        } else {
            return random.nextInt(Math.max(256, GameScreen.SPEECH_SEED / var1)) / 8 == 0;
        }
    }

    public List<Mushroom> getMushrooms() {
        return gameObjects;
    }

    public Label[] getSpeeches() {
        return mushroomsSpeeches;
    }

    private SequenceAction getSpeechAction(float duration, final int i) {
        SequenceAction seq = new SequenceAction();
        seq.addAction(Actions.delay(duration));
        seq.addAction(Actions.run(new Runnable() {
            @Override public void run() {
                mushroomsSpeeches[i] = null;
            }
        }));
        seq.addAction(Actions.removeActor());
        return seq;
    }

    @Override
    public void dispose() {

        for (int i = 0; i < mushroomsSpeeches.length; i++) {
            if (mushroomsSpeeches[i] != null) {
                mushroomsSpeeches[i].remove();
                mushroomsSpeeches[i] = null;
            }
        }

        super.dispose();
    }
}
