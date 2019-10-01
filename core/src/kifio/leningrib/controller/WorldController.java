package kifio.leningrib.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.List;
import kifio.leningrib.levels.CommonLevel;
import kifio.leningrib.screens.GameScreen;
import kifio.leningrib.levels.Level;

public class WorldController {

	private GameScreen gameScreen;
	private Level level;
	private float xLimit = Gdx.graphics.getWidth() - GameScreen.tileSize;
	private float yLimit;

	public WorldController(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	public void reset(Level level) {
		yLimit = (gameScreen.constantsConfig.getLevelHeight() - 1) * GameScreen.tileSize;
		if (this.level != null) { this.level.dispose(); }
		this.level = level;
	}

	public void movePlayerTo(float x, float y) {
		level.movePlayerTo(x, y);
	}

	public void update(float delta, float cameraPositionY, Stage stage) {

		if (level.getGrandma() != null) {
			if (level.getGrandma().isReadyForDialog(level.getPlayer())) {
				level.getGrandma().startDialog();
				stage.addActor(level.getGrandma().getGrandmaLabel());
			}
		}

		Label[] mushroomSpeeches = level.getMushroomsSpeeches();
		for (int i = 0; i < mushroomSpeeches.length; i++) {
			Label speech = mushroomSpeeches[i];
			if (speech != null && speech.getStage() == null) {
				stage.addActor(speech);
			}
		}

		updateWorld(delta, cameraPositionY);
	}

	private void updateWorld(float delta, float cameraPositionY) {
		if (!gameScreen.isGameOver() && level.getPlayer().getY() >= yLimit) {
			gameScreen.onGoUp();
			gameScreen.onLevelPassed();
			return;
		} else if (!gameScreen.isGameOver() && level.getPlayer().getX() >= xLimit) {
			gameScreen.onGoRight();
			gameScreen.onLevelPassed();
			return;
		}

		level.update(delta, cameraPositionY);
	}

	public void dispose() {
		this.level.dispose();
		gameScreen = null;
		level = null;
	}
}