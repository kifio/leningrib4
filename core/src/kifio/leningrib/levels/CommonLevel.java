package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.Utils;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Grandma;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Room;

public class CommonLevel extends Level {

	CommonLevel(int x, int y, GameScreen gameScreen) {
		super(x, y, gameScreen);
	}

	@Override protected LevelMap getLevelMap(int x, int y) {
		return gameScreen.worldMap.addLevel(x, y, gameScreen.constantsConfig);
	}

	@Override protected Array<Mushroom> initMushrooms(ConstantsConfig config, Array<? extends Actor> trees) {
		Array<Mushroom> mushrooms = new Array<>();

		int levelHeight = config.getLevelHeight();
		int levelWidth = config.getLevelWidth();

		for (int i = 1; i < levelHeight; i++) {
			int x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2));
			int y = GameScreen.tileSize * i;
			if (!Utils.isOverlapsWithActors(trees, x, y)) {
				mushrooms.add(new Mushroom(x, y));
			}
		}

		return mushrooms;

	}

	@Override protected Array<Forester> initForesters(LevelMap levelMap) {
		Array<Forester> gameObjects = new Array<>();
		Rectangle[] roomsRectangles = getRoomsRectangles(levelMap, gameScreen.constantsConfig);
		for (int i = 0; i < roomsRectangles.length; i++) {
			if (!Utils.isInRoom(roomsRectangles[i],
				gameScreen.player.getX() / GameScreen.tileSize, gameScreen.player.getY() / GameScreen.tileSize)) {

				int left = (int) (roomsRectangles[i].x + 1);
				int top = (int) (roomsRectangles[i].y + roomsRectangles[i].height);
				int right = (int) (roomsRectangles[i].width - 2);
				int bottom = (int) roomsRectangles[i].y;

				int originalFromY = ThreadLocalRandom.current().nextInt(bottom, top);
				int originalToY = ThreadLocalRandom.current().nextInt(bottom, top);

				boolean ltr = ThreadLocalRandom.current().nextBoolean();

				Forester f = new Forester(
					GameScreen.tileSize * (ltr ? left : right),
					GameScreen.tileSize * originalFromY,
					GameScreen.tileSize * (ltr ? right : left), GameScreen.tileSize * originalToY, 1, bottom, top);

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