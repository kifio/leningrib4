package kifio.leningrib.controller;

import kifio.leningrib.model.*;
import java.util.*;
import com.badlogic.gdx.Gdx;
import kifio.leningrib.LGCGame;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class WorldController {

	// Параметр должен зависеть от разрешения экрана
	private static final int SCALE = 4;

	private LGCGame game;

	public Player player = new Player(SCALE);
	public Set<Group> trees = new HashSet<>();
	public Set<Rectangle> treesBounds = new HashSet<>();

	private TextureRegion[] tree = new TextureRegion[4];

	public WorldController(LGCGame game) {
		this.game = game;
		initTrees();
	}

	private void initTrees() {
		Texture overworld = game.getTexture("overworld.png");
		tree[0] = new TextureRegion(overworld, 80, 256, 16, 16);
		tree[1] = new TextureRegion(overworld, 80, 272, 16, 16);
		tree[2] = new TextureRegion(overworld, 96, 272, 16, 16);
		tree[3] = new TextureRegion(overworld, 96, 256, 16, 16);

		addTree(200, 200);
	}

	private void addTree(int x, int y) {
		Group group = new Group();
		group.addActor(new TreePart(tree[0], x, y, SCALE));
		group.addActor(new TreePart(tree[1], x, y  - 16 * SCALE, SCALE));
		group.addActor(new TreePart(tree[2], x  + 16 * SCALE, y  - 16 * SCALE, SCALE));
		group.addActor(new TreePart(tree[3], x  + 16 * SCALE, y, SCALE));
		treesBounds.add(new Rectangle(x, y  - 16 * SCALE, 2 * 16 * SCALE, 2 * 16 * SCALE));
		trees.add(group);
	}

	public void movePlayerTo(float x, float y) {
		player.moveTo(x, y);
    }

    public void update() {
    	for (Rectangle bounds : treesBounds) {
			if (bounds.overlaps(player.bounds)) {
				player.stopMoving();
			}
		}
    }

    // Если игрок пересекся с деревом, возвращаем его назад, чтобы не блокировать движение
    private void moveBack() {

    }
}