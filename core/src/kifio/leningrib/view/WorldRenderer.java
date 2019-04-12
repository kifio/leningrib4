package kifio.leningrib.view;

import kifio.leningrib.LGCGame;
import kifio.leningrib.model.*;
import kifio.leningrib.controller.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {

    private LGCGame game;
    private WorldController worldController;
    private Stage stage;
    private ScreenViewport viewport;
    private TextureRegion grass;

    private OrthographicCamera camera;

    public WorldRenderer(LGCGame game, WorldController worldController) {
        this.game = game;
        this.worldController = worldController;
        loadTextures();
        initCamera();
        initStage();
    }

    private void loadTextures() {
        grass = new TextureRegion(TextureManager.get("grass"));
    }

    private void initCamera() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        // create the camera and the game.batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
    }

    private void initStage() {
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.batch);
        stage.addActor(worldController.player);

        for (Group tree : worldController.trees) {
            stage.addActor(tree);
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        drawGrass();
        game.batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        game.renderer.setProjectionMatrix(camera.combined);
        game.renderer.begin(ShapeRenderer.ShapeType.Line);
        game.renderer.setColor(Color.BLUE);

        for (int i = 0; i < game.cameraWidth; i++) {
            game.renderer.line(game.tileSize * i, 0,
                    game.tileSize * i, game.cameraHeight * game.tileSize);
        }

        for (int i = 0; i < game.cameraHeight; i++) {
            game.renderer.line(0, game.tileSize * i,
                    game.cameraWidth * game.tileSize, game.tileSize * i);
        }

        game.renderer.setColor(Color.RED);

        game.renderer.rect(worldController.player.bounds.x,
                worldController.player.bounds.y,
                worldController.player.bounds.width,
                worldController.player.bounds.height);

        game.renderer.setColor(Color.YELLOW);
        game.renderer.rect(worldController.player.tile.x,
                worldController.player.tile.y,
                worldController.player.tile.width,
                worldController.player.tile.height);

        game.renderer.end();
    }

    private void drawGrass() {
        for (int i = 0; i < game.cameraWidth; i++) {
            for (int j = 0; j < game.cameraHeight; j++) {
                game.batch.draw(grass, game.tileSize * i, game.tileSize * j, game.tileSize, game.tileSize);
            }
        }
    }

    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}