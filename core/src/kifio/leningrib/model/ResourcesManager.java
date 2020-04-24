package kifio.leningrib.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;

import generator.SegmentType;

public class ResourcesManager {

    public static final String GRASS_2 = "grass_2.png";
    public static final String GRASS_0 = "grass_0.png";
    public static final String OVERLAY = "overlay.png";
    public static final String HUD_BOTTLE = "bottle_hud.png";
    public static final String HUD_BOTTLE_PRESSED = "bottle_hud_pressed.png";
    public static final String PAUSE = "pause_unpressed.png";
    public static final String PAUSE_PRESSED = "pause_pressed.png";
    public static final String SETTINGS = "settings_unpressed.png";
    public static final String SETTINGS_PRESSED = "settings_pressed.png";
    public static final String HUD_BACKGROUND = "background_hud.png";
    public static final String BACK = "back.png";
    public static final String RESTART = "restart.png";
    public static final String LENIN_GRIB = "lenin_grib.png";
    public static final String LAUNCH_TREES = "launch_trees.png";
    public static final String LAUNCH_PROGRESS_BACKGROUND = "launch_progress_background.png";
    public static final String LAUNCH_PROGRESS_FOREGROUND = "launch_progress_foreground.png";
    public static final String SETTING = "setting_unpressed.png";
    public static final String SETTINGS_BACKGROUND = "settings_background.png";
    public static final String SETTING_ENABLED = "enabled_unpressed.png";
    public static final String SETTING_ENABLED_PRESSED = "enabled_pressed.png";
    public static final String SETTING_DISABLED = "disabled_unpressed.png";
    public static final String SETTING_DISABLED_PRESSED = "disabled_pressed.png";
    public static final String START_GAME = "start_game.png";
    public static final String START_GAME_PRESSED = "start_game_pressed.png";


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

    private static final int TILE_SIZE = 16; // Размер тайла который мы вырезаем из png в пикселя
    private static AssetManager am = new AssetManager();
    private static HashMap<String, TextureRegion> regions = new HashMap<>();

    public static void loadSplash() {
        am.load(LENIN_GRIB, Texture.class);
        am.load(LAUNCH_TREES, Texture.class);
        am.load(LAUNCH_PROGRESS_BACKGROUND, Texture.class);
        am.load(LAUNCH_PROGRESS_FOREGROUND, Texture.class);

        am.finishLoadingAsset(LENIN_GRIB);
        am.finishLoadingAsset(LAUNCH_TREES);
        am.finishLoadingAsset(LAUNCH_PROGRESS_BACKGROUND);
        am.finishLoadingAsset(LAUNCH_PROGRESS_FOREGROUND);

        putTexture(LENIN_GRIB);
        putTexture(LAUNCH_TREES);
        putTexture(LAUNCH_PROGRESS_BACKGROUND);
        putTexture(LAUNCH_PROGRESS_FOREGROUND);
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

    // Кладем регион с произвольными размерами
    private static void putTexture(String key, int w, int h) {
        Texture texture = am.get(key);
        regions.put(key, new TextureRegion(
                texture,
                0, 0, w, h));
    }

    public static TextureRegion getRegion(String name) {
        return regions.get(name);
    }

    public static Texture getTexture(String name) {
        return am.get(name.contains(".png") ? name : name.concat(".png"));
    }

    public static void loadAssets() {
        am.load("trees_map.png", Texture.class);

        am.load(GRASS_0, Texture.class);
        am.load(GRASS_2, Texture.class);
        am.load(OVERLAY, Texture.class);
        am.load(HUD_BOTTLE, Texture.class);
        am.load(HUD_BOTTLE_PRESSED, Texture.class);
        am.load(PAUSE, Texture.class);
        am.load(PAUSE_PRESSED, Texture.class);
        am.load(SETTINGS, Texture.class);
        am.load(SETTINGS_PRESSED, Texture.class);
        am.load(HUD_BACKGROUND, Texture.class);
        am.load(BACK, Texture.class);
        am.load(RESTART, Texture.class);
        am.load(START_GAME, Texture.class);
        am.load(START_GAME_PRESSED, Texture.class);
        am.load(SETTING, Texture.class);
        am.load(SETTINGS_BACKGROUND, Texture.class);
        am.load(SETTING_ENABLED, Texture.class);
        am.load(SETTING_ENABLED_PRESSED, Texture.class);
        am.load(SETTING_DISABLED, Texture.class);
        am.load(SETTING_DISABLED_PRESSED, Texture.class);

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
    }

    public static void buildRegions() {
        putTrees((Texture) am.get("trees_map.png"));
        putTexture(HUD_BOTTLE);
        putTexture(OVERLAY);
        putTexture(HUD_BOTTLE_PRESSED);
        putTexture(PAUSE);
        putTexture(PAUSE_PRESSED);
        putTexture(SETTINGS);
        putTexture(SETTINGS_PRESSED);
        putTexture(PAUSE_PRESSED);
        putTexture(HUD_BACKGROUND);
        putTexture(BACK);
        putTexture(RESTART);
        putTexture(SETTING);
        putTexture(SETTINGS_BACKGROUND);
        putTexture(SETTING_ENABLED);
        putTexture(SETTING_ENABLED_PRESSED);
        putTexture(SETTING_DISABLED);
        putTexture(SETTING_DISABLED_PRESSED);
        putTexture(START_GAME);
        putTexture(START_GAME_PRESSED);
        putTexture(GRASS_0, TILE_SIZE * 2, TILE_SIZE * 2);
        putTexture(GRASS_2, TILE_SIZE * 2, TILE_SIZE * 2);
    }

    public static void initializeSpeeches() throws InterruptedException {
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
}
