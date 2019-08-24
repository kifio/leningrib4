package kifio.leningrib.levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kifio.leningrib.Utils;
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

	void initForester(int levelX, int levelY, Rectangle[] roomsRectangles, Random random) {
		Vector2 playerPosition;

		if (levelX == 0 && levelY == 0) {
			playerPosition = new Vector2(0, 1);
		} else {
			playerPosition = new Vector2(gameScreen.player.getX() / GameScreen.tileSize,
				gameScreen.player.getY() / GameScreen.tileSize);
		}

		for (Rectangle rectangle : roomsRectangles) {
			if (!rectangle.contains(playerPosition) && rectangle.height > 1) {
				boolean generateDifficultWay = false;
				if (rectangle.height > 3) generateDifficultWay = random.nextBoolean();

				if (generateDifficultWay) {
					Array<Vector2> points = new Array<>();
					float y0 = rectangle.y + (rectangle.height - 1);
					float y1 = rectangle.y + 1;

					points.add(new Vector2(GameScreen.tileSize * (rectangle.x + 1), GameScreen.tileSize * y0));
					points.add(new Vector2(GameScreen.tileSize * (rectangle.width - 2), GameScreen.tileSize * y0));
					points.add(new Vector2(GameScreen.tileSize * (rectangle.width - 2), GameScreen.tileSize * y1));
					points.add(new Vector2(GameScreen.tileSize * (rectangle.x + 1), GameScreen.tileSize * y1));

					gameObjects.add(new Forester(points));
				} else {
					float y = GameScreen.tileSize * MathUtils.random(rectangle.y, rectangle.y + (rectangle.height - 2));
					gameObjects.add(new Forester(
						new Vector2(GameScreen.tileSize * (rectangle.x + 1), y),
						new Vector2(GameScreen.tileSize * (rectangle.width - 2), y)));
				}
			}
		}
	}

	public List<Forester> getForesters() {
		return gameObjects;
	}

	private void updateForesters(float delta) {
		for (Forester forester : gameObjects) {
			result.set(0f, 0f, 0f, 0f);
			if (isPlayerCaught(forester.bounds, gameScreen.player.bounds)) {
				gameScreen.gameOver = true;
				gameScreen.player.stop();
				forester.stop();
				forester.path.add(new Vector2(gameScreen.player.getX(), gameScreen.player.getY()));
				forester.addAction(forester.getMoveActionsSequence());
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
		forester.updateMoving(gameScreen.player, delta);
		if (forester.isPursuePlayer()) {
			setForesterPath(forester, gameScreen.player.bounds.x, gameScreen.player.bounds.y);
		}
	}

	private void setForesterPath(Forester forester, float tx, float ty) {
		GraphPath<Vector2> path = forestGraph.getPath(
			Utils.mapCoordinate(forester.getX()),
			Utils.mapCoordinate(forester.getY()),
			Utils.mapCoordinate(tx),
			Utils.mapCoordinate(ty));

		forester.stop();

		// Первая точка пути совпадает с координатами игрока,
		// чтобы лесник не стоял на месте лишнее время ее из пути удаляем.
		int start = path.getCount() > 1 ? 1 : 0;
		for (int i = start; i < path.getCount(); i++) {
			forester.path.add(new Vector2(path.get(i)));
		}

		forester.addAction(forester.getMoveActionsSequence());
	}

	@Override
	public void dispose() {
		gameScreen = null;
		forestGraph = null;
		result = null;
		super.dispose();
	}
}
