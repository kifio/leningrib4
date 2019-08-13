package kifio.leningrib.model.speech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import kifio.leningrib.model.ResourcesManager;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by kifio on 19/02/2018.
 */
public class SpeechManager {

    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

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
    }

    private BitmapFont generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dejavu.ttf"));
        BitmapFont font = generator.generateFont(getFontParameter());
        generator.dispose();
        return font;
    }

    private FreeTypeFontGenerator.FreeTypeFontParameter getFontParameter() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюя1234567890.,:;_¡!¿?\"'+-*/()[]={}";
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

    public String getRandomMushroomSpeech(float scale) {
        bitmapFont.getData().setScale(scale);
        String speechId = String.valueOf(random(1, 71));
        return ResourcesManager.getMushroomSpeechBundle().get(speechId);
    }

    public String getRandomExitSpeech(float scale) {
        bitmapFont.getData().setScale(scale);
        String speechId = String.valueOf(random(1, 5));
        return ResourcesManager.getExitSpeechBundle().get(speechId);
    }
}
