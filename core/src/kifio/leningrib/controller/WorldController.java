package kifio.leningrib.controller;

import kifio.leningrib.model.*;

import java.util.*;

import kifio.leningrib.LGCGame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.math.Rectangle;

public class WorldController {

    // Параметр должен зависеть от разрешения экрана
//	private static final int scale = 8;

    private LGCGame game;

    public MovableActor player;
    public Set<Group> trees = new HashSet<>();

    // Множестов зон, в которые нельзя попасть
    public Set<Rectangle> unreachableBounds = new HashSet<>();

    public WorldController(LGCGame game) {
        this.game = game;
        this.player = new MovableActor(game.width / game.tileSize, game.tileSize, "player.txt");
        initTrees();
        initMushrooms();
    }

    private void initMushrooms() {
        Group group = new Group();
        group.addActor(new MovableActor(game.tileSize * 3, game.tileSize * 3, -1,
                game.tileSize, "power_mushroom.txt"));
        trees.add(group);
    }

    private void initTrees() {
        // TODO: Разные группы будут отвечать за разные эффекты деревьев
        Group group = new Group();
        addTree(group, game.tileSize, 3 * game.tileSize);
        addTree(group, game.tileSize * 3, 5 * game.tileSize);
        addTree(group, game.tileSize * 4, 2 * game.tileSize);

        addTopLeftSegment(group, game.tileSize, 0);
        addTopRightSegment(group, 2 * game.tileSize, 0);

        addBottomRightSegment(group, 0, 4 * game.tileSize);
        addTopRightSegment(group, 0, 5 * game.tileSize);

        trees.add(group);
    }

    // TODO: Выыделить методы для добавления сегментов дерева
    private void addTree(Group group, int x, int y) {
        addTopLeftSegment(group, x, y);
        addBottomLeftSegment(group, x, y - game.tileSize);
        addTopRightSegment(group, x + game.tileSize, y);
        addBottomRightSegment(group, x + game.tileSize, y - game.tileSize);
    }

    private void addTopLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_0"), x, y, game.tileSize, game.tileSize));
    }

    private void addTopRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart( TextureManager.get("tree_3"), x, y, game.tileSize, game.tileSize));
    }

    private void addBottomLeftSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_1"), x, y, game.tileSize, game.tileSize));
        unreachableBounds.add(new Rectangle(x, y, game.tileSize, game.tileSize));
    }

    private void addBottomRightSegment(Group group, int x, int y) {
        group.addActor(new TreePart(TextureManager.get("tree_2"), x, y, game.tileSize, game.tileSize));
        unreachableBounds.add(new Rectangle(x, y, game.tileSize, game.tileSize));
    }

    public void movePlayerTo(float x, float y) {
        if (player.checkIsStayed(x, y)) return;
        if (isUnreachableZone(new Vector2(x, y))) return;
        if (player.checkMoveLeft(x, y)) player.moveLeft();
        else if (player.checkMoveRight(x, y)) player.moveRight();
        else if (player.checkMoveUp(x, y)) player.moveUp();
        else if (player.checkMoveDown(x, y)) player.moveDown();
    }

    private boolean isUnreachableZone(Vector2 point) {
        for (Rectangle r : unreachableBounds)
            if (r.contains(point)) return true;
        return false;
    }

    public void update() {
        setPlayerRect();
    }

    public void setPlayerRect() {
        player.tile.setX((float) (player.getTileX(game.tileSize) * game.tileSize));
        player.tile.setY((float) (player.getTileY(game.tileSize) * game.tileSize));
    }
}