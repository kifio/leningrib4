package kifio.leningrib.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;

public class ResourcesManager {

	public static final String GRASS_0 = "grass_0";
	public static final String GRASS_1 = "grass_1";
	public static HashMap<String, TextureRegion> regions = new HashMap<>();
	public static I18NBundle commonMushroomsSpeechBundle;
	public static I18NBundle powerMushroomsSpeechBundle;
	public static I18NBundle dexterityMushroomsSpeechBundle;
	public static I18NBundle invisibilityMushroomsSpeechBundle;
	public static I18NBundle speedMushroomsSpeechBundle;
	public static I18NBundle forestersSpeechesPatrolBundle;
	public static I18NBundle forestersSpeechesPursuitBundle;
	public static I18NBundle forestersSpeechesAlarmBundle;
	public static I18NBundle forestersSpeechesFearBundle;
	public static I18NBundle forestersSpeechesInvisiblePlayerBundle;
	public static I18NBundle forestersSpeechesStopBundle;
	public static I18NBundle grandmaSpeechesBundle;

	private static AssetManager am;

	public static void init() {

		am = new AssetManager();
		am.load("overworld.png", Texture.class);
		am.load("trees_horizontal.png", Texture.class);
		am.load("trees_vertical.png", Texture.class);
		am.load("power_mushroom.png", Texture.class);
		am.load("speed_mushroom.png", Texture.class);
		am.load("dexterity_mushroom.png", Texture.class);
		am.load("bottle.png", Texture.class);
		am.load("player_run.png", Texture.class);
		am.load("player_idle.png", Texture.class);
		am.load("enemy_1_run.png", Texture.class);
		am.load("enemy_1_idle.png", Texture.class);
		am.load("enemy_2_run.png", Texture.class);
		am.load("enemy_2_idle.png", Texture.class);
		am.load("grass_1.png", Texture.class);
		am.load("grass_0.png", Texture.class);
		am.load("i18n/mushroom_speech", I18NBundle.class);
		am.load("i18n/mushroom_power_speech", I18NBundle.class);
		am.load("i18n/mushroom_invisibility_speech", I18NBundle.class);
		am.load("i18n/mushroom_speed_speech", I18NBundle.class);
		am.load("i18n/mushroom_dexterity_speech", I18NBundle.class);
		am.load("i18n/foresters_speeches_alarm", I18NBundle.class);
		am.load("i18n/foresters_speeches_fear", I18NBundle.class);
		am.load("i18n/foresters_speeches_patrol", I18NBundle.class);
		am.load("i18n/foresters_speeches_pursuit", I18NBundle.class);
		am.load("i18n/foresters_speeches_stop", I18NBundle.class);
		am.load("i18n/foresters_speeches_invisible_player", I18NBundle.class);
		am.load("i18n/grandma_speeches", I18NBundle.class);
		am.finishLoading();

		Texture overworld = am.get("overworld.png");
		Texture horizontalTree = am.get("trees_horizontal.png");
		Texture verticalTree = am.get("trees_vertical.png");
		Texture grass0Texture = am.get("grass_0.png");
		Texture grass1Texture = am.get("grass_1.png");

		regions.put("horizontal_tree_top_left", new TextureRegion(horizontalTree, 0, 0, 45, 48));
		regions.put("horizontal_tree_top_right", new TextureRegion(horizontalTree, 45, 0, 45, 48));
		regions.put("horizontal_tree_bottom_left", new TextureRegion(horizontalTree, 0, 48, 45, 48));
		regions.put("horizontal_tree_bottom_right", new TextureRegion(horizontalTree, 45, 48, 45, 48));

		regions.put("vertical_tree_top_left", new TextureRegion(verticalTree, 0, 0, 45, 42));
		regions.put("vertical_tree_top_right", new TextureRegion(verticalTree, 45, 0, 45, 42));
		regions.put("vertical_tree_bottom_left", new TextureRegion(verticalTree, 0, 42, 45, 42));
		regions.put("vertical_tree_bottom_right", new TextureRegion(verticalTree, 45, 42, 45, 42));

		regions.put(GRASS_0, new TextureRegion(grass0Texture, 0, 0, 96, 96));
		regions.put(GRASS_1, new TextureRegion(grass1Texture, 0, 0, 96, 96));
		regions.put("log_0", new TextureRegion(overworld, 16 * 3, 16 * 5, 16, 16));
		regions.put("log_1", new TextureRegion(overworld, 16 * 4, 16 * 5, 16, 16));
		regions.put("log_2", new TextureRegion(overworld, 16 * 5, 16 * 5, 16, 16));
		regions.put("stone_default", new TextureRegion(overworld, 16 * 6, 16 * 5, 16, 16));
		regions.put("stone_1", new TextureRegion(overworld, 16 * 7, 16 * 5, 16, 16));
		regions.put("stone_2", new TextureRegion(overworld, 16 * 8, 16 * 5, 16, 16));
		regions.put("stone_3", new TextureRegion(overworld, 16 * 9, 16 * 5, 16, 16));
		regions.put("stone_4", new TextureRegion(overworld, 16 * 10, 16 * 5, 16, 16));

		commonMushroomsSpeechBundle = am.get("i18n/mushroom_speech", I18NBundle.class);
		powerMushroomsSpeechBundle = am.get("i18n/mushroom_power_speech", I18NBundle.class);
		dexterityMushroomsSpeechBundle = am.get("i18n/mushroom_dexterity_speech", I18NBundle.class);
		invisibilityMushroomsSpeechBundle = am.get("i18n/mushroom_invisibility_speech", I18NBundle.class);
		speedMushroomsSpeechBundle = am.get("i18n/mushroom_speed_speech", I18NBundle.class);
		forestersSpeechesAlarmBundle = am.get("i18n/foresters_speeches_alarm", I18NBundle.class);
		forestersSpeechesPursuitBundle = am.get("i18n/foresters_speeches_pursuit", I18NBundle.class);
		forestersSpeechesFearBundle = am.get("i18n/foresters_speeches_fear", I18NBundle.class);
		forestersSpeechesPatrolBundle = am.get("i18n/foresters_speeches_patrol", I18NBundle.class);
		forestersSpeechesStopBundle = am.get("i18n/foresters_speeches_stop", I18NBundle.class);
		grandmaSpeechesBundle = am.get("i18n/grandma_speeches", I18NBundle.class);
		forestersSpeechesInvisiblePlayerBundle = am.get("i18n/foresters_speeches_invisible_player", I18NBundle.class);
	}

	public static TextureRegion getRegion(String name) {
		return regions.get(name);
	}

	public static Texture getTexture(String name) {
		return am.get(name.concat(".png"));
	}
}
