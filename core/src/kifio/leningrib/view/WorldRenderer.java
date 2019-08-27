package kifio.leningrib.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import kifio.leningrib.levels.Level;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.speech.Speech;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class WorldRenderer {

    private boolean debug = true;
    private Stage stage;
    private Level level;
    private TextureRegion grass;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private OrthographicCamera camera;
    private int cameraWidth;
    private int cameraHeight;

    private Color playerDebugColor = new Color(0f, 0f, 1f, 0.5f);
    private Color playerPathDebugColor = new Color(0f, 0f, 1f, 1f);
    private Color foresterDebugColor = new Color(1f, 0f, 0f, 0.5f);

    private static final String GAME_OVER_TEXT = "ЕБАНИ МЕНЯ ПАЛЬЦЕМ ЧТОБЫ НАЧАТЬ СНАЧАЛА";
    private static final String TEST_OVER_TEXT = "ЭТО САМАЯ ЧТО НИ НА ЕСТЬ ПОБЕДА!\nТЕПЕРЬ\n" + GAME_OVER_TEXT;

    public WorldRenderer(OrthographicCamera camera,
                         int cameraWidth,
                         int cameraHeight) {
        this.camera = camera;
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        this.grass = new TextureRegion(ResourcesManager.get("grass_0"));
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
        this.stage = new Stage(new ScreenViewport(camera), batch);
    }

    public void reset(Level level) {
        this.level = level;
        resetStage(level);
    }

    private void resetStage(Level level) {
        stage.clear();
        for (Actor mushroom : level.getMushrooms()) stage.addActor(mushroom);
        stage.addActor(level.getPlayer());
        if (level.getGrandma() != null) stage.addActor(level.getGrandma());
        for (Forester forester : level.getForesters()) stage.addActor(forester);
        for (Actor tree : level.getTrees()) stage.addActor(tree);
    }

    public void renderBlackScreen(boolean levelPassed, float gameOverTime, float gameOverAnimationTime) {
        render();

        float alpha = Math.min(gameOverTime / gameOverAnimationTime, 1);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0f, 0f, 0f, alpha);
        renderer.rect(0f, camera.position.y - Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.end();

        if (!levelPassed)
            drawGameOverText(GAME_OVER_TEXT);
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCamera();
        drawGrass();
//        drawDebug();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        drawTexts();
    }

    private void updateCamera() {
        camera.update();
        float playerY = level.getPlayer().getY();
        float bottomTreshold = Gdx.graphics.getHeight() / 2f;
        float topTreshold = level.getLevelHeight() * GameScreen.tileSize - Gdx.graphics.getHeight() / 2f;
        if (playerY < bottomTreshold) {
            camera.position.y = bottomTreshold;
        } else if (playerY > topTreshold) {
            camera.position.y = topTreshold;
        } else {
            camera.position.y = playerY;
        }
    }

    private void drawDebug() {
        if (!debug) return;
        // Включаем поддержку прозрачности
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

//        drawPlayerPath();

        // Прямоугольник, на котором находится игрок
//        drawCharacterDebug();

//        drawMushroomsBounds();

        // Прямоугольник, на котором находится лесник
//        drawForesterDebug();

//        drawExitRect();

        renderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawPlayerPath() {
        renderer.setColor(playerPathDebugColor);
        for (Vector2 vec : level.getPlayer().path) {
            renderer.rect(vec.x,
                    vec.y,
                    GameScreen.tileSize,
                    GameScreen.tileSize);
        }
    }

    private void drawMushroomsBounds() {
        renderer.setColor(playerPathDebugColor);
        for (Mushroom m: level.getMushrooms()) {
            renderer.rect(m.bounds.x,
                m.bounds.y,
                m.bounds.width,
                m.bounds.height);
        }
    }

    private void drawCharacterDebug() {
        renderer.setColor(playerDebugColor);
        Rectangle bounds = level.getPlayer().bounds;
        renderer.rect(bounds.x,
                bounds.y,
                bounds.width,
                bounds.height);
    }

    private void drawForesterDebug() {
        renderer.setColor(foresterDebugColor);
        for (Forester forester : level.getForesters()) drawForesterPath(forester);
    }

    private void drawExitRect() {
        renderer.setColor(playerDebugColor);

        renderer.rect(GameScreen.tileSize * 3f,
                GameScreen.tileSize * 24f,
                GameScreen.tileSize,
                GameScreen.tileSize);

        renderer.rect(GameScreen.tileSize * 4f,
                GameScreen.tileSize * 24f,
                GameScreen.tileSize,
                GameScreen.tileSize);
    }

    private void drawForesterPath(Forester forester) {
        for (Vector2 vec : forester.path) {
            renderer.rect(vec.x,
                    vec.y,
                    GameScreen.tileSize,
                    GameScreen.tileSize);
        }
    }

    private void drawGrass() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        int dc = calcDC();  //чтобы трава рисовалась плавно при движении, добавляем ряд травы ниже камеры и выше камеры
        for (int i = 0; i < cameraWidth; i++) {
            for (int j = dc > 0 ? -1 : 0; j <= cameraHeight; j++) {
                int x = GameScreen.tileSize * i;
                int y = GameScreen.tileSize * (j + dc);
                batch.draw(grass, x, y, GameScreen.tileSize, GameScreen.tileSize);
            }
        }
        batch.end();
    }

    private void drawTexts() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Speech speech : level.getMushroomsSpeeches())  {
            SpeechManager.getInstance().getBitmapFont().draw(batch,
                    speech.getSpeech(),
                    speech.getX(),
                    speech.getY());
            speech.increaseY(0.5f);
        }

        for (Speech speech : level.getExitsSpeeches())  {
            SpeechManager.getInstance().getBitmapFont().draw(batch,
                    speech.getSpeech(),
                    speech.getX(),
                    speech.getY());
            speech.decreaseX(0.5f);
        }

        SpeechManager.getInstance().getBitmapFont().draw(batch,
            level.getPlayer().getMushroomsCount(),
            Gdx.graphics.getWidth() - 64,
            camera.position.y + (Gdx.graphics.getHeight() / 2) - 64);

        batch.end();
    }

    private void drawMushroomCount(String mushroomsCount) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();



        batch.end();
    }

    private int calcDC() {
        float dy = camera.position.y - (float) Gdx.graphics.getHeight() / 2;
        int dc = 0;
        if (dy > 0) dc = (int) (dy / GameScreen.tileSize);
        return dc;
    }

    private void drawGameOverText(String text) {
        batch.begin();
        SpeechManager speechManager = SpeechManager.getInstance();
        float x = (Gdx.graphics.getWidth() / 2f) - (speechManager.getTextWidth(text) / 2);
        float y = camera.position.y - (speechManager.getTextHeight(text) / 2);
        speechManager.getBitmapFont().draw(batch, text, x, y);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        renderer.dispose();
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        camera = null;
    }
}