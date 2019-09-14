package kifio.leningrib.levels;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.Utils;
import kifio.leningrib.model.actors.Forester;
import kifio.leningrib.model.actors.Player;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.SpeechManager;
import kifio.leningrib.screens.GameScreen;

public class ForestersManager extends ObjectsManager<Forester> {

	private GameScreen gameScreen;
	private Rectangle result = new Rectangle();
	private static float caughtArea = 0.5f * GameScreen.tileSize * GameScreen.tileSize;

	ForestersManager(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	void initForester(Rectangle[] roomsRectangles) {
		speeches = new Label[roomsRectangles.length];
		gameObjects = new Array<>(roomsRectangles.length - 1);

		int k = 0;
		for (int i = 0; i < roomsRectangles.length; i++) {
			if (!Utils.isInRoom(roomsRectangles[i],
				gameScreen.player.getX() / GameScreen.tileSize, gameScreen.player.getY() / GameScreen.tileSize)) {

				int left = (int) (roomsRectangles[i].x + 1);
				int top = (int) (roomsRectangles[i].y + roomsRectangles[i].height);
				int right = (int) (roomsRectangles[i].width - 2);
				int bottom = (int) roomsRectangles[i].y;

				int originalFromY = ThreadLocalRandom.current().nextInt(bottom, top);
				int originalToY = ThreadLocalRandom.current().nextInt(bottom, top);

				Forester f = new Forester(
					GameScreen.tileSize * left,
					GameScreen.tileSize * originalFromY,
					GameScreen.tileSize * right, GameScreen.tileSize * originalToY, 1, bottom, top);

				float x = getNewSpeechX(f);
				float y = getNewSpeechY(f);

				speeches[i - k] = SpeechManager.getInstance().getLabel(SpeechManager.getInstance().getForesterPatrolSpeech(), x, y,
					GameScreen.tileSize * 2);

				gameObjects.add(f);
			} else {
				k++;
			}
		}
	}

	void initDebugForester(Forester forester) {
		gameObjects.add(forester);
	}

	public Array<Forester> getForesters() {
		return gameObjects;
	}

	void updateForesters(float delta, ForestGraph forestGraph) {
		for (int i = 0; i < gameObjects.size; i++) {
			Forester forester = gameObjects.get(i);
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
				updateForestersPath(forester, speeches[i], delta, forestGraph);
			}
			speeches[i].setX(getNewSpeechX(forester));
			speeches[i].setY(getNewSpeechY(forester));
		}
	}

	private boolean isPlayerCaught(Rectangle f, Rectangle p) {
		Intersector.intersectRectangles(f, p, result);
		float resultArea = result.area();
		return resultArea >= caughtArea;
	}

	private void updateForestersPath(Forester forester, Label label, float delta, ForestGraph forestGraph) {
		forester.updateArea();
		forester.updateMovementState(gameScreen.player, label, delta, forestGraph);
	}

	@Override public void dispose() {
		gameScreen = null;
		result = null;
		super.dispose();
	}

	private float getNewSpeechX(Forester m) {
		return m.getX() - GameScreen.tileSize / 2f;
	}

	private float getNewSpeechY(Forester m) {
		return m.getY() + GameScreen.tileSize * 1.5f;
	}
}
