package kifio.leningrib.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;

public class ResourcesManager {

    private static HashMap<String, TextureRegion> regions = new HashMap<>();
    private static I18NBundle mushroomsSpeechBundle;
    private static I18NBundle exitsSpeechBumdle;

    public static void init() {

        AssetManager am = new AssetManager();
        am.load("overworld.png", Texture.class);
        am.load("forest_tiles.png", Texture.class);
        am.load("power_mushroom.png", Texture.class);
        am.load("bottle.png", Texture.class);
        am.load("i18n/mushroom_speech", I18NBundle.class);
        am.load("i18n/exit_speech", I18NBundle.class);
        am.finishLoading();

        Texture overworld = am.get("overworld.png");
        Texture forestTiles = am.get("forest_tiles.png");

        regions.put("tree_0", new TextureRegion(overworld, 80, 256, 16, 16));
        regions.put("tree_1", new TextureRegion(overworld, 80, 272, 16, 16));
        regions.put("tree_2", new TextureRegion(overworld, 96, 256, 16, 16));
        regions.put("tree_3", new TextureRegion(overworld, 96, 272, 16, 16));
        regions.put("log_0", new TextureRegion(overworld, 16 * 3, 16 * 5, 16, 16));
        regions.put("log_1", new TextureRegion(overworld, 16 * 4, 16 * 5, 16, 16));
        regions.put("log_2", new TextureRegion(overworld, 16 * 5, 16 * 5, 16, 16));
        regions.put("stone_0", new TextureRegion(overworld, 16 * 6, 16 * 5, 16, 16));
        regions.put("stone_1", new TextureRegion(overworld, 16 * 7, 16 * 5, 16, 16));
        regions.put("stone_2", new TextureRegion(overworld, 16 * 8, 16 * 5, 16, 16));
        regions.put("stone_3", new TextureRegion(overworld, 16 * 9, 16 * 5, 16, 16));
        regions.put("stone_4", new TextureRegion(overworld, 16 * 10, 16 * 5, 16, 16));

        for (int i = 0; i < 15; i++) {
            int row = i / 5, col = i % 5;
            regions.put("grass_" + i, new TextureRegion(forestTiles, row * 32, col * 32, 32, 32));
        }

        mushroomsSpeechBundle = am.get("i18n/mushroom_speech", I18NBundle.class);
        exitsSpeechBumdle = am.get("i18n/exit_speech", I18NBundle.class);
    }

    public static TextureRegion get(String name) {
        return regions.get(name);
    }

    public static I18NBundle getMushroomSpeechBundle() {
        return mushroomsSpeechBundle;
    }

    public static I18NBundle getExitSpeechBundle() {
        return exitsSpeechBumdle;
    }
}
