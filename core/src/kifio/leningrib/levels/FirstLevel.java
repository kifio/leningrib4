package kifio.leningrib.levels;

import generator.ConstantsConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Exit;
import model.LevelMap;
import model.Room;
import model.Segment;

public class FirstLevel {

	static LevelMap getFirstLevel(ConstantsConfig constantsConfig) {

		Set<Segment> segmentList = new HashSet<>();
		List<Exit> exits = new ArrayList<>(3);

		int firstExit = 11;
		int secondExit = 20;

		// Добавляем нижнюю границу
		for (int i = 0; i < constantsConfig.getLevelWidth(); i++) {
			segmentList.add(new Segment(i, 0,
				i % 2 == 0 ? constantsConfig.getTreeTopRight() : constantsConfig.getTreeTopLeft()));
		}

		// Добавляем леву/ границу
		for (int i = 3; i < constantsConfig.getLevelHeight() - 1; i++) {
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

		rooms.add(new Room(1, 7));
		rooms.add(new Room(7, 7));
		rooms.add(new Room(14, 6));
		rooms.add(new Room(20, 8));

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
}
