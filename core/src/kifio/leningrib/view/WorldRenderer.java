package kifio.leningrib.view;

import com.badlogic.gdx.Gdx;
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

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import generator.Config;
import kifio.leningrib.levels.Level;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.Space;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class WorldRenderer {

	private boolean debug = true;
	private Stage stage;
	private Level level;
	private boolean[] grass;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private Config constantsConfig;
	private int cameraWidth;
	private int cameraHeight;

	private Color demoGrass = Color.valueOf("#8f6f0b");
	private Color playerDebugColor = new Color(0f, 0f, 1f, 0.5f);
	private Color playerPathDebugColor = new Color(0f, 0f, 1f, 1f);
	private Color foresterDebugColor = new Color(1f, 0f, 0f, 0.5f);

	private static final String GAME_OVER_TEXT = "ЯДРЕНА КОЧЕРЫЖКА\nТЫ СОБРАЛ %s ГРИБОВ";

	public WorldRenderer(OrthographicCamera camera, int cameraWidth, int cameraHeight, Stage stage,
		SpriteBatch batch, Config constantsConfig) {
		this.camera = camera;
		this.cameraWidth = cameraWidth;
		this.cameraHeight = cameraHeight;
		this.stage = stage;
		this.batch = batch;
		this.constantsConfig = constantsConfig;
		this.renderer = new ShapeRenderer();
	}

	public void reset(Level level) {
		this.level = level;
		resetStage(level);
	}

	private void resetStage(Level level) {
		stage.clear();

		for (Actor tree : level.getTrees()) { stage.addActor(tree); }

		if (level.getTutorialLabels() != null) {
			for (Label l : level.getTutorialLabels()) {
				stage.addActor(l);
			}
		}

		for (Actor mushroom : level.getMushrooms()) { stage.addActor(mushroom); }
		stage.addActor(level.getPlayer());
		if (level.getGrandma() != null) { stage.addActor(level.getGrandma()); }

		for (int i = 0; i < level.getForesters().size; i++) {
			stage.addActor(level.getForesters().get(i));
			stage.addActor(level.getForestersSpeeches()[i]);
		}

		if (level.getGrandma() != null) {
			stage.addActor(level.getGrandma().getGrandmaLabel());
		}

		for (Space s : level.getSpaces()) {
			stage.addActor(s);
		}

		this.grass = new boolean[constantsConfig.getLevelHeight() * constantsConfig.getLevelWidth()];
		for (int i = 0; i <grass.length; i++) {
			grass[i] = ThreadLocalRandom.current().nextBoolean();
		}
	}

	public void renderBlackScreen(boolean levelPassed, float gameOverTime, float gameOverAnimationTime) {
		render();

		float alpha = Math.min(gameOverTime / gameOverAnimationTime, 1);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		renderer.setColor(0f, 0f, 0f, alpha);
		renderer.rect(0f,
			camera.position.y - Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		renderer.end();

		if (!levelPassed) { drawGameOverText(); }
	}

	private void drawGameOverText() {
		batch.begin();
		String text = String.format(Locale.getDefault(), GAME_OVER_TEXT, level.getPlayer().getMushroomsCount());
		SpeechManager speechManager = SpeechManager.getInstance();
		float x = (Gdx.graphics.getWidth() / 2f) - (speechManager.getTextWidth(text) / 2);
		float y = camera.position.y - (speechManager.getTextHeight(text) / 2);
		speechManager.getBitmapFont().draw(batch, text, x, y);
		batch.end();
	}

	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateCamera();
		drawGrass();
		// drawDebug();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	private void updateCamera() {
		camera.update();
		float playerY = level.getPlayer().getY();
		float bottomThreshold = Gdx.graphics.getHeight() / 2f;
		float topThreshold = constantsConfig.getLevelHeight() * GameScreen.tileSize - Gdx.graphics.getHeight() / 2f;
		if (playerY < bottomThreshold) {
			camera.position.y = bottomThreshold;
		} else if (playerY > topThreshold) {
			camera.position.y = topThreshold;
		} else {
			camera.position.y = playerY;
		}
	}

	private void drawDebug() {
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
        drawMushroomsBounds();
		// drawForesterDebug();

		Gdx.gl.glDisable(GL20.GL_BLEND);
		renderer.end();

	}

//	private void drawGrid() {
//		for (int i = 0; i < Gdx.graphics.getWidth(); i+=GameScreen.tileSize) {
//			renderer.line(i, 0f, i, Gdx.graphics.getHeight());
//		}
//
//		for (int i = 0; i < Gdx.graphics.getHeight(); i+=GameScreen.tileSize) {
//			renderer.line(0, i, Gdx.graphics.getWidth(), i);
//		}
//	}

	private void drawPlayerPath() {
		renderer.setColor(playerPathDebugColor);
		for (Vector2 vec : level.getPlayer().getPath()) {
			renderer.rect(vec.x, vec.y, GameScreen.tileSize, GameScreen.tileSize);
		}
	}

	private void drawMushroomsBounds() {
		renderer.setColor(playerPathDebugColor);
		for (Mushroom m : level.getMushrooms()) {
			renderer.rect(m.bounds.x, m.bounds.y, m.bounds.width, m.bounds.height);
		}
	}

	private void drawCharacterDebug() {
		renderer.setColor(playerDebugColor);
		Rectangle bounds = level.getPlayer().bounds;
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private void drawGrandmaDebug() {
		if (level.getGrandma() == null) { return; }
		renderer.setColor(playerDebugColor);
		Rectangle bounds = level.getGrandma().bounds;
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private void drawForesterDebug() {
		for (Forester forester : level.getForesters()) { drawForesterPath(forester); }
//		for (Forester forester : level.getForesters()) { drawForesterArea(forester); }
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
		// TODO: Оптимизировать, не рисовать то, что пределами экрана или под деревьями
		// TODO: Не шароебиться по карте в поисках текстуры
		for (int i = 0; i < grass.length; i++) {
			int x = GameScreen.tileSize * (i % cameraWidth);
			int y = GameScreen.tileSize * (i / cameraWidth);
			batch.draw(
				ResourcesManager.getRegion(grass[i] ? ResourcesManager.GRASS_0 : ResourcesManager.GRASS_0),
				x, y, GameScreen.tileSize, GameScreen.tileSize);
		}
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
