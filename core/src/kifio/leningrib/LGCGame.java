package kifio.leningrib;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.screens.GameScreen;
import kifio.leningrib.screens.MenuScreen;

public class LGCGame extends Game {

	private Screen currentScreen;

    @Override
	public void create () {
		ResourcesManager.init();
		showMenuScreen();
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
		currentScreen = new MenuScreen(this);
		setScreen(currentScreen);
	}
}
