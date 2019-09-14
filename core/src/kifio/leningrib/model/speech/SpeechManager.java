package kifio.leningrib.model.speech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import kifio.leningrib.model.ResourcesManager;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by kifio on 19/02/2018.
 */
public class SpeechManager {

	private BitmapFont bitmapFont;
	private GlyphLayout glyphLayout;
	Label.LabelStyle labelStyle = new Label.LabelStyle();

	private static SpeechManager speechManager;

	public static SpeechManager getInstance() {
		if (speechManager == null) {
			speechManager = new SpeechManager();
		}
		return speechManager;
	}

	private SpeechManager() {
		bitmapFont = generateFont();
		glyphLayout = new GlyphLayout();
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
			"АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя1234567890.,:;_¡!¿?\"'+-*/()[]={}";
		parameter.color = Color.WHITE;
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
		return ResourcesManager.mushroomsSpeechBundle.get(String.valueOf(random(1, 84)));
	}

	public String getForesterPatrolSpeech() {
		return ResourcesManager.forestersSpeechesPatrolBundle.get(String.valueOf(random(1, 11)));
	}

	public String getForesterPlayerNoticedSpeech() {
		return ResourcesManager.forestersSpeechesPlayerNoticesBundle.get(String.valueOf(random(1, 5)));
	}

	public String getForesterPlayerInRoomSpeech() {
		return ResourcesManager.forestersSpeechesPlayerInRoomBundle.get(String.valueOf(random(1, 11)));
	}

	public String getForesterPursuitSpeech() {
		return ResourcesManager.forestersSpeechesPursuitBundle.get(String.valueOf(random(1, 14)));
	}

	public String getForesterStopSpeech() {
		return ResourcesManager.forestersSpeechesStopBundle.get(String.valueOf(random(1, 2)));
	}

	public Label getLabel(String text, float x, float y, float targetWidth) {
		Label label = new Label(text, labelStyle);
		label.setWrap(true);
		label.setFontScale(Gdx.graphics.getDensity(), Gdx.graphics.getDensity());
		label.setWidth(targetWidth);
		label.setPosition(x, y);
		label.setAlignment(Align.center, Align.center);
		return label;
	}
}
