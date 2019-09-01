package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

class MushroomsManager extends ObjectsManager<Mushroom> {

    private static final String ZERO = "3";
    private int index = 0;

    MushroomsManager(Random random) {
        this.random = random;
        gameObjects = new ArrayList<>();
    }

    private Label[] mushroomsSpeeches;

    void initMushrooms(Rectangle[] rooms, List<Actor> trees) {
        int[] counters = getMushroomsCounts(rooms);
        for (int i = 0; i < rooms.length; i++) {
            Rectangle room = rooms[i];
            int mushroomsCount = counters[i];

            for (int j = 0; j < mushroomsCount; j++) {
                int x = GameScreen.tileSize * (random.nextInt((int) room.width - 1));
                int y = GameScreen.tileSize * ((int) room.y + random.nextInt((int) (room.height - 1)));

                if (!isOverlapsWithActor(trees, x, y)) {
                    gameObjects.add(new Mushroom(x, y, random));
                }
            }
        }

        mushroomsSpeeches = new Label[gameObjects.size()];
    }

    void initMushrooms(List<Mushroom> mushrooms) {
        this.gameObjects.addAll(mushrooms);
    }

    private boolean isOverlapsWithActor(List<Actor> actors, int x, int y) {
        for (int k = 0; k < actors.size(); k++) {
            Actor a = actors.get(k);
            if ((int) a.getX() == x && (int) a.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private int[] getMushroomsCounts(Rectangle[] rooms) {
        int[] counters = new int[rooms.length];
        for (int i = 0; i < rooms.length; i++) {
            counters[i] = random.nextInt((int) rooms[i].height);
        }
        return counters;
    }

    void updateMushrooms(Player player, float cameraPositionY) {

        int halfScreenHeight = Gdx.graphics.getHeight() / 2;

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        index = 0;
        Iterator<Mushroom> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            Mushroom m = iterator.next();
            if (m.getY() >= cameraPositionY - halfScreenHeight
                && m.getY() <= cameraPositionY + halfScreenHeight) {
                if (m.bounds.overlaps(player.bounds)) {
                    m.setEaten();
                    m.remove();
                    iterator.remove();
                    if (mushroomsSpeeches[index] != null) {
                        mushroomsSpeeches[index].remove();
                        mushroomsSpeeches[index] = null;
                    }
                    if (m.getEffect() != null)
                        player.onEffectiveMushroomTake(m);
                    player.increaseMushroomCount();
                } else if (!player.getMushroomsCount().equals(ZERO)) {
                    addMushroomSpeech(m);
                }
            }
            index++;
        }
    }

    private void addMushroomSpeech(Mushroom m) {

        // С некоторой вероятностью добавляем новую речь
        if (random.nextInt(128) / 8 == 0 && mushroomsSpeeches[index] == null) {
            String speech = SpeechManager.getInstance().getRandomMushroomSpeech();
            Vector2 pos = new Vector2(
                m.getX() - GameScreen.tileSize / 2f,
                m.getY() + GameScreen.tileSize / 2f
            );
            mushroomsSpeeches[index] = SpeechManager.getInstance().getLabel(speech, pos.x, pos.y, GameScreen.tileSize * 2);
            mushroomsSpeeches[index].addAction(getSpeechAction(1f));
        }
    }

    public List<Mushroom> getMushrooms() {
        return gameObjects;
    }

    public Label[] getSpeeches() {
        return mushroomsSpeeches;
    }

    private SequenceAction getSpeechAction(float duration) {
        final int i = index;
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
