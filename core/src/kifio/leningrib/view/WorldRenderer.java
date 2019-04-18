package kifio.leningrib.view;

import kifio.leningrib.LGCGame;
import kifio.leningrib.levels.Level;
import kifio.leningrib.model.*;
import kifio.leningrib.controller.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {

    private LGCGame game;
    private WorldController worldController;
    private Stage stage;
    private ScreenViewport viewport;
    private TextureRegion grass;
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;

    public WorldRenderer(LGCGame game, WorldController worldController, OrthographicCamera camera, Level level) {
        this.game = game;
        this.worldController = worldController;
        this.camera = camera;
        loadTextures();
        initMap();
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.batch);
        resetStage(level);
    }

    private void initMap() {
        map = new TmxMapLoader().load("lvl_0.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, game.tileSize / 16);
    }

    private void loadTextures() {
        grass = new TextureRegion(TextureManager.get("grass_1"));
    }

    public void resetStage(Level level) {
        stage.clear();
        stage.addActor(worldController.player);
        for (Group tree : level.trees) stage.addActor(tree);
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        if (worldController.player.getY() > Gdx.graphics.getHeight() / 2) {
            camera.position.y = worldController.player.getY();
        }

        renderer.setView(camera);
        renderer.render();

        game.batch.setProjectionMatrix(camera.combined);

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
        game.renderer.rect(worldController.player.getX() - game.tileSize / 2f,
                worldController.player.getY() - game.tileSize / 2f,
                game.tileSize,
                game.tileSize);

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