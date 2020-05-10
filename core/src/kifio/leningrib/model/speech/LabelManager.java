package kifio.leningrib.model.speech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.screens.GameScreen;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by kifio on 19/02/2018.
 */
public class LabelManager {

	public BitmapFont smallFont;
	public BitmapFont mediumFont;
	public BitmapFont largeFont;

	private GlyphLayout glyphLayout = new GlyphLayout();
	private Label.LabelStyle labelStyle = new Label.LabelStyle();

	private static final float SCALE = 0.9f;
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя1234567890.,:;_¡!¿?'+-*/()[]={}@";

	private static LabelManager speechManager;

	public static LabelManager getInstance() {
		if (speechManager == null) {
			speechManager = new LabelManager();
		}
		return speechManager;
	}

	private LabelManager() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/3572.ttf"));
		smallFont = generator.generateFont(getFontParameters(0.6f, 0));
		mediumFont = generator.generateFont(getFontParameters(0.8f, 0));
		largeFont = generator.generateFont(getFontParameters(1f, 1));
		labelStyle.font = smallFont;
		generator.dispose();
	}

	private FreeTypeFontGenerator.FreeTypeFontParameter getFontParameters(float scale, int shadowOffsetY) {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters = CHARACTERS;
		parameter.color = Color.WHITE;
		parameter.minFilter = Nearest;
		parameter.magFilter = Linear;
		parameter.spaceY = (int) (2 * Gdx.graphics.getDensity());
		parameter.shadowOffsetY = shadowOffsetY;
		parameter.size *= (scale * Gdx.graphics.getDensity());
		return parameter;
	}

	public float getTextWidth(String text, BitmapFont font) {
		glyphLayout.setText(font, text);
		return glyphLayout.width;
	}

	public float getTextHeight(String text, BitmapFont font) {
		glyphLayout.setText(font, text);
		return glyphLayout.height;
	}

	public String getRandomMushroomSpeech() {
		return ResourcesManager.commonMushroomsSpeechBundle.get(String.valueOf(random(1, 111)));
	}

	public String getPowerMushroomSpeech() {
		return ResourcesManager.powerMushroomsSpeechBundle.get(String.valueOf(random(1, 21)));
	}

	public String getSpeedMushroomSpeech() {
		return ResourcesManager.speedMushroomsSpeechBundle.get(String.valueOf(random(1, 15)));
	}

	public String getInvisibilityMushroomSpeech() {
		return ResourcesManager.invisibilityMushroomsSpeechBundle.get(String.valueOf(random(1, 12)));
	}

	public String getDexterityMushroomSpeech() {
		return ResourcesManager.dexterityMushroomsSpeechBundle.get(String.valueOf(random(1, 15)));
	}

	public String getForesterPatrolSpeech() {
		return ResourcesManager.forestersSpeechesPatrolBundle.get(String.valueOf(random(1, 11)));
	}

	public String getForesterInvisiblePlayerSpeech() {
		return ResourcesManager.forestersSpeechesInvisiblePlayerBundle.get(String.valueOf(random(1, 3)));
	}

	public String getForesterScaredSpeech() {
		return ResourcesManager.forestersSpeechesFearBundle.get(String.valueOf(random(1, 4)));
	}

	public String getForesterPursuitSpeech() {
		return ResourcesManager.forestersSpeechesPursuitBundle.get(String.valueOf(random(1, 24)));
	}

	public String getForesterStopSpeech() {
		return ResourcesManager.forestersSpeechesStopBundle.get(String.valueOf(random(1, 5)));
	}

	public String getForesterDrinkingSpeech() {
		return ResourcesManager.forestersSpeechesDrinking.get(String.valueOf(random(1, 10)));
	}

	public String getForesterDrunkSpeech() {
		return ResourcesManager.forestersSpeechesDrunk.get(String.valueOf(random(1, 10)));
	}

	public String getForesterRunToBottleSpeech() {
		return ResourcesManager.forestersSpeechesRunToBottle.get(String.valueOf(random(1, 6)));
	}

	public String getGrandmaSpeech(int i) {
		return ResourcesManager.grandmaSpeechesBundle.get(String.valueOf(i));
	}

	public Label getLabel(String text, float x, float y) {
		return getLabel(text, x, y, Color.WHITE);
	}

	public Label getLabel(String text, float x, float y, Color color) {
		return getLabel(text, x, y, Align.center, color);
	}

	public Label getLabel(String text, float x, float y, int align, Color color) {
		Label label = new Label(text, labelStyle);
		label.setColor(color);
		label.setWidth(GameScreen.tileSize * 2);
		label.setFontScale(SCALE, SCALE);
		label.setPosition(x, y);
		label.setWrap(true);
		label.setAlignment(align, align);
		return label;
	}
}
