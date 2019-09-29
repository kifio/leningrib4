package kifio.leningrib.levels;

import kifio.leningrib.screens.GameScreen;

public class LevelFabric {

	public static Level getNextLevel(int x, int y, GameScreen gameScreen) {
		if (!gameScreen.isFirstLevelPassed) {
			return new FirstLevel(gameScreen);
		} else {
			return new CommonLevel(x, y, gameScreen);
		}
	}

}
