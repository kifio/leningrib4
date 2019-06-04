package kifio.leningrib.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;

public class ResourcesManager {

    private static HashMap<String, TextureRegion> regions = new HashMap<>();
    private static I18NBundle mushroomsSpeechBundle;

    public static void init() {

        AssetManager am = new AssetManager();
        am.load("overworld.png", Texture.class);
        am.load("forest_tiles.png", Texture.class);
        am.load("power_mushroom.png", Texture.class);
        am.load("mushroom.png", Texture.class);
        am.load("mushroom.png", Texture.class);
        am.load("i18n/mushroom_speech", I18NBundle.class);
        am.finishLoading();

        Texture overworld = am.get("overworld.png");

        regions.put("tree_0", new TextureRegion(overworld, 80, 256, 16, 16));
        regions.put("tree_1", new TextureRegion(overworld, 80, 272, 16, 16));
        regions.put("tree_2", new TextureRegion(overworld, 96, 272, 16, 16));
        regions.put("tree_3", new TextureRegion(overworld, 96, 256, 16, 16));

        Texture forestTiles = am.get("forest_tiles.png");
        regions.put("m_0", new TextureRegion(forestTiles, 416, 0, 32, 32));

        for (int i = 0, k = 0; i < 15; i++) {
            int row = i / 5, col = i % 5;
            regions.put("grass_" + i, new TextureRegion(forestTiles, row * 32, col * 32, 32, 32));
        }

        Texture powerMushroom = am.get("power_mushroom.png");
        regions.put("pm_0", new TextureRegion(powerMushroom, 0, 0, 96, 96));
        regions.put("pm1", new TextureRegion(powerMushroom, 0, 96, 96, 96));

        mushroomsSpeechBundle = am.get("i18n/mushroom_speech", I18NBundle.class);
    }

    public static TextureRegion get(String name) {
        return regions.get(name);
    }

    public static I18NBundle getMushroomSpeechBundle() {
        return mushroomsSpeechBundle;
    }
}
