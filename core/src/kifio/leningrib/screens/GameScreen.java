package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

import kifio.leningrib.controller.WorldController;
import kifio.leningrib.levels.Level;
import kifio.leningrib.view.WorldRenderer;
import model.WorldMap;

public class GameScreen extends InputAdapter implements Screen {

    private static final float GAME_OVER_ANIMATION_TIME = 1f;

    private WorldRenderer worldRenderer;
    private WorldController worldController;
    private OrthographicCamera camera;
    private WorldMap worldMap;

    public static int tileSize;
    public static int cameraWidth;
    public static int cameraHeight;
    public static boolean gameOver;
    public static boolean win;

    private float gameOverTime;
    private float gameTime;

    public GameScreen() {
        Gdx.input.setInputProcessor(this);
        initScreenSize();
        initCamera();
        this.worldController = new WorldController(this);
        this.worldRenderer = new WorldRenderer(camera, cameraWidth, cameraHeight);
        this.worldMap = new WorldMap();
        setLevel(getNextLevel(0, 0));
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

    private Level getNextLevel(int x, int y) {
        return new Level(0, 0, worldMap);
    }

    public void setLevel(Level level) {
        this.worldController.reset(level);
        this.worldRenderer.reset(level);
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    @Override
    public void render(float delta) {
        if (gameOver) {
            gameOverTime += delta;
            gameTime = 0f;
            worldController.update(delta, gameOverTime);
            worldRenderer.renderBlackScreen(gameOverTime, GAME_OVER_ANIMATION_TIME);
        } else {
            gameTime += delta;
            worldController.update(delta, gameTime);
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
        } else if (gameOverTime > GAME_OVER_ANIMATION_TIME) {
            setLevel(getNextLevel(0, 0));
            gameOverTime = 0f;
            gameTime = 0f;
            gameOver = false;
            win = false;
        }
        return true;
    }

    public void onLevelPassed() {
        gameOver = true;
        win = true;
    }
}
