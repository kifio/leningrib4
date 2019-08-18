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
import model.Room;

class MushroomsManager {

    private static final String POWER_MUSHROOM = "power_mushroom.txt";
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
                int x = pickRandomPointBetween(rand, room.x, (room.x + 1) + (room.width - 2));
                int y = pickRandomPointBetween(rand, room.y, room.y + (room.height - 1));
                mushrooms.add(new Mushroom(x, y, POWER_MUSHROOM, random));
            }
        }
    }

    private int pickRandomPointBetween(Random rand, float p1, float p2) {
        if (p1 == p2) return MathUtils.round(p1);
        float delta = p2 - p1;
        float offset = rand.nextFloat() * delta;
        return MathUtils.round(p1 + offset);
    }

    private int[] getMushroomsCounts(Rectangle[] rooms) {
        int[] counters = new int[rooms.length];
        for (int i = 0; i < rooms.length; i++) {
            counters[i] = random.nextInt((int) rooms[i].height);
        }
        return counters;
    }

    void updateMushrooms(float stateTime, Player player) {

        // Удаляем просроченные реплики
        Iterator<Speech> speechIterator = mushroomsSpeeches.iterator();
        while (speechIterator.hasNext()) {
            Speech sp = speechIterator.next();
            if (stateTime - sp.getStartTime() > 1) {
                sp.dispose();
                speechIterator.remove();
            }
        }

        // Удаляем съеденные грибы, несъеденным добавляем реплики
        Iterator<Mushroom> iterator = mushrooms.iterator();
        while (iterator.hasNext()) {
            Mushroom m = iterator.next();
            if (m.bounds.overlaps(player.bounds)) {
                m.remove();
                iterator.remove();
                if (m.getEffect() != null) player.onEffectiveMushroomTake(m);
                player.increaseMushroomCount();
            } else if (!player.getMushroomsCount().equals(ZERO)) {
                if (mushroomsSpeeches.size() > 0) return;
                addMushroomSpeech(m, stateTime);
            }
        }
    }

    private void addMushroomSpeech(Speech.SpeechProducer m, float stateTime) {

        for (Speech speech : mushroomsSpeeches) {
            if (speech.getSpeechProducer().equals(m)) return;
        }

        // С некоторой вероятностью добавляем новую речь
        if (random.nextInt(128) / 8 == 0) {
            mushroomsSpeeches.add(new Speech(m, stateTime,
                    SpeechManager.getInstance().getRandomMushroomSpeech(Gdx.graphics.getDensity())));
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
