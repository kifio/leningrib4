package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

class MushroomsManager extends ObjectsManager<Mushroom> {

    private static final String ZERO = "0";

    MushroomsManager(Random random) {
        this.random = random;
        gameObjects = new ArrayList<>();
    }

    ArrayList<Speech> mushroomsSpeeches = new ArrayList<>(8);

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

        long currentTime = System.currentTimeMillis();
        int halfScreenHeight = Gdx.graphics.getHeight() / 2;

        // Удаляем просроченные реплики
        Iterator<Speech> speechIterator = mushroomsSpeeches.iterator();
        while (speechIterator.hasNext()) {
            Speech sp = speechIterator.next();
            if (currentTime - sp.getStartTime() > Speech.LIFETIME) {
                sp.dispose();
                speechIterator.remove();
            }
        }

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        // TODO: заменить на for (0..length)
        Iterator<Mushroom> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            Mushroom m = iterator.next();
            if (m.getY() >= cameraPositionY - halfScreenHeight
                && m.getY() <= cameraPositionY + halfScreenHeight) {
                if (m.bounds.overlaps(player.bounds)) {
                    m.setEaten();
                    m.remove();
                    iterator.remove();
                    if (m.getEffect() != null)
                        player.onEffectiveMushroomTake(m);
                    player.increaseMushroomCount();
//                } else if (!player.getMushroomsCount().equals(ZERO)) {
                } else if (true) {
                    if (mushroomsSpeeches.size() > 0)
                        return;
                    addMushroomSpeech(m);
                }
            }
        }
    }

    private void addMushroomSpeech(Speech.SpeechProducer m) {

        for (Speech speech : mushroomsSpeeches) {
            if (speech.getSpeechProducer().equals(m)) return;
        }

        // С некоторой вероятностью добавляем новую речь
        if (random.nextInt(128) / 8 == 0) {
            String speech = SpeechManager.getInstance().getRandomMushroomSpeech();
            mushroomsSpeeches.add(new Speech(m, speech));
        }
    }

    public List<Mushroom> getMushrooms() {
        return gameObjects;
    }

    @Override
    public void dispose() {

        Iterator<Speech> speechIterator = mushroomsSpeeches.iterator();
        while (speechIterator.hasNext()) {
            speechIterator.next().dispose();
            speechIterator.remove();
        }

        mushroomsSpeeches = null;
        super.dispose();
    }
}
