package kifio.leningrib.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Mushroom;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;
import model.Exit;
import model.LevelMap;
import model.Room;
import model.Segment;

// Chad first level config generator.
public class FirstLevelBuilder {

	static LevelMap getFirstLevel(ConstantsConfig constantsConfig) {

		Set<Segment> segmentList = new HashSet<>();
		List<Exit> exits = new ArrayList<>(3);

		int firstExit = 11;
		int secondExit = 20;

		// Добавляем нижнюю границу
		segmentList.add(new Segment(0, 0,
			constantsConfig.getTreeTopRight()));
		for (int i = 3; i < constantsConfig.getLevelWidth(); i++) {
			segmentList.add(new Segment(i, 0,
				i % 2 == 0 ? constantsConfig.getTreeTopRight() : constantsConfig.getTreeTopLeft()));
		}

		// Добавляем леву/ границу
		for (int i = 1; i < constantsConfig.getLevelHeight() - 1; i++) {
			segmentList.add(new Segment(0, i,
				i % 2 != 0 ? constantsConfig.getTreeBottomRight() : constantsConfig.getTreeTopRight()));
		}

		// Добавляем правую границу с выходами
		for (int i = 1; i < firstExit; i++) {
			segmentList.add(new Segment(
				constantsConfig.getLevelWidth() - 1, i,
				i % 2 != 0 ? constantsConfig.getTreeBottomLeft() : constantsConfig.getTreeTopLeft()));
		}

		for (int i = firstExit + 1; i < secondExit; i++) {
			segmentList.add(new Segment(
				constantsConfig.getLevelWidth() - 1, i,
				i % 2 == 0 ? constantsConfig.getTreeBottomLeft() : constantsConfig.getTreeTopLeft()));
		}

		for (int i = secondExit + 1; i < constantsConfig.getLevelHeight() - 1; i++) {
			segmentList.add(new Segment(
				constantsConfig.getLevelWidth() - 1, i,
				i % 2 != 0 ? constantsConfig.getTreeBottomLeft() : constantsConfig.getTreeTopLeft()));
		}

		exits.add(new Exit(constantsConfig.getLevelWidth() - 1, firstExit, true));
		exits.add(new Exit(constantsConfig.getLevelWidth() - 1, secondExit, true));

		// Добавляем верхнюю границу
		segmentList.add(new Segment(0, constantsConfig.getLevelHeight() - 1, constantsConfig.getTreeBottomRight()));

		for (int i = 2; i < constantsConfig.getLevelWidth(); i++) {
			segmentList.add(new Segment(i,
				constantsConfig.getLevelHeight() - 1,
				i % 2 != 0 ? constantsConfig.getTreeBottomRight() : constantsConfig.getTreeBottomLeft()));
		}

		exits.add(new Exit(1, constantsConfig.getLevelHeight() - 1, true));

		// Добавляем комнаты
		LevelMap levelMap = new LevelMap(segmentList, exits, constantsConfig);

		List<Room> rooms = new ArrayList<>(4);

		rooms.add(new Room(1, 8));
		rooms.add(new Room(9, 7));
		rooms.add(new Room(16, 7));
		rooms.add(new Room(23, 6));

		for (int i = 0; i < 3; i++) {
			Room r = rooms.get(i);
			segmentList.addAll(getTree(1, r.getY() + r.getHeight() - 2, constantsConfig));
			segmentList.addAll(getTree(4, r.getY() + r.getHeight() - 2, constantsConfig));
		}

		levelMap.setRooms(rooms);

		return levelMap;
	}

	private static List<Segment> getTree(int x, int y, ConstantsConfig constantsConfig) {
		List<Segment> arr = new ArrayList<>(4);
		arr.add(new Segment(x, y, constantsConfig.getTreeBottomLeft()));
		arr.add(new Segment(x + 1, y, constantsConfig.getTreeBottomRight()));
		arr.add(new Segment(x, y + 1, constantsConfig.getTreeTopLeft()));
		arr.add(new Segment(x + 1, y + 1, constantsConfig.getTreeTopRight()));
		return arr;
	}

	static Array<Mushroom> getMushrooms() {
		Array<Mushroom> mushrooms = new Array<>();
		mushrooms.add(new Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 7));
		mushrooms.add(new Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 14));
//		mushrooms.add(new Mushroom(GameScreen.tileSize * 1, GameScreen.tileSize * 2, random));
//		mushrooms.add(new Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 1, random));
		return mushrooms;
	}

	// FIXME: scaredArea should not be null!
	static Forester getForester() {
		return new Forester(GameScreen.tileSize, GameScreen.tileSize * 27,
			GameScreen.tileSize * 6, GameScreen.tileSize * 27, 1,
			GameScreen.tileSize * 23, GameScreen.tileSize * 29,
				GameScreen.tileSize, GameScreen.tileSize * 7, null);
	}

	private static float labelWidth =  Gdx.graphics.getWidth() - 2 * GameScreen.tileSize;

	static Label[] getTutorialLabels() {

		Label[] list = new Label[2];

		list[0] = (SpeechManager.getInstance().getLabel("Нажимай на экран, чтобы перемещать персонажа",
			(Gdx.graphics.getWidth() - labelWidth) / 2,GameScreen.tileSize * 4, GameScreen.tileSize * 4));

		list[1] = (SpeechManager.getInstance().getLabel("Ты всегда можешь покинуть уровень через выход справа или сверху",
			(Gdx.graphics.getWidth() - labelWidth) / 2 + GameScreen.tileSize,
			GameScreen.tileSize * 12 + GameScreen.tileSize / 2, GameScreen.tileSize * 4));

		return list;
	}
}
