package kifio.leningrib.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.awt.Color;
import java.util.List;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class FirstLevel extends Level {

	List<Label> tutorialLabels = FirstLevelBuilder.getTutorialLabels();

	FirstLevel(GameScreen gameScreen) {
		super(0, 0, gameScreen);
		gameScreen.isFirstLevelPassed = false;
	}

	@Override protected Array<Actor> getActors() {
		if (actors == null) {
			Array<Actor> actors = new Array<>(2);

			// Инициализация бабки
			actors.add(new Grandma(GameScreen.tileSize * 4, GameScreen.tileSize * 19));

			// Инициализация лесника
			actors.add(forestersManager.getForesters().get(0));
		}
		return actors;
	}

	@Override protected LevelMap getLevelMap(GameScreen gameScreen, int x, int y) {
		return gameScreen.worldMap.addLevel(0, 0, FirstLevelBuilder.getFirstLevel(gameScreen.constantsConfig));
	}

	@Override protected Array<Mushroom> initMushrooms(ConstantsConfig constantsConfig, Array<Actor> trees) {
		return FirstLevelBuilder.getMushrooms();
	}

	@Override protected Array<Forester> initForesters(LevelMap levelMap, GameScreen gameScreen) {
		Array<Forester> foresters = new Array<>(1);
		foresters.add(FirstLevelBuilder.getForester());
		return foresters;
	}
}