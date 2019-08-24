package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;
import model.Exit;

public class ExitsManager {

    private static final int INITIAL_EXITS_CAPACITY = 2;

    private Random random;

    ExitsManager(Random random) {
        this.random = random;
    }

    private ArrayList<ExitWrapper> exits = new ArrayList<>(INITIAL_EXITS_CAPACITY);
    ArrayList<Speech> exitsSpeeches = new ArrayList<>(3);

    void init(List<Exit> exits) {
        for (Exit exit : exits) {
            this.exits.add(new ExitWrapper(exit));
        }
    }

    void updateExits() {

        long currentTime = System.currentTimeMillis();

        // Удаляем просроченные реплики
        Iterator<Speech> speechIterator = exitsSpeeches.iterator();
        while (speechIterator.hasNext()) {
            Speech sp = speechIterator.next();
            if (currentTime - sp.getStartTime() > Speech.LIFETIME) {
                sp.dispose();
                speechIterator.remove();
            }
        }

        for (ExitWrapper exit : exits) {
            for (Speech speech : exitsSpeeches) {
                if (speech.getSpeechProducer().equals(exit)) return;
            }

            if (random.nextInt(128) / 8 == 0) {
                String speech = SpeechManager.getInstance().getRandomExitSpeech(Gdx.graphics.getDensity());
                exitsSpeeches.add(new Speech(exit, speech));
            }
        }
    }

    static class ExitWrapper implements Speech.SpeechProducer {

        private Exit exit;

        ExitWrapper(Exit exit) {
            this.exit = exit;
        }

        @Override
        public Vector2 getSpeechPosition(String speech) {
            float speechWidth = SpeechManager.getInstance().getTextWidth(speech);
            float xOffset = speechWidth - (0.8f * GameScreen.tileSize);
            return new Vector2(
                    GameScreen.tileSize * exit.getX() - xOffset,
                    GameScreen.tileSize * exit.getY() + (GameScreen.tileSize / 2)
            );
        }
    }

    void dispose() {
        Iterator<Speech> speechIterator = exitsSpeeches.iterator();
        while (speechIterator.hasNext()) {
            speechIterator.next().dispose();
            speechIterator.remove();
        }
        exits.clear();
        exitsSpeeches = null;
        exits = null;
        random = null;
    }
}
