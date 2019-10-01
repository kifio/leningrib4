package kifio.leningrib;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.screens.GameScreen;

public class LGCGame extends Game {

	private Screen currentScreen;

    @Override
	public void create () {
		ResourcesManager.init();
		showGameScreen();
	}

    @Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		currentScreen.dispose();
	}

	public void showGameScreen() {
		currentScreen = new GameScreen(this);
		setScreen(currentScreen);
	}

	public void showMenuScreen() {

	}
}
