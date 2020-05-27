package kifio.leningrib;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {

	private PlayGamesClient playGamesClient = new PlayGamesClient(this);

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Store store = new Store(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new LGCGame(store, playGamesClient), config);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		playGamesClient.handleSignInResult(requestCode, resultCode, data);
	}

}
