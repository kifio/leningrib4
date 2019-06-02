package kifio.leningrib.model.speech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.I18NBundle;

import kifio.leningrib.model.ResourcesManager;

/**
 * Created by kifio on 19/02/2018.
 */
class SpeechManager {

    private BitmapFont bitmapFont;
    private I18NBundle speechBundle;
    private GlyphLayout glyphLayout;

    SpeechManager() {
        bitmapFont = generateFont();
        speechBundle = ResourcesManager.getMushroomSpeechBundle();
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

    float getTextWidth(String text) {
        glyphLayout.setText(bitmapFont, text);
        return glyphLayout.width;
    }

    float getTextHeight(String text) {
        glyphLayout.setText(bitmapFont, text);
        return glyphLayout.height;
    }

    void scale(float scale) {
        bitmapFont.getData().setScale(scale);
    }
}
