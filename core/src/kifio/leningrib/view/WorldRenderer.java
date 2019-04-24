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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import kifio.leningrib.levels.Level;
import kifio.leningrib.model.TextureManager;
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

    public WorldRenderer(Level level,
                         OrthographicCamera camera,
                         int cameraWidth,
                         int cameraHeight) {
        this.level = level;
        this.camera = camera;
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        loadTextures();
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        ScreenViewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, batch);
        resetStage(level);
    }

    private void loadTextures() {
        grass = new TextureRegion(TextureManager.get("grass_0"));
    }

    public void resetStage(Level level) {
        stage.clear();
        stage.addActor(level.player);
        stage.addActor(level.forester);
        for (Group tree : level.trees) stage.addActor(tree);
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCamera();
        drawGrass();
        drawDebug();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();


//        for (int i = 0; i < cameraWidth; i++) {
//            renderer.line(tileSize * i, 0,
//                    tileSize * i, cameraHeight * tileSize);
//        }
//
//        for (int i = 0; i < cameraHeight; i++) {
//            renderer.line(0, tileSize * i,
//                    cameraWidth * tileSize, tileSize * i);
//        }
    }

    private void updateCamera() {
        camera.update();
        float playerY = level.player.getY();
        if (playerY > Gdx.graphics.getHeight() / 2 && playerY < (level.mapHeight * GameScreen.tileSize - Gdx.graphics.getHeight() / 2))
            camera.position.y = playerY + (GameScreen.tileSize / 2f);
    }

    private void drawDebug() {
        if (!debug) return;
        // Включаем поддержку прозрачности
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        drawPlayerPath();

        // Прямоугольник, на котором находится игрок
        drawCharacterDebug();

        // Прямоугольник, на котором находится лесник
        drawForesterDebug();

        renderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawPlayerPath() {
        renderer.setColor(playerPathDebugColor);
        for (Vector2 vec: level.path) {
            renderer.rect(vec.x,
                    vec.y,
                    GameScreen.tileSize,
                    GameScreen.tileSize);
        }

    }

    private void drawCharacterDebug() {
        renderer.setColor(playerDebugColor);
        Rectangle bounds = level.player.bounds;
        renderer.rect(bounds.x,
                bounds.y,
                bounds.width,
                bounds.height);
    }

    private void drawForesterDebug() {
        renderer.setColor(foresterDebugColor);
        Rectangle rectangle = level.forester.getPatrolRectangle();
        renderer.rect(rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height);
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

    private int calcDC() {
        float dy = camera.position.y - (float) Gdx.graphics.getHeight() / 2;
        int dc = 0;
        if (dy > 0) dc = (int) (dy / GameScreen.tileSize);
        return dc;
    }

    public void dispose() {
        batch.dispose();
        renderer.dispose();
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}