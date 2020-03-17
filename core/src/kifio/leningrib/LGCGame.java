package kifio.leningrib;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import generator.Config;
import kifio.leningrib.model.ResourcesManager;
import kifio.leningrib.screens.GameScreen;
import kifio.leningrib.screens.MenuScreen;

public class LGCGame extends Game {

	private Screen currentScreen;
	private Config constantsConfig = new Config(10, 46);

	@Override
	public void create () {
		ResourcesManager.init(constantsConfig.getLevelWidth(), constantsConfig.getLevelHeight());
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
		currentScreen = new GameScreen(this, constantsConfig);
		setScreen(currentScreen);
	}

	public void showMenuScreen() {
		currentScreen = new MenuScreen(this);
		setScreen(currentScreen);
	}
}
