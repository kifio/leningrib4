package kifio.leningrib.view;

import kifio.leningrib.LGCGame;
import kifio.leningrib.model.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {
    
	private LGCGame game;
	private World world;
    private Texture overworld;
    private TextureRegion grass;

	private OrthographicCamera camera;
	private int cameraWidth;
	private int cameraHeight;
	private int tileSize;
	private float elapsedTime = 0;

	public WorldRenderer(LGCGame game, World world) {
		this.game = game;
		this.world = world;
		loadTextures();
		initCamera();
	}

	private void loadTextures() {
		this.overworld = game.getTexture("overworld.png");
		this.grass = new TextureRegion(overworld, 0, 0, 16, 16);
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
		
		Gdx.app.log("kifio", String.format("cameraWidth: %d; cameraHeight: %d", cameraWidth, cameraHeight));
	}

	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		elapsedTime += Gdx.graphics.getDeltaTime();
		
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		drawGrass();
		drawPlayer();
		game.batch.end();
	}

	private void drawPlayer() {
		TextureRegion playerTexture = world.player.getTextureRegion(elapsedTime);
		game.batch.draw(playerTexture, world.player.x, world.player.y, playerTexture.getRegionWidth() * 4, playerTexture.getRegionHeight() * 4);
	}

	private void drawGrass() {
		for (int i = 0; i < cameraWidth; i++) {
			for (int j = 0; j < cameraHeight; j++) {
				game.batch.draw(grass, tileSize * i, tileSize * j, tileSize, tileSize);
			}
		}
	}
}