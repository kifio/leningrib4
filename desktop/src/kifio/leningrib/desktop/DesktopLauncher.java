package kifio.leningrib.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;
import java.awt.Toolkit;

import kifio.leningrib.LGCGame;

public class DesktopLauncher {

	private static final float GAME_WIDTH = 540;
	private static final float GAME_HEIGHT = 960;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		float screenHeight = dimension.height * 0.9f;
		float scale = screenHeight / GAME_HEIGHT;
		config.width = (int) (GAME_WIDTH * scale);
		config.height = (int) (GAME_HEIGHT * scale);
		config.resizable = false;
		new LwjglApplication(new LGCGame(), config);
	}
}
