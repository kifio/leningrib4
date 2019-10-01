package kifio.leningrib.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class FirstLevel extends Level {

	private Actor[] actors;

	FirstLevel(GameScreen gameScreen) {
		super(0, 0, gameScreen);
		initActors(gameScreen.constantsConfig);
		gameScreen.isFirstLevelPassed = false;
	}

	private void initActors (ConstantsConfig constantsConfig) {
		actors = new Actor[2];

		// Инициализация бабки
		actors[0] = (new Grandma(GameScreen.tileSize * 4, GameScreen.tileSize * 19));

		// Инициализация лесника
		actors[1] = (forestersManager.getForesters().get(0));
	}

	@Override protected LevelMap getLevelMap(int x, int y) {
		return gameScreen.worldMap.addLevel(x, y, FirstLevelBuilder.getFirstLevel(gameScreen.constantsConfig));
	}

	@Override protected Array<Mushroom> initMushrooms(ConstantsConfig constantsConfig, Array<?extends Actor> trees) {
		return FirstLevelBuilder.getMushrooms();
	}

	@Override protected Array<Forester> initForesters(LevelMap levelMap) {
		Array<Forester> foresters = new Array<>(1);
		foresters.add(FirstLevelBuilder.getForester());
		return foresters;
	}

	@Override public Grandma getGrandma() {
		return ((Grandma) actors[0]);
	}

	@Override public Label[] getTutorialLabels() {
		return FirstLevelBuilder.getTutorialLabels();
	}
}