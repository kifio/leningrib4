package kifio.leningrib.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class TextureManager {

    private static HashMap<String, TextureRegion> regions = new HashMap<>();

    public static void init() {

        AssetManager am = new AssetManager();
        am.load("overworld.png", Texture.class);
        am.load("power_mushroom.png", Texture.class);
        am.finishLoading();

        Texture overworld = am.get("overworld.png");

        regions.put("tree_0", new TextureRegion(overworld, 80, 256, 16, 16));
        regions.put("tree_1", new TextureRegion(overworld, 80, 272, 16, 16));
        regions.put("tree_2", new TextureRegion(overworld, 96, 272, 16, 16));
        regions.put("tree_3", new TextureRegion(overworld, 96, 256, 16, 16));
        regions.put("grass", new TextureRegion(overworld, 0, 0, 16, 16));

        Texture powerMushroom = am.get("power_mushroom.png");
        regions.put("pm_0", new TextureRegion(powerMushroom, 0, 0, 96, 96));
        regions.put("pm1", new TextureRegion(powerMushroom, 0, 96, 96, 96));

    }

    public static TextureRegion get(String name) {
        return regions.get(name);
    }
}
