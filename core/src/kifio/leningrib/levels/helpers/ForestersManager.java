package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import kifio.leningrib.levels.FirstLevel;
import kifio.leningrib.model.actors.game.Forester;
import kifio.leningrib.model.actors.game.Player;
import kifio.leningrib.model.items.Bottle;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.model.speech.LabelManager;
import kifio.leningrib.screens.GameScreen;

public class ForestersManager extends ObjectsManager<Forester> {

	private Rectangle result = new Rectangle();
	private static float caughtArea = 0.5f * GameScreen.tileSize * GameScreen.tileSize;

	public ForestersManager(Array<Forester> foresters) {
		addForesters(foresters);
	}

	public void addForesters(Array<Forester> foresters) {
		return;
//		gameObjects.addAll(foresters);
//		Label[] oldSpeeches = speeches;
//
//		speeches = new Label[gameObjects.size];
//
//		for (int i = 0; i < gameObjects.size; i++) {
//			if (oldSpeeches != null && i < oldSpeeches.length) {
//				speeches[i] = oldSpeeches[i];
//				oldSpeeches[i] = null;
//			} else {
//				Forester f = gameObjects.get(i);
//				String speech = "";
//
//				float w = 0;
//				float x = f.getNewSpeechX(w);
//				float y = f.getNewSpeechY();
//
//				speeches[i] = LabelManager.getInstance().getLabel(speech, x, y, Forester.DEFAULT_SPEECH_COLOR);
//			}
//		}
	}

	public void updateForesters(GameScreen gameScreen, float delta, ArrayList<Bottle> bottles, ForestGraph forestGraph) {
		for (int i = 0; i < gameObjects.size; i++) {
			Forester forester = gameObjects.get(i);
			result.set(0f, 0f, 0f, 0f);
			if (isPlayerCaught(forester, gameScreen.player)) {
				if (gameScreen.player.isStrong()) {
					forester.disable(speeches[i]);
					// TODO: Добавить анимацию драки
				} else {
					gameScreen.showGameOver();
					gameScreen.player.stop();
					forester.stop();
					forester.setPathDirectly(new Vector2(gameScreen.player.getX(), gameScreen.player.getY()));
					forester.addAction(forester.getMoveActionsSequence());
				}
			} else if (gameScreen.getGameOver()) {
				forester.stop();
			} else {
				updateForestersPath(forester, bottles, i, delta, forestGraph, gameScreen);
			}
		}
	}

	private boolean isPlayerCaught(Forester forester, Player player) {
		Rectangle f = forester.bounds;
		Rectangle p = player.bounds;
		Intersector.intersectRectangles(f, p, result);
		float resultArea = result.area();
		return resultArea >= caughtArea && !player.isInvisible() && !player.isDexterous();
	}

	private void updateForestersPath(Forester forester,
									 ArrayList<Bottle> bottles,
									 int index,
									 float delta,
									 ForestGraph forestGraph,
									 GameScreen gameScreen) {
		forester.updateArea();
		forester.updateMovementState(gameScreen.player, bottles, delta, forestGraph, gameScreen.isPaused());
		forester.updatePath(forestGraph, gameScreen.player);

		if (forester.isShouldRemoveSpeech()) {
			speeches[index].remove();
		} else if (forester.isShouldResetSpeech()) {
			float w = LabelManager.getInstance().getTextWidth(forester.speech, LabelManager.getInstance().smallFont);
			float x = forester.getNewSpeechX(w);
			float y = forester.getNewSpeechY();
			speeches[index].remove();
			speeches[index] = LabelManager.getInstance().getLabel(forester.speech, x, y, forester.speechColor);
		} else {
			speeches[index].setX(forester.getNewSpeechX(speeches[index].getWidth()));
			speeches[index].setY(forester.getNewSpeechY());
		}

		forester.updateSpeechDuration(delta);
	}

	@Override public void dispose() {
		result = null;
		super.dispose();
	}
}
