package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

import kifio.leningrib.controller.WorldController;
import kifio.leningrib.levels.FirstLevel;
import kifio.leningrib.levels.Level;
import kifio.leningrib.view.WorldRenderer;

public class GameScreen extends InputAdapter implements Screen {

    private WorldRenderer worldRenderer;
    private WorldController worldController;
    private OrthographicCamera camera;

    public static int tileSize;
    public static int cameraWidth;
    public static int cameraHeight;
    public static boolean gameOver;

    public GameScreen() {
        Gdx.input.setInputProcessor(this);
        initScreenSize();
        initCamera();
        Level level = getNextLevel();
        this.worldController = new WorldController(this, level);
        this.worldRenderer = new WorldRenderer(level, camera, cameraWidth, cameraHeight);
    }

    // Инициализирует камеру ортгональную карте
    private void initCamera() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        // create the camera and the batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
    }

    // Инициализирует размер экрана.
    // Экран разбит на квадарты, здесь задается количество квадратов по ширине,
    // в зависимости от этого рассчитывается количество кадратов по высоте
    private void initScreenSize() {
        cameraWidth = 6;
        tileSize = (Gdx.graphics.getWidth() / cameraWidth) + 1;
        cameraHeight = (Gdx.graphics.getHeight() / tileSize) + 1;
    }

    private Level getNextLevel() {
        return new FirstLevel();
    }

    public void onLevelPassed() {
        Level level = getNextLevel();
        this.worldController.setLevel(level);
        this.worldRenderer.resetStage(level);
    }

    @Override
    public void render(float delta) {
        if (gameOver) {
//            worldRenderer.renderBlackScreen(delta);
            worldRenderer.render();
        } else {
            worldController.update(delta);
            worldRenderer.render();
        }
    }

    @Override
    public void dispose() {
        if (worldRenderer != null) {
            worldRenderer.dispose();
            worldRenderer = null;
        }
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
        if (!gameOver) {
            worldController.movePlayerTo(
                    camera.position.x - (Gdx.graphics.getWidth() / 2f) + x,
                    camera.position.y - (Gdx.graphics.getHeight() / 2f) + (Gdx.graphics.getHeight() - y));
        }
        return true;
    }
}
