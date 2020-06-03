package kifio.leningrib.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import kifio.leningrib.LGCGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.useGL30 = true;
		config.width = 540;
		config.height = 960;
		new LwjglApplication(new LGCGame(), config);
	}
}
