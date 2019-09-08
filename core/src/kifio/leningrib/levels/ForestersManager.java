package kifio.leningrib.levels;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.Utils;
import kifio.leningrib.levels.ExitsManager.ExitWrapper;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class ForestersManager extends ObjectsManager<Forester> {

	private GameScreen gameScreen;
	private ForestGraph forestGraph;
	private Rectangle result = new Rectangle();
	private static float caughtArea = 0.5f * GameScreen.tileSize * GameScreen.tileSize;

	ForestersManager(Random random, GameScreen gameScreen, ForestGraph forestGraph) {
		this.random = random;
		this.gameScreen = gameScreen;
		this.forestGraph = forestGraph;
		gameObjects = new ArrayList<>();
	}

	void initForester(int levelX, int levelY, Rectangle[] roomsRectangles, List<ExitWrapper> exits, Random random) {

		for (Rectangle rectangle : roomsRectangles) {
			if (!Utils.isInRoom(rectangle,
				gameScreen.player.getX() / GameScreen.tileSize,
				gameScreen.player.getY() / GameScreen.tileSize)) {

				int left = (int) (rectangle.x + 1);
				int top = (int) (rectangle.y + rectangle.height);
				int right = (int) (rectangle.width - 2);
				int bottom = (int) rectangle.y;

				int originalFromY = ThreadLocalRandom.current().nextInt(bottom, top);
				int originalToY = ThreadLocalRandom.current().nextInt(bottom, top);

				gameObjects.add(new Forester(
					GameScreen.tileSize * left,
					GameScreen.tileSize * originalFromY,
					GameScreen.tileSize * right, GameScreen.tileSize * originalToY,
					1,
					forestGraph,
					bottom, top));
				return;
			}
		}
	}

	void initDebugForester(Forester forester) {
		gameObjects.add(forester);
	}

	public List<Forester> getForesters() {
		return gameObjects;
	}

	void updateForesters(float delta) {
		for (Forester forester : gameObjects) {
			result.set(0f, 0f, 0f, 0f);
			if (isPlayerCaught(forester.bounds, gameScreen.player.bounds)) {
				gameScreen.gameOver = true;
				gameScreen.player.stop();
				forester.stop();
				forester.setPathDirectly(new Vector2(gameScreen.player.getX(), gameScreen.player.getY()));
				forester.addAction(forester.getMoveActionsSequence(forestGraph));
			} else if (gameScreen.isGameOver()) {
				forester.stop();
			} else {
				updateForestersPath(forester, delta);
			}
		}
	}

	private boolean isPlayerCaught(Rectangle f, Rectangle p) {
		Intersector.intersectRectangles(f, p, result);
		float resultArea = result.area();
		return resultArea >= caughtArea;
	}

	private void updateForestersPath(Forester forester, float delta) {
		forester.updateArea();
		forester.updateMovementState(gameScreen.player, delta, forestGraph);
	}

	@Override public void dispose() {
		gameScreen = null;
		forestGraph = null;
		result = null;
		super.dispose();
	}
}
