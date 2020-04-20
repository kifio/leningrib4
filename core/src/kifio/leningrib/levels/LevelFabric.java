package kifio.leningrib.levels;

import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class LevelFabric {

	public static Level getNextLevel(int x, int y, GameScreen gameScreen, LevelMap levelMap) {
		if (!gameScreen.isFirstLevelPassed) {
			return new FirstLevel(gameScreen, levelMap);
		} else {
			return new CommonLevel(x, y, gameScreen, levelMap);
		}
	}

}
