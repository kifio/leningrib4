package kifio.leningrib.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import generator.Config;
import kifio.leningrib.levels.Level;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.actors.Space;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class WorldRenderer {

	private boolean debug = true;

	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private int levelWidth;
	private int levelHeight;
	private int grassCount = 0;

	private Color playerDebugColor = new Color(0f, 0f, 1f, 0.5f);
	private Color playerPathDebugColor = new Color(0f, 0f, 1f, 1f);
	private Color foresterDebugColor = new Color(1f, 0f, 0f, 0.5f);
	private TextureRegion grass = ResourcesManager.getRegion(ResourcesManager.GRASS_0);
	private TextureRegion hudBottle = ResourcesManager.getRegion(ResourcesManager.HUD_BOTTLE);
	private TextureRegion hudPause = ResourcesManager.getRegion(ResourcesManager.HUD_PAUSE);
	private TextureRegion hudBackground = ResourcesManager.getRegion(ResourcesManager.HUD_BACKGROUND);

	private static final String GAME_OVER_TEXT = "ЯДРЕНА КОЧЕРЫЖКА\nТЫ СОБРАЛ %s ГРИБОВ";

	public WorldRenderer(OrthographicCamera camera,
						 int levelWidth,
						 int levelHeight,
						 int cameraHeight,
						 SpriteBatch batch) {
		this.camera = camera;
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		this.batch = batch;
		this.renderer = new ShapeRenderer();
		this.grassCount = levelWidth * (cameraHeight + 2);
	}

	public void renderBlackScreen(boolean levelPassed,
								  float gameOverTime,
								  float gameOverAnimationTime,
								  Level level,
								  Stage stage) {
		render(level, stage);

		float alpha = Math.min(gameOverTime / gameOverAnimationTime, 1);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		renderer.setColor(0f, 0f, 0f, alpha);
		renderer.rect(0f,
			camera.position.y - Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		renderer.end();

		if (!levelPassed) { drawGameOverText(level.getPlayer().getMushroomsCount()); }
	}

	private void drawGameOverText(int mushroomsCount) {
		batch.begin();
		String text = String.format(Locale.getDefault(), GAME_OVER_TEXT, mushroomsCount);
		SpeechManager speechManager = SpeechManager.getInstance();
		float x = (Gdx.graphics.getWidth() / 2f) - (speechManager.getTextWidth(text) / 2);
		float y = camera.position.y - (speechManager.getTextHeight(text) / 2);
		speechManager.getBitmapFont().draw(batch, text, x, y);
		batch.end();
	}

	public void render(Level level, Stage stage) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateCamera(level.getPlayer());
		drawGrass();
		drawDebug(level);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		drawHUD();
	}

	private void drawHUD() {

		float w = GameScreen.tileSize * levelWidth;
		float h = camera.position.y + Gdx.graphics.getHeight() / 2f;

		float pauseX = w - 1.1f * GameScreen.tileSize;
		float pauseY = h - 1.1f * GameScreen.tileSize;

		Gdx.app.log("kifio", String.format("tileSize: %d", GameScreen.tileSize));

		float buttonsWidth = GameScreen.tileSize * 1f;
		float buttonsHeight = GameScreen.tileSize * 1f;

		float bottleX = w - 1.1f * GameScreen.tileSize;
		float bottleY = h - 2.2f * GameScreen.tileSize;

		float itemsWidth = GameScreen.tileSize * 1f;
		float itemsHeight = GameScreen.tileSize * 1f;

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(hudBackground,
				pauseX, pauseY,
				buttonsWidth, buttonsHeight);

		batch.draw(hudBackground,
				bottleX, bottleY,
				itemsWidth, itemsHeight);

		batch.draw(
				hudPause,
				pauseX,
				pauseY,
				buttonsWidth,
				buttonsHeight
		);

		batch.draw(
				hudBottle,
				bottleX,
				bottleY,
				buttonsWidth,
				buttonsHeight
		);
		batch.end();
	}

	private void updateCamera(Player player) {
		camera.update();
		float bottomThreshold = Gdx.graphics.getHeight() / 2f;
		float topThreshold = levelHeight * GameScreen.tileSize - Gdx.graphics.getHeight() / 2f;
		if (player.getY() < bottomThreshold) {
			camera.position.y = bottomThreshold;
		} else camera.position.y = Math.min(player.getY(), topThreshold);
	}

	private void drawDebug(Level level) {
		if (!debug) { return; }
		// Включаем поддержку прозрачности
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Filled);

        // drawPlayerPath();
//        drawGrid();
//		  drawCharacterDebug();
//		  drawGrandmaDebug();
//        drawMushroomsBounds();
		// drawForesterDebug();

		Gdx.gl.glDisable(GL20.GL_BLEND);
		renderer.end();

	}

	private void drawGrid() {
		for (int i = 0; i < Gdx.graphics.getWidth(); i+=GameScreen.tileSize) {
			renderer.line(i, 0f, i, Gdx.graphics.getHeight());
		}

		for (int i = 0; i < Gdx.graphics.getHeight(); i+=GameScreen.tileSize) {
			renderer.line(0, i, Gdx.graphics.getWidth(), i);
		}
	}

	private void drawPlayerPath(Player player) {
		renderer.setColor(playerPathDebugColor);
		for (Vector2 vec : player.getPath()) {
			renderer.rect(vec.x, vec.y, GameScreen.tileSize, GameScreen.tileSize);
		}
	}

	private void drawMushroomsBounds(Array<Mushroom> mushrooms) {
		renderer.setColor(playerPathDebugColor);
		for (Mushroom m : mushrooms) {
			if (m != null) {
				renderer.rect(m.bounds.x, m.bounds.y, m.bounds.width, m.bounds.height);
			}
		}
	}

	private void drawCharacterDebug(Player player) {
		renderer.setColor(playerDebugColor);
		Rectangle bounds = player.bounds;
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private void drawGrandmaDebug(Grandma grandma) {
		if (grandma == null) { return; }
		renderer.setColor(playerDebugColor);
		Rectangle bounds = grandma.bounds;
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private void drawForesterDebug(Array<Forester> foresters) {
		for (Forester forester : foresters) { drawForesterPath(forester); }
//		for (Forester forester : foresters) { drawForesterArea(forester); }
	}

	private void drawExitRect() {
		renderer.setColor(playerDebugColor);

		renderer.rect(GameScreen.tileSize * 3f, GameScreen.tileSize * 24f, GameScreen.tileSize, GameScreen.tileSize);

		renderer.rect(GameScreen.tileSize * 4f, GameScreen.tileSize * 24f, GameScreen.tileSize, GameScreen.tileSize);
	}

	private void drawForesterPath(Forester forester) {
		renderer.setColor(foresterDebugColor);
		for (Vector2 vec : forester.getPath()) {
			renderer.rect(vec.x, vec.y, GameScreen.tileSize, GameScreen.tileSize);
		}
	}

	private void drawForesterArea(Forester forester) {

		Rectangle r = forester.getPursueArea();
		renderer.setColor(foresterDebugColor);
		renderer.rect(r.x, r.y, r.width, r.height);

		r = forester.getNoticeArea();
		renderer.setColor(playerDebugColor);
		renderer.rect(r.x, r.y, r.width, r.height);
	}

	private void drawGrass() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (int i = 0; i < grassCount; i++) {
			int x = GameScreen.tileSize * (i % levelWidth);
			int y = GameScreen.tileSize * (i / levelWidth) + (int) (camera.position.y - (Gdx.graphics.getHeight() / 2 + GameScreen.tileSize));
			batch.draw(grass, x, y, GameScreen.tileSize, GameScreen.tileSize);
		}
		batch.end();
	}

	public void dispose() {
		batch.dispose();
		renderer.dispose();
		camera = null;
	}
}
