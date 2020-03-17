package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

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

	public ForestersManager(GameScreen gameScreen, Array<Forester> foresters) {
		foresters.clear();
		this.gameScreen = gameScreen;
		gameObjects = new Array<>(foresters.size);
		gameObjects.addAll(foresters);
		speeches = new Label[foresters.size];

		for (int i = 0; i < foresters.size; i++) {
			Forester f = foresters.get(i);

			float x = f.getNewSpeechX();
			float y = f.getNewSpeechY();

			String speech = SpeechManager.getInstance().getForesterPatrolSpeech();

			speeches[i] = SpeechManager.getInstance().getLabel(speech, x, y,
					GameScreen.tileSize * 2, 0x549A03FF);
		}
	}

	public Array<Forester> getForesters() {
		return gameObjects;
	}

	public void updateForesters(float delta, ForestGraph forestGraph) {
		for (int i = 0; i < gameObjects.size; i++) {
			Forester forester = gameObjects.get(i);
			result.set(0f, 0f, 0f, 0f);
			if (isPlayerCaught(forester, gameScreen.player)) {
				if (gameScreen.player.isStrong()) {
					forester.disable(speeches[i]);
					// TODO: Добавить анимацию драки
				} else {
					gameScreen.gameOver = true;
					gameScreen.player.stop();
					forester.stop();
					forester.setPathDirectly(new Vector2(gameScreen.player.getX(), gameScreen.player.getY()));
					forester.addAction(forester.getMoveActionsSequence(forestGraph));
				}
			} else if (gameScreen.isGameOver()) {
				forester.stop();
			} else {
				updateForestersPath(forester, speeches[i], delta, forestGraph);
			}
			speeches[i].setX(forester.getNewSpeechX());
			speeches[i].setY(forester.getNewSpeechY());
		}
	}

	private boolean isPlayerCaught(Forester forester, Player player) {
		Rectangle f = forester.bounds;
		Rectangle p = player.bounds;
		Intersector.intersectRectangles(f, p, result);
		float resultArea = result.area();
		return resultArea >= caughtArea && !player.isInvisible();
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
}
