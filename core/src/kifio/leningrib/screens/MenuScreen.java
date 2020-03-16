package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import generator.Config;

import kifio.leningrib.LGCGame;
import kifio.leningrib.controller.WorldController;
import kifio.leningrib.levels.CommonLevel;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import kifio.leningrib.levels.Level;
import kifio.leningrib.levels.LevelFabric;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.view.WorldRenderer;
import model.WorldMap;

public class MenuScreen extends InputAdapter implements Screen {

    private LGCGame game;
    private OrthographicCamera camera;

    public MenuScreen(LGCGame game) {
        Gdx.input.setInputProcessor(this);

        initCamera();

        float x = Gdx.graphics.getWidth() / 2f;
        float y = Gdx.graphics.getHeight() / 2f;

        this.game = game;
        SpriteBatch batch = new SpriteBatch();
        
    }

    // Инициализирует камеру ортгональную карте
    private void initCamera() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        // create the camera and the batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    @Override
    public void render(float delta) {
  
    }

    @Override
    public void dispose() {
        game = null;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        return super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (game != null) {
            game.showGameScreen();
            return true;
        } else {
            return super.touchUp(x, y, pointer, button);
        }
    }
}
