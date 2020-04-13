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
import static com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapNearestNearest;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static com.badlogic.gdx.math.MathUtils.floor;
import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by kifio on 19/02/2018.
 */
public class SpeechManager {

	private static BitmapFont bitmapFont;
	private GlyphLayout glyphLayout = new GlyphLayout();
	private Label.LabelStyle labelStyle = new Label.LabelStyle();

	private static SpeechManager speechManager;

	public static SpeechManager getInstance() {
		if (speechManager == null) {
			speechManager = new SpeechManager();
		}
		return speechManager;
	}

	private SpeechManager() {
		bitmapFont = generateFont();
		bitmapFont.getData().setScale(Gdx.graphics.getDensity());
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = Color.WHITE;
	}

	private BitmapFont generateFont() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/3572.ttf"));
		BitmapFont font = generator.generateFont(getFontParameter());
		generator.dispose();
		return font;
	}

	private FreeTypeFontGenerator.FreeTypeFontParameter getFontParameter() {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters =
			"АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя1234567890.,:;_¡!¿?\"'+-*/()[]={}@";
		parameter.color = Color.WHITE;
		parameter.minFilter = Nearest;
		parameter.magFilter = Nearest;
		return parameter;
	}

	public float getTextWidth(String text) {
		glyphLayout.setText(bitmapFont, text);
		return glyphLayout.width;
	}

	public float getTextHeight(String text) {
		glyphLayout.setText(bitmapFont, text);
		return glyphLayout.height;
	}

	public BitmapFont getBitmapFont() {
		return bitmapFont;
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

	public String getGrandmaSpeech(int i) {
		return ResourcesManager.grandmaSpeechesBundle.get(String.valueOf(i));
	}

	public Label getLabel(String text, float x, float y, float w) {
		return getLabel(text, x, y, w, Color.WHITE);
	}

	public Label getLabel(String text, float x, float y, float width, Color color) {
		return getLabel(text, x, y, 0.8f, width, color);
	}

	public Label getLabel(String text,
						  float x,
						  float y,
						  float scale,
						  float width,
						  Color color) {
		Label label = new Label(text, labelStyle);
		label.setWrap(true);
		label.setColor(color);
		label.setFontScale(Gdx.graphics.getDensity() * scale, Gdx.graphics.getDensity() * scale);
		label.setWidth(width);
		label.setPosition(x, y);
		label.setAlignment(Align.center, Align.bottom);
		return label;
	}

	public float getLabelWidth(String[] words) {
		float maxLabelWidth = 0;
		for (String word : words) {
			float w = getTextWidth(word);
			if (maxLabelWidth < w) {
				maxLabelWidth = w;
			}
		}
		return maxLabelWidth;
	}
}
