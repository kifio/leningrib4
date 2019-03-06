package kifio.leningrib.view;

import kifio.leningrib.LGCGame;
import kifio.leningrib.model.*;
import kifio.leningrib.controller.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
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
	private int cameraWidth;
	private int cameraHeight;
	private int tileSize;

	public WorldRenderer(LGCGame game, WorldController worldController) {
		this.game = game;
		this.worldController = worldController;
		loadTextures();
		initCamera();
		initStage();
	}

	private void loadTextures() {
		grass = new TextureRegion(game.getTexture("overworld.png"), 0, 0, 16, 16);
	}

	private void initCamera() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		cameraWidth = 16;
		tileSize = (width / cameraWidth) + 1;
		cameraHeight = (height / tileSize) + 1;

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
	}

	private void drawGrass() {
		for (int i = 0; i < cameraWidth; i++) {
			for (int j = 0; j < cameraHeight; j++) {
				game.batch.draw(grass, tileSize * i, tileSize * j, tileSize, tileSize);
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