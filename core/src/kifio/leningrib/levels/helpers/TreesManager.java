package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private static final String TREE = "tree_";
    private static final String HORIZONTAL = "horizontal_";
    private static final String VERTICAL = "vertical_";
    private static final String STONE = "stone_";
    private static final String TEXTURE_NAME_TEMPLATE = "%s%s%s";

    private static final String TOP_LEFT = "top_left";
    private static final String TOP_RIGHT = "top_right";
    private static final String BOTTOM_LEFT = "bottom_left";
    private static final String BOTTOM_RIGHT = "bottom_right";

    private static final String DEFAULT = "default";

    private Array<Actor> trees = new Array<>();
    private Array<Actor> outerTrees = new Array<>();

    public void buildTrees(LevelMap levelMap, ConstantsConfig constantsConfig) {
        for (Segment s : levelMap.getSegments()) {
            Actor tree = getActorFromCell(s.getValue(),
                    s.getX() * GameScreen.tileSize, s.getY() * GameScreen.tileSize, constantsConfig);
            if (tree != null) {
                trees.add(tree);
                if (s.getX() == 0
                        || s.getY() == 0
                        || s.getX() == (constantsConfig.getLevelWidth() - 1)
                        || s.getY() == (constantsConfig.getLevelHeight() - 1)) {
                    outerTrees.add(tree);
                }
            }
        }
    }

    private static Actor getActorFromCell(int value, int x, int y, ConstantsConfig constantsConfig) {
        if (value == constantsConfig.getTreeTopLeft()) {
            return getObstacle(TREE, TOP_LEFT, x, y, constantsConfig);
        } else if (value == constantsConfig.getTreeTopRight()) {
            return getObstacle(TREE, TOP_RIGHT, x, y, constantsConfig);
        } else if (value == constantsConfig.getTreeBottomLeft()) {
            return getObstacle(TREE, BOTTOM_LEFT, x, y, constantsConfig);
        } else if (value == constantsConfig.getTreeBottomRight()) {
            return getObstacle(TREE, BOTTOM_RIGHT, x, y, constantsConfig);
        } else if (value == constantsConfig.getStone()) {
            return getObstacle(STONE, DEFAULT, x, y, constantsConfig);
        } else {
            return null;
        }
    }

    private static Actor getObstacle(String name, String value, int x, int y, ConstantsConfig constantsConfig) {
        TextureRegion region;
        if (name.equals(STONE)) {
            region = ResourcesManager.getRegion(String.format(Locale.getDefault(), TEXTURE_NAME_TEMPLATE, "", name, value));
            return new TreePart(region, x, y,
                    GameScreen.tileSize, GameScreen.tileSize);
        } else if (y > 0 && y < Gdx.graphics.getHeight() - GameScreen.tileSize) { // Вертикальные деревья
            if (x == 0 || x == Gdx.graphics.getWidth() - GameScreen.tileSize) {
                region = ResourcesManager.getRegion(String.format(Locale.getDefault(), TEXTURE_NAME_TEMPLATE, VERTICAL, name, value));
                return new TreePart(region, x, y,
                        GameScreen.tileSize, GameScreen.tileSize);
            } else {
                region = ResourcesManager.getRegion(String.format(Locale.getDefault(), TEXTURE_NAME_TEMPLATE, HORIZONTAL, name, value));
                int w = region.getRegionWidth();
                return new TreePart(region, x, y, GameScreen.tileSize, GameScreen.tileSize);
            }
        } else {
            region = ResourcesManager.getRegion(String.format(Locale.getDefault(), TEXTURE_NAME_TEMPLATE, HORIZONTAL, name, value));
            int w = region.getRegionWidth();
            return new TreePart(region, x, y, GameScreen.tileSize, GameScreen.tileSize);
        }
    }

    public Array<? extends Actor> getTrees() {
        return trees;
    }

    public Array<? extends Actor> getOuterBordersTrees() {
        return outerTrees;
    }
}
