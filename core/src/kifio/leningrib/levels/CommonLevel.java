package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

import generator.Config;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class CommonLevel extends Level {

	private static final int MIN_STEP = 1;
	private static final int MAX_STEP = 4;

	CommonLevel(int x, int y, GameScreen gameScreen) {
		super(x, y, gameScreen);
	}

	@Override protected LevelMap getLevelMap(int x, int y) {
		return gameScreen.worldMap.addLevel(x, y, gameScreen.getConstantsConfig());
	}

	@Override protected Array<Mushroom> initMushrooms(Config config, TreesManager treesManager) {
		Array<Mushroom> mushrooms = new Array<>();

		int levelHeight = config.getLevelHeight();
		int levelWidth = config.getLevelWidth();

		int mushroomsCount = getPlayer().getMushroomsCount();
		int step = Math.max(MIN_STEP, MAX_STEP - (mushroomsCount / 10));

		for (int i = 1; i < levelHeight - 1; i += step) {
			int x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2));
			int y = GameScreen.tileSize * i;

			if (!Utils.isOverlapsWithActors(treesManager.getInnerBordersTrees(), x, y)) {
				boolean hasEffect = ThreadLocalRandom.current().nextInt(256) % 8 == 0;
				mushrooms.add(new Mushroom(x, y,
						mushroomsCount > 0 && hasEffect));
			}
		}

		return mushrooms;
	}

	@Override protected Array<Forester> initForesters(LevelMap levelMap) {
		Array<Forester> gameObjects = new Array<>();
		Rectangle[] roomsRectangles = getRoomsRectangles(levelMap, gameScreen.getConstantsConfig());
		for (Rectangle roomsRectangle : roomsRectangles) {
			if (!Utils.isInRoom(roomsRectangle,
					gameScreen.player.getX() / GameScreen.tileSize, gameScreen.player.getY() / GameScreen.tileSize)) {

				int left = (int) (roomsRectangle.x + 1);
				int top = (int) (roomsRectangle.y + roomsRectangle.height - 1);
				int right = (int) (roomsRectangle.width - 2);
				int bottom = (int) roomsRectangle.y + 1;

				int originalFromY = ThreadLocalRandom.current().nextInt(bottom, top);
				int originalToY = ThreadLocalRandom.current().nextInt(bottom, top);

				boolean ltr = ThreadLocalRandom.current().nextBoolean();

				Forester f = new Forester(
						GameScreen.tileSize * (ltr ? left : right),
						GameScreen.tileSize * originalFromY,
						GameScreen.tileSize * (ltr ? right : left),
						GameScreen.tileSize * originalToY,
						ThreadLocalRandom.current().nextInt(1, 4),
						GameScreen.tileSize * bottom,
						GameScreen.tileSize * top,
						GameScreen.tileSize, GameScreen.tileSize * 7,
						roomsRectangle);

				gameObjects.add(f);
			}
		}
		return gameObjects;
	}

	@Override public Grandma getGrandma() {
		return null;
	}

	@Override public Label[] getTutorialLabels() {
		return null;
	}

}