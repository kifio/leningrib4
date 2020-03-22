package kifio.leningrib.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.List;
import kifio.leningrib.levels.CommonLevel;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.Space;
import kifio.leningrib.screens.GameScreen;
import kifio.leningrib.levels.Level;

public class WorldController {

	private GameScreen gameScreen;
	private float xLimit = Gdx.graphics.getWidth() - GameScreen.tileSize;
	private float yLimit;

	public WorldController(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	public void reset(Level level, Stage stage) {
		yLimit = (gameScreen.constantsConfig.getLevelHeight() - 1) * GameScreen.tileSize;
		resetStage(level, stage);
	}

	private void resetStage(Level level, Stage stage) {
		stage.clear();

		if (level.getTutorialLabels() != null) {
			for (Label l : level.getTutorialLabels()) {
				stage.addActor(l);
			}
		}

		for (Actor mushroom : level.getMushrooms()) { stage.addActor(mushroom); }

		TreesManager treesManager = level.getTreesManager();
		for (Actor tree : treesManager.getObstacleTrees()) { stage.addActor(tree); }
		for (Actor tree : treesManager.getTopBorderNonObstaclesTrees()) { stage.addActor(tree); }
		stage.addActor(level.getPlayer());
		for (Actor tree : treesManager.getBottomBorderNonObstaclesTrees()) { stage.addActor(tree); }

		if (level.getGrandma() != null) { stage.addActor(level.getGrandma()); }

		for (int i = 0; i < level.getForesters().size; i++) {
			stage.addActor(level.getForesters().get(i));
			stage.addActor(level.getForestersSpeeches()[i]);
		}
//
//		if (level.getGrandma() != null) {
//			stage.addActor(level.getGrandma().getGrandmaLabel());
//		}

		for (Space s : level.getSpaces()) {
			stage.addActor(s);
		}
	}

	public void movePlayerTo(float x, float y, Level level) {
		level.movePlayerTo(x, y);
	}

	public void update(float delta, float cameraPositionY, Level level, Stage stage) {
		if (level.getGrandma() != null) {
			if (level.getGrandma().isReadyForDialog(level.getPlayer())) {
				level.getGrandma().startDialog();
				stage.addActor(level.getGrandma().getGrandmaLabel());
			}
		}
		addSpeechesToStage(stage, level.getMushroomsSpeeches());
		addSpeechesToStage(stage, level.getForestersSpeeches());
		updateWorld(delta, cameraPositionY, level);
	}

	private void addSpeechesToStage(Stage stage, Label[] speeches) {
		for (Label speech : speeches) {
			if (speech != null && speech.getStage() == null) {
				stage.addActor(speech);
			}
		}
	}

	private void updateWorld(float delta, float cameraPositionY, Level level) {
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
		gameScreen = null;
	}
}