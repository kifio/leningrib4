package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import generator.Config;
import kifio.leningrib.model.actors.Space;
import kifio.leningrib.model.actors.game.Player;

public class SpaceManager {

	private Array<Space> spaces = new Array<>();

	public void buildSpaces(Player player,
		Config config,
		Array<? extends Actor> trees,
		Array<? extends Actor> mushrooms,
		Rectangle[] rooms) {

//		if (player.getMushroomsCount() >= 0) {
//			int levelHeight = config.getLevelHeight();
//			int levelWidth = config.LEVEL_WIDTH;
//
//			for (int i = 1; i < levelHeight; i++) {
//				int x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2));
//				int y = GameScreen.tileSize * i;
//				if (isNotBlocker(rooms, x / GameScreen.tileSize, y / GameScreen.tileSize) &&
//					!Utils.isOverlapsWith(player.getX(), player.getY(), x, y) &&
//					!Utils.isOverlapsWithActors(trees, x, y) &&
//					!Utils.isOverlapsWithActors(mushrooms, x, y)) {
//
//					if (ThreadLocalRandom.current().nextInt(16) % 4 == 0) {
//						spaces.add(new Space(x, y));
//					}
//				}
//			}
//		}
	}

	private boolean isNotBlocker(Rectangle[] rooms, int x, int y) {
		for (Rectangle room : rooms) {

			int left = 2;
			int top = (int) (room.y + (room.height - 2));
			int right = (int) room.width - 2;
			int bottom = (int) room.y + 1;

			if (x >= left && x <= right && y >= bottom && y <= top) {
				return true;
			}
		}
		return false;
	}

	public Array<Space> getSpaces() {
		return spaces;
	}
}
