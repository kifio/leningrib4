package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;

public class MushroomsManager {

    private Random random;

    MushroomsManager(Random random) {
        this.random = random;
    }

    ArrayList<Mushroom> mushrooms = new ArrayList<>();
    ArrayList<Speech> mushroomsSpeeches = new ArrayList<>(8);

    void initMushrooms(ArrayList<Mushroom> mushrooms) {
        this.mushrooms = mushrooms;
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
                player.increaseMushroomCount();
            } else if (player.getMushroomsCount() > 0) {
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
                    SpeechManager.getInstance().getRandomMushroomSpeech(Gdx.graphics.getDensity() / 2)));
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
