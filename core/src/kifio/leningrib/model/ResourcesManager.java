package kifio.leningrib.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
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
	public static I18NBundle forestersSpeechesPlayerNoticesBundle;
	public static I18NBundle forestersSpeechesPlayerInRoomBundle;
	public static I18NBundle forestersSpeechesPursuitBundle;
	public static I18NBundle forestersSpeechesStopBundle;
	public static I18NBundle grandmaSpeechesBundle;

	private static AssetManager am;

	public static void init() {

		am = new AssetManager();
		am.load("overworld.png", Texture.class);
		am.load("forest_tiles.png", Texture.class);
		am.load("power_mushroom.png", Texture.class);
		am.load("bottle.png", Texture.class);
		am.load("player_run.png", Texture.class);
		am.load("player_idle.png", Texture.class);
		am.load("enemy_1_run.png", Texture.class);
		am.load("enemy_1_idle.png", Texture.class);
		am.load("enemy_2_run.png", Texture.class);
		am.load("enemy_2_idle.png", Texture.class);
		am.load("medieval_rts_spritesheet.png", Texture.class);
		am.load("medieval_grass_0.png", Texture.class);
		am.load("medieval_grass_1.png", Texture.class);
		am.load("autumn_grass.png", Texture.class);
		am.load("pine.png", Texture.class);
		am.load("leafed.png", Texture.class);
		am.load("i18n/mushroom_speech", I18NBundle.class);
		am.load("i18n/mushroom_power_speech", I18NBundle.class);
		am.load("i18n/mushroom_invisibility_speech", I18NBundle.class);
		am.load("i18n/mushroom_speed_speech", I18NBundle.class);
		am.load("i18n/mushroom_dexterity_speech", I18NBundle.class);
		am.load("i18n/foresters_speeches_noticing", I18NBundle.class);
		am.load("i18n/foresters_speeches_patrol", I18NBundle.class);
		am.load("i18n/foresters_speeches_player_in_room", I18NBundle.class);
		am.load("i18n/foresters_speeches_pursuit", I18NBundle.class);
		am.load("i18n/foresters_speeches_stop", I18NBundle.class);
		am.load("i18n/grandma_speeches", I18NBundle.class);
		am.load("mushroom_0.png", Texture.class);
		am.load("mushroom_1.png", Texture.class);
		am.load("mushroom_2.png", Texture.class);
		am.load("mushroom_3.png", Texture.class);
		am.load("autumn_mushroom.png", Texture.class);
		am.finishLoading();

		Texture overworld = am.get("overworld.png");
		Texture forestTiles = am.get("forest_tiles.png");

		Texture autumnGrass = am.get("autumn_grass.png");
		Texture grass0Texture = am.get("medieval_grass_0.png");
		Texture grass1Texture = am.get("medieval_grass_1.png");
		Texture pineTexture = am.get("pine.png");
		Texture leafedTexture = am.get("leafed.png");
		Texture mushroom0 = am.get("mushroom_0.png");
		Texture mushroom1 = am.get("mushroom_1.png");
		Texture mushroom2 = am.get("mushroom_2.png");
		Texture mushroom3 = am.get("mushroom_3.png");
		Texture mushroom4 = am.get("autumn_mushroom.png");

		regions.put("tree_0", new TextureRegion(pineTexture, 0, 0, 64, 64));
		regions.put("tree_1", new TextureRegion(pineTexture, 0, 64, 64, 64));
		regions.put("tree_2", new TextureRegion(pineTexture, 64, 0, 64, 64));
		regions.put("tree_3", new TextureRegion(pineTexture, 64, 64, 64, 64));
		regions.put(GRASS_0, new TextureRegion(grass0Texture, 0, 0, 128, 128));
		regions.put(GRASS_1, new TextureRegion(grass1Texture, 0, 0, 128, 128));
		regions.put("log_0", new TextureRegion(overworld, 16 * 3, 16 * 5, 16, 16));
		regions.put("log_1", new TextureRegion(overworld, 16 * 4, 16 * 5, 16, 16));
		regions.put("log_2", new TextureRegion(overworld, 16 * 5, 16 * 5, 16, 16));
		regions.put("stone_0", new TextureRegion(overworld, 16 * 6, 16 * 5, 16, 16));
		regions.put("stone_1", new TextureRegion(overworld, 16 * 7, 16 * 5, 16, 16));
		regions.put("stone_2", new TextureRegion(overworld, 16 * 8, 16 * 5, 16, 16));
		regions.put("stone_3", new TextureRegion(overworld, 16 * 9, 16 * 5, 16, 16));
		regions.put("stone_4", new TextureRegion(overworld, 16 * 10, 16 * 5, 16, 16));
		regions.put("mushroom_0", new TextureRegion(mushroom0, 0, 0, 64, 64));
		regions.put("mushroom_1", new TextureRegion(mushroom1, 0, 0, 64, 64));
		regions.put("mushroom_2", new TextureRegion(mushroom2, 0, 0, 64, 64));
		regions.put("mushroom_3", new TextureRegion(mushroom3, 0, 0, 64, 64));
		regions.put("mushroom_4", new TextureRegion(mushroom4, 0, 0, 152, 152));
		regions.put("mushroom_1_dexterity", new TextureRegion(mushroom3, 0, 0, 64, 64));

		commonMushroomsSpeechBundle = am.get("i18n/mushroom_speech", I18NBundle.class);
		powerMushroomsSpeechBundle = am.get("i18n/mushroom_power_speech", I18NBundle.class);
		dexterityMushroomsSpeechBundle = am.get("i18n/mushroom_dexterity_speech", I18NBundle.class);
		invisibilityMushroomsSpeechBundle = am.get("i18n/mushroom_invisibility_speech", I18NBundle.class);
		speedMushroomsSpeechBundle = am.get("i18n/mushroom_speed_speech", I18NBundle.class);
		forestersSpeechesPlayerNoticesBundle = am.get("i18n/foresters_speeches_noticing", I18NBundle.class);
		forestersSpeechesPatrolBundle = am.get("i18n/foresters_speeches_patrol", I18NBundle.class);
		forestersSpeechesPlayerInRoomBundle = am.get("i18n/foresters_speeches_player_in_room", I18NBundle.class);
		forestersSpeechesPursuitBundle = am.get("i18n/foresters_speeches_pursuit", I18NBundle.class);
		forestersSpeechesStopBundle = am.get("i18n/foresters_speeches_stop", I18NBundle.class);
		grandmaSpeechesBundle = am.get("i18n/grandma_speeches", I18NBundle.class);
	}

	public static TextureRegion getRegion(String name) {
		return regions.get(name);
	}

	public static Texture getTexture(String name) {
		return am.get(name.concat(".png"));
	}

	private static final int CLEAR = Color.rgba8888(Color.CLEAR);

	// Color can be mixed or overlayed
	public static TextureRegion getRegionWithTint(String name, int effect, boolean mix) {
		TextureRegion region = regions.get(name);
		region.getTexture().getTextureData().prepare();

		int width = region.getRegionWidth();
		int height = region.getRegionHeight();

		Pixmap regionPixmap = region.getTexture().getTextureData().consumePixmap();
		Pixmap resultPixmap = new Pixmap(width, height, Format.RGBA8888);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j <= height; j++) {
				if (regionPixmap.getPixel(i, j) != CLEAR) {
					if (mix) {
						resultPixmap.drawPixel(i, j, regionPixmap.getPixel(i, j) & effect);
					} else {
						resultPixmap.drawPixel(i, j, regionPixmap.getPixel(i, j));
						resultPixmap.drawPixel(i, j, effect);
					}
				}
			}
		}

		TextureRegion resultRegion = new TextureRegion(new Texture(resultPixmap));

		regionPixmap.dispose();
		resultPixmap.dispose();
		return resultRegion;
	}
}
