package kifio.leningrib.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import kifio.leningrib.levels.Level;
import kifio.leningrib.model.TextureManager;

public class WorldRenderer {

    private Stage stage;
    private Level level;
    private TextureRegion grass;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private OrthographicCamera camera;
    private int tileSize;
    private int cameraWidth;
    private int cameraHeight;

    public WorldRenderer(Level level,
                         OrthographicCamera camera,
                         int tileSize,
                         int cameraWidth,
                         int cameraHeight) {
        this.level = level;
        this.camera = camera;
        this.tileSize = tileSize;
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        if (level.player.getY() > Gdx.graphics.getHeight() / 2) {
            camera.position.y = level.player.getY() + (tileSize / 2f);
        }

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        drawGrass();
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLUE);

//        for (int i = 0; i < cameraWidth; i++) {
//            renderer.line(tileSize * i, 0,
//                    tileSize * i, cameraHeight * tileSize);
//        }
//
//        for (int i = 0; i < cameraHeight; i++) {
//            renderer.line(0, tileSize * i,
//                    cameraWidth * tileSize, tileSize * i);
//        }

        renderer.setColor(Color.RED);

        renderer.rect(level.player.bounds.x,
                level.player.bounds.y,
                level.player.bounds.width,
                level.player.bounds.height);

        renderer.end();
    }


    private void drawGrass() {
        int dc = calcDC();  //чтобы трава рисовалась плавно при движении, добавляем ряд травы ниже камеры и выше камеры
        for (int i = 0; i < cameraWidth; i++) {
            for (int j = dc > 0 ? -1 : 0; j <= cameraHeight; j++) {
                batch.draw(grass, tileSize * i, tileSize * (j + dc), tileSize, tileSize);
            }
        }
    }

    private int calcDC() {
        float dy = camera.position.y - (float) Gdx.graphics.getHeight() / 2;
        int dc = 0;
        if (dy > 0) dc = (int) (dy / tileSize);
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