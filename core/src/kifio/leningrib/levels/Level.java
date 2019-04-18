package kifio.leningrib.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.HashSet;
import java.util.Set;

import kifio.leningrib.model.TextureManager;
import kifio.leningrib.model.TreePart;

public abstract class Level {

    protected int tileSize;

    // Деревья
    public Set<Group> trees = new HashSet<>();

    // Множестов зон, в которые нельзя попасть
    public Set<Rectangle> unreachableBounds = new HashSet<>();

    public void init() {
        initTrees();
        initMushrooms();
    }

    // TODO: Сделать парсинг карты и инициализацию групп актеров.
    private void initMushrooms() {
//        Group group = new Group();
//        group.addActor(new MovableActor(tileSize * 3, tileSize * 3, -1,
//                tileSize, "power_mushroom.txt"));
//        trees.add(group);
    }

    private void initTrees() {
        // TODO: Разные группы будут отвечать за разные эффекты деревьев
//        Group group = new Group();
//        addTree(group, tileSize, 3 * tileSize);
//        addTree(group, tileSize * 3, 5 * tileSize);
//        addTree(group, tileSize * 4, 2 * tileSize);
//
//        addTopLeftSegment(group, tileSize, 0);
//        addTopRightSegment(group, 2 * tileSize, 0);
//
//        addBottomRightSegment(group, 0, 4 * tileSize);
//        addTopRightSegment(group, 0, 5 * tileSize);
//
//        trees.add(group);
    }

    private void addTree(Group group, int x, int y) {
        addTopLeftSegment(group, x, y);
        addBottomLeftSegment(group, x, y - tileSize);
        addTopRightSegment(group, x + tileSize, y);
        addBottomRightSegment(group, x + tileSize, y - tileSize);
    }

    private void addTopLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_0"), x, y, tileSize, tileSize));
    }

    private void addTopRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart( TextureManager.get("tree_3"), x, y, tileSize, tileSize));
    }

    private void addBottomLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_1"), x, y, tileSize, tileSize));
        unreachableBounds.add(new Rectangle(x, y, tileSize, tileSize));
    }

    private void addBottomRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_2"), x, y, tileSize, tileSize));
        unreachableBounds.add(new Rectangle(x, y, tileSize, tileSize));
    }

    public boolean isUnreachableZone(Vector2 point) {
        for (Rectangle r : unreachableBounds)
            if (r.contains(point)) return true;
        return false;
    }
}
