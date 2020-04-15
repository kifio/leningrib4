package kifio.leningrib.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;

import generator.SegmentType;

public class ResourcesManager {

    private static final int TILE_SIZE = 16; // Размер тайла который мы вырезаем из png в пикселя
    public static final String GRASS_0 = "grass_0";
    public static final String HUD_BOTTLE = "bottle_hud.png";
    public static final String HUD_BOTTLE_PRESSED = "bottle_hud_pressed.png";
    public static final String HUD_PAUSE = "pause_hud.png";
    public static final String HUD_PAUSE_PRESSED = "pause_hud_pressed.png";
    public static final String HUD_BACKGROUND = "background_hud.png";
    public static final String BACK = "back.png";
    public static final String RESUME = "resume.png";
    public static final String MENU = "menu.png";
    public static final String RESTART = "restart.png";


    public static HashMap<String, TextureRegion> regions = new HashMap<>();
    public static I18NBundle commonMushroomsSpeechBundle;
    public static I18NBundle powerMushroomsSpeechBundle;
    public static I18NBundle dexterityMushroomsSpeechBundle;
    public static I18NBundle invisibilityMushroomsSpeechBundle;
    public static I18NBundle speedMushroomsSpeechBundle;
    public static I18NBundle forestersSpeechesPatrolBundle;
    public static I18NBundle forestersSpeechesPursuitBundle;
    public static I18NBundle forestersSpeechesFearBundle;
    public static I18NBundle forestersSpeechesInvisiblePlayerBundle;
    public static I18NBundle forestersSpeechesStopBundle;
    public static I18NBundle forestersSpeechesDrinking;
    public static I18NBundle forestersSpeechesDrunk;
    public static I18NBundle forestersSpeechesRunToBottle;
    public static I18NBundle grandmaSpeechesBundle;

    private static AssetManager am;

    // В аргументах размеры уровня в тайлах
    public static void init(int levelWidth, int levelHeight) {
        long start = System.nanoTime();

        am = new AssetManager();
        am.load("grass_2.png", Texture.class);
        am.load("power_mushroom.png", Texture.class);
        am.load("speed_mushroom.png", Texture.class);
        am.load("dexterity_mushroom.png", Texture.class);
        am.load("common_mushroom.png", Texture.class);
        am.load("bottle.png", Texture.class);
        am.load("bottle_smol.png", Texture.class);

        am.load("enemy_1_run.png", Texture.class);
        am.load("enemy_1_idle.png", Texture.class);
        am.load("enemy_2_run.png", Texture.class);
        am.load("enemy_2_idle.png", Texture.class);
        am.load("trees_map.png", Texture.class);
        am.load("resume.png", Texture.class);
        am.load("menu.png", Texture.class);

        am.load(HUD_BOTTLE, Texture.class);
        am.load(HUD_BOTTLE_PRESSED, Texture.class);
        am.load(HUD_PAUSE, Texture.class);
        am.load(HUD_PAUSE_PRESSED, Texture.class);
        am.load(HUD_BACKGROUND, Texture.class);
        am.load(BACK, Texture.class);
        am.load(RESTART, Texture.class);
        am.load(RESUME, Texture.class);
        am.load(MENU, Texture.class);

        am.load("i18n/mushroom_speech", I18NBundle.class);
        am.load("i18n/mushroom_power_speech", I18NBundle.class);
        am.load("i18n/mushroom_invisibility_speech", I18NBundle.class);
        am.load("i18n/mushroom_speed_speech", I18NBundle.class);
        am.load("i18n/mushroom_dexterity_speech", I18NBundle.class);
        am.load("i18n/foresters_speeches_fear", I18NBundle.class);
        am.load("i18n/foresters_speeches_patrol", I18NBundle.class);
        am.load("i18n/foresters_speeches_pursuit", I18NBundle.class);
        am.load("i18n/foresters_speeches_stop", I18NBundle.class);
        am.load("i18n/foresters_speeches_invisible_player", I18NBundle.class);
        am.load("i18n/foresters_speeches_drinking", I18NBundle.class);
        am.load("i18n/foresters_speeches_drunk", I18NBundle.class);
        am.load("i18n/foresters_speeches_run_to_bottle", I18NBundle.class);
        am.load("i18n/grandma_speeches", I18NBundle.class);
        am.finishLoading();

        Gdx.app.log("kifio", "loading take: " + (System.nanoTime() - start) / 1000 + "ms");
        start = System.nanoTime();
        Texture treesMap = am.get("trees_map.png");

        putTrees(treesMap);

        putTexture(HUD_BOTTLE);
        putTexture(HUD_BOTTLE_PRESSED);
        putTexture(HUD_PAUSE);
        putTexture(HUD_PAUSE_PRESSED);
        putTexture(HUD_BACKGROUND);
        putTexture(BACK);
        putTexture(RESTART);
        putTexture(RESUME);
        putTexture(MENU);


        Texture grassTexture = am.get("grass_2.png");
        regions.put(GRASS_0, new TextureRegion(grassTexture, 0, 0, TILE_SIZE * 2, TILE_SIZE * 2));

        Gdx.app.log("kifio", "building regions take: " + (System.nanoTime() - start) / 1000 + "ms");

        commonMushroomsSpeechBundle = am.get("i18n/mushroom_speech", I18NBundle.class);
        powerMushroomsSpeechBundle = am.get("i18n/mushroom_power_speech", I18NBundle.class);
        dexterityMushroomsSpeechBundle = am.get("i18n/mushroom_dexterity_speech", I18NBundle.class);
        invisibilityMushroomsSpeechBundle = am.get("i18n/mushroom_invisibility_speech", I18NBundle.class);
        speedMushroomsSpeechBundle = am.get("i18n/mushroom_speed_speech", I18NBundle.class);
        forestersSpeechesPursuitBundle = am.get("i18n/foresters_speeches_pursuit", I18NBundle.class);
        forestersSpeechesFearBundle = am.get("i18n/foresters_speeches_fear", I18NBundle.class);
        forestersSpeechesPatrolBundle = am.get("i18n/foresters_speeches_patrol", I18NBundle.class);
        forestersSpeechesStopBundle = am.get("i18n/foresters_speeches_stop", I18NBundle.class);
        grandmaSpeechesBundle = am.get("i18n/grandma_speeches", I18NBundle.class);
        forestersSpeechesInvisiblePlayerBundle = am.get("i18n/foresters_speeches_invisible_player", I18NBundle.class);
        forestersSpeechesDrinking = am.get("i18n/foresters_speeches_drinking", I18NBundle.class);
        forestersSpeechesDrunk = am.get("i18n/foresters_speeches_drunk", I18NBundle.class);
        forestersSpeechesRunToBottle = am.get("i18n/foresters_speeches_run_to_bottle", I18NBundle.class);
    }

    // Кладем регион размером с текстуру
    private static void putTexture(String key) {
        Texture texture = am.get(key);
        regions.put(key, new TextureRegion(
                texture,
                0, 0,
                texture.getWidth(),
                texture.getHeight()));
    }

    private static void putTrees(Texture treesMap) {
        regions.put(SegmentType.BOTTOM_LEFT_CORNER_COMMON_TOP.name(), new TextureRegion(treesMap, 0, 32, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 0, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM.name(), new TextureRegion(treesMap, 48, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP.name(), new TextureRegion(treesMap, 16, 32, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 16, 48, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM.name(), new TextureRegion(treesMap, 64, 48, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_LEFT_CORNER_OUT_BORDER_TOP.name(), new TextureRegion(treesMap, 48, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_LEFT_CORNER_COMMON_TOP.name(), new TextureRegion(treesMap, 0, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_LEFT_CORNER_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 48, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP.name(), new TextureRegion(treesMap, 64, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_RIGHT_CORNER_COMMON_TOP.name(), new TextureRegion(treesMap, 16, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_RIGHT_CORNER_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 16, 16, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_COMMON_LEFT_TOP.name(), new TextureRegion(treesMap, 16, 32, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_COMMON_LEFT_BOTTOM.name(), new TextureRegion(treesMap, 16, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_COMMON_RIGHT_TOP.name(), new TextureRegion(treesMap, 16, 32, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_COMMON_RIGHT_BOTTOM.name(), new TextureRegion(treesMap, 16, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_COMMON_LEFT_TOP.name(), new TextureRegion(treesMap, 16, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_COMMON_LEFT_BOTTOM.name(), new TextureRegion(treesMap, 16, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_COMMON_RIGHT_TOP.name(), new TextureRegion(treesMap, 16, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_COMMON_RIGHT_BOTTOM.name(), new TextureRegion(treesMap, 16, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.LEFT_BEGIN_BOTTOM.name(), new TextureRegion(treesMap, 80, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.LEFT_BEGIN_TOP.name(), new TextureRegion(treesMap, 80, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.LEFT_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 96, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.LEFT_COMMON_TOP.name(), new TextureRegion(treesMap, 96, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.LEFT_END_BOTTOM.name(), new TextureRegion(treesMap, 64, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.LEFT_END_TOP.name(), new TextureRegion(treesMap, 64, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.RIGHT_BEGIN_BOTTOM.name(), new TextureRegion(treesMap, 96, 16, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.RIGHT_BEGIN_TOP.name(), new TextureRegion(treesMap, 96, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.RIGHT_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 112, 16, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.RIGHT_COMMON_TOP.name(), new TextureRegion(treesMap, 112, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.RIGHT_END_BOTTOM.name(), new TextureRegion(treesMap, 80, 16, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.RIGHT_END_TOP.name(), new TextureRegion(treesMap, 80, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_TOP.name(), new TextureRegion(treesMap, 32, 32, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM.name(), new TextureRegion(treesMap, 32, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_TOP.name(), new TextureRegion(treesMap, 48, 32, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM.name(), new TextureRegion(treesMap, 48, 48, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP.name(), new TextureRegion(treesMap, 32, 0, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_ROOM_OUT_LEFT_BORDER_BOTTOM.name(), new TextureRegion(treesMap, 32, 16, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP.name(), new TextureRegion(treesMap, 48, 0, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_BOTTOM.name(), new TextureRegion(treesMap, 48, 16, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.ROOM_WALL_START_TOP.name(), new TextureRegion(treesMap, 64, 32, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.ROOM_WALL_START_BOTTOM.name(), new TextureRegion(treesMap, 64, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.ROOM_WALL_COMMON_TOP.name(), new TextureRegion(treesMap, 80, 32, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.ROOM_WALL_COMMON_BOTTOM.name(), new TextureRegion(treesMap, 80, 48, TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.ROOM_WALL_END_TOP.name(), new TextureRegion(treesMap, 80, 32, -TILE_SIZE, TILE_SIZE));
        regions.put(SegmentType.ROOM_WALL_END_BOTTOM.name(), new TextureRegion(treesMap, 80, 48, -TILE_SIZE, TILE_SIZE));
    }

    public static TextureRegion getRegion(String name) {
        return regions.get(name);
    }

    public static Texture getTexture(String name) {
        return am.get(name.concat(".png"));
    }
}
