package kifio.leningrib;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import kifio.leningrib.model.TextureManager;
import kifio.leningrib.screens.GameScreen;

public class LGCGame extends Game {

	private Screen currentScreen;

    @Override
	public void create () {
		TextureManager.init();
		currentScreen = new GameScreen();
		setScreen(currentScreen);
	}

    @Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		currentScreen.dispose();
	}
}
