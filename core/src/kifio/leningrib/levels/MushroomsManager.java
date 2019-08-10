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

    // TODO: Генерировать грибы в генераторе
    private static final String NEW_LINE = "\n";
    private static final String COMMA = ",";
    private static final String POWER_MUSHROOM = "power_mushroom.txt";

    ArrayList<Mushroom> mushrooms = new ArrayList<>();
    ArrayList<Speech> mushroomsSpeeches = new ArrayList<>(8);

    void initMushrooms(String fileName) {
        try {
            FileHandle handle = Gdx.files.internal(fileName);
            String content = handle.readString();
            String[] positions = content.split(NEW_LINE);

            for (String position : positions) {
                String[] coordinates = position.split(COMMA);
                mushrooms.add(new Mushroom(Integer.parseInt(coordinates[0]),
                        Integer.parseInt(coordinates[1]), POWER_MUSHROOM));
            }
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
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
            mushroomsSpeeches.add(new Speech(m, stateTime, SpeechManager.getInstance().getRandomMushroomSpeech()));
        }
    }

}
