package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;

class MushroomsManager {

    private static final String POWER_MUSHROOM = "power_mushroom.txt";
    private static final String SPEED_MUSHROOM = "speed_mushroom.txt";
    private static final String MUSHROOM = "mushroom.txt";
    private static final String ZERO = "0";

    private Random random;

    MushroomsManager(Random random) {
        this.random = random;
    }

    ArrayList<Mushroom> mushrooms = new ArrayList<>();
    ArrayList<Speech> mushroomsSpeeches = new ArrayList<>(8);

    void initMushrooms(Rectangle[] rooms) {
        Random rand = new Random();
        int[] counters = getMushroomsCounts(rooms);
        for (int i = 0; i < rooms.length; i++) {
            Rectangle room = rooms[i];
            int mushroomsCount = counters[i];
            for (int j = 0; j < mushroomsCount; j++) {
                int x = pickRandomPointBetween(rand, (int) 1, (int) (room.width - 2));
                int y = pickRandomPointBetween(rand, (int) room.y + 1, (int) (room.y + (room.height - 1)));
                if (x == 0 || x == room.width - 1 || y == room.y || y == room.height) {
                    Gdx.app.log("kifio", "Wrong!");
                }
                mushrooms.add(new Mushroom(x, y, POWER_MUSHROOM, random));
            }
        }
    }

    private int pickRandomPointBetween(Random rand, int p1, int p2) {
        return rand.nextInt(p2) + p1;
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
        Iterator<Mushroom> iterator = mushrooms.iterator();
        while (iterator.hasNext()) {
            Mushroom m = iterator.next();
            
            if (m.getY() >= cameraPositionY - halfScreenHeight
                && m.getY() <= cameraPositionY + halfScreenHeight) {
                if (m.bounds.overlaps(player.bounds)) {
                    m.remove();
                    iterator.remove();
                    if (m.getEffect() != null)
                        player.onEffectiveMushroomTake(m);
                    player.increaseMushroomCount();
                } else if (!player.getMushroomsCount().equals(ZERO)) {
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
            String speech = SpeechManager.getInstance().getRandomMushroomSpeech(Gdx.graphics.getDensity());
            mushroomsSpeeches.add(new Speech(m, speech));
        }
    }


    void dispose() {
        Iterator<Speech> speechIterator = mushroomsSpeeches.iterator();
        while (speechIterator.hasNext()) {
            speechIterator.next().dispose();
            speechIterator.remove();
        }
        mushrooms.clear();
        mushroomsSpeeches = null;
        mushrooms = null;
    }

}
