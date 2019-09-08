package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import generator.ConstantsConfig;

import kifio.leningrib.LGCGame;
import kifio.leningrib.controller.WorldController;
import kifio.leningrib.levels.Level;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.view.WorldRenderer;
import model.WorldMap;

public class GameScreen extends InputAdapter implements Screen {

    private static final float GAME_OVER_ANIMATION_TIME = 1f;
    public static final int SPEECH_SEED = 5120;

    private LGCGame game;
    private WorldRenderer worldRenderer;
    private WorldController worldController;
    private OrthographicCamera camera;

    public static int tileSize;
    private static int cameraWidth;
    private static int cameraHeight;
    public boolean gameOver;
    private boolean win;
    private int nextLevelX = 0;
    private int nextLevelY = 0;
    private Stage stage;
    private SpriteBatch batch;
    public Label gameOverLabel;

    private float gameOverTime;
    public boolean isFirstLaunch = false;

    public WorldMap worldMap;
    public Player player;

    public ConstantsConfig constantsConfig = new ConstantsConfig(
            7,
            30,
            2,
            0,
            1,
            2,
            3,
            4
    );

    public GameScreen(LGCGame game) {
        Gdx.input.setInputProcessor(this);
        initScreenSize();
        initCamera();

        float x = Gdx.graphics.getWidth() / 2f;
        float y = Gdx.graphics.getHeight() / 2f;

        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(camera), batch);
        this.gameOverLabel = SpeechManager.getInstance().getLabel("", x, y, Gdx.graphics.getWidth());
        this.worldController = new WorldController(this);
        this.worldRenderer = new WorldRenderer(camera, cameraWidth, cameraHeight, stage, batch);
        this.worldMap = new WorldMap();
        this.player = new Player(0f, 2 * GameScreen.tileSize);
        setLevel(getNextLevel(nextLevelX, nextLevelY));
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
        cameraWidth = constantsConfig.getLevelWidth();
        tileSize = (Gdx.graphics.getWidth() / cameraWidth) + 1;
        cameraHeight = (Gdx.graphics.getHeight() / tileSize) + 1;
    }

    private Level getNextLevel(int x, int y) {
        return new Level(x, y, this);
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
        if (isGameOver() && gameOverTime < 1f) {
            gameOverTime += delta;
            worldController.update(delta, camera.position.y, stage);
            worldRenderer.renderBlackScreen(win, gameOverTime, GAME_OVER_ANIMATION_TIME);
        } else if (win && gameOverTime >= GAME_OVER_ANIMATION_TIME) {

            if (player.getY() >= (constantsConfig.getLevelHeight() - 1) * GameScreen.tileSize) {
                player.setY(0);
            } else if (player.getX() >= (constantsConfig.getLevelWidth() - 1) * GameScreen.tileSize) {
                player.setX(0);
            }

            setLevel(getNextLevel(nextLevelX, nextLevelY));

            gameOverTime = 0f;
            win = false;
        } else if (!gameOver) {
            worldController.update(delta, camera.position.y, stage);
            worldRenderer.render();
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("kifio", "GameScreen.dispose");
        if (worldRenderer != null) {
            worldRenderer.dispose();
            worldRenderer = null;
        }


        if (worldController != null) {
            worldController.dispose();
            worldController = null;
    }   }

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
        if (!isGameOver()) {
            worldController.movePlayerTo(
                    camera.position.x - (Gdx.graphics.getWidth() / 2f) + x,
                    camera.position.y - (Gdx.graphics.getHeight() / 2f) + (Gdx.graphics.getHeight() - y));
        } else if (gameOverTime > GAME_OVER_ANIMATION_TIME) {
            game.showGameScreen();
            dispose();
        }
        return true;
    }

    public void onLevelPassed() {
        win = true;
    }

    public void onGoRight() {
        nextLevelX++;
    }

    public void onGoUp() {
        nextLevelY++;
    }

    public boolean isGameOver() {
        return gameOver || win;
    }
}
