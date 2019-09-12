package kifio.leningrib.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import generator.ConstantsConfig;
import java.util.Locale;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.model.TreePart;
import kifio.leningrib.screens.GameScreen;
import model.LevelMap;
import model.Segment;

public class TreesManager {

	public Array<Actor> trees = new Array<>();

	void buildTrees(LevelMap levelMap, ConstantsConfig constantsConfig) {
		for (Segment s : levelMap.getSegments()) {
			Actor tree = getActorFromCell(s.getValue(),
				s.getX() * GameScreen.tileSize, s.getY() * GameScreen.tileSize, constantsConfig);
			if (tree != null) { trees.add(tree); }
		}
	}

	private static Actor getActorFromCell(int value, int x, int y, ConstantsConfig constantsConfig) {
		if (value == constantsConfig.getTreeTopLeft()) {
			return getObstacle("tree", 0, x, y);
		} else if (value == constantsConfig.getTreeTopRight()) {
			return getObstacle("tree", 2, x, y);
		} else if (value == constantsConfig.getTreeBottomLeft()) {
			return getObstacle("tree", 1, x, y);
		} else if (value == constantsConfig.getTreeBottomRight()) {
			return getObstacle("tree", 3, x, y);
		}
		if (value == constantsConfig.getStone()) {
			return getObstacle("stone", 0, x, y);
		} else {
			return null;
		}
	}

	private static Actor getObstacle(String name, int value, int x, int y) {
		return new TreePart(ResourcesManager.get(String.format(Locale.getDefault(), "%s_%d", name, value)), x, y,
			GameScreen.tileSize, GameScreen.tileSize);
	}
}
