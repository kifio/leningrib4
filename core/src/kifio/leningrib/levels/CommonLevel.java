package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

import generator.Config;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.helpers.TreesManager;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;

public class CommonLevel extends Level {

	private static final int MIN_STEP = 1;
	private static final int MAX_STEP = 4;

	public CommonLevel(Player player, LevelMap levelMap) {
		super(player, levelMap);
	}

	@Override protected Array<Mushroom> initMushrooms(Config config,
													  TreesManager treesManager,
													  int mushroomsCount) {
		Array<Mushroom> mushrooms = new Array<>();

		int levelHeight = config.getLevelHeight();
		int levelWidth = config.getLevelWidth();

		int step = Math.max(MIN_STEP, MAX_STEP - (mushroomsCount / 10));

		for (int i = 1; i < levelHeight - 1; i += step) {
			int x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2));
			int y = GameScreen.tileSize * i;

			if (!Utils.isOverlapsWithActors(treesManager.getInnerBordersTrees(), x, y)) {
				boolean hasEffect = ThreadLocalRandom.current().nextInt(256) % 8 == 0;
				mushrooms.add(new Mushroom(x, y, mushroomsCount > 0 && hasEffect));
			}
		}

//		int size = GameScreen.tileSize;
//		mushrooms.add(new Mushroom(size * 4, size * 2, Mushroom.Effect.SPEED));
//		mushrooms.add(new Mushroom(size * 5, size * 2, Mushroom.Effect.INVISIBLE));
//		mushrooms.add(new Mushroom(size * 6, size * 2, Mushroom.Effect.DEXTERITY));
//		mushrooms.add(new Mushroom(size * 7, size * 2, Mushroom.Effect.POWER));

		return mushrooms;
	}

	@Override protected Array<Forester> initForesters(LevelMap levelMap,
													  Config config,
													  Player player,
													  Rectangle[] roomsRectangles) {
		Array<Forester> gameObjects = new Array<>();

		for (Rectangle roomsRectangle : roomsRectangles) {
			if (player == null || !Utils.isInRoom(roomsRectangle,
					player.getX() / GameScreen.tileSize,
					player.getY() / GameScreen.tileSize)) {

				int left = (int) (roomsRectangle.x + 1);
				int top = (int) (roomsRectangle.y + roomsRectangle.height - 2);
				int right = (int) (roomsRectangle.width - 2);
				int bottom = (int) roomsRectangle.y;

				int originalFromY = ThreadLocalRandom.current().nextInt(bottom, top);

				boolean ltr = ThreadLocalRandom.current().nextBoolean();

				Forester f = new Forester(
						GameScreen.tileSize * (ltr ? left : right),
						GameScreen.tileSize * originalFromY,
						GameScreen.tileSize * (ltr ? right : left),
						ThreadLocalRandom.current().nextInt(1, 4),
						GameScreen.tileSize * bottom,
						GameScreen.tileSize * top,
						GameScreen.tileSize,
						GameScreen.tileSize * (config.getLevelWidth() - 2));

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