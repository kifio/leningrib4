package kifio.leningrib;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AndroidLauncher extends AndroidApplication {

	private GoogleSignInClient googleSignInClient;
	private AchievementsClient achievementClient;
	private LeaderboardsClient leaderboardsClient;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGoogleClientAndSignIn();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new LGCGame(new Store()), config);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	private void initGoogleClientAndSignIn() {
		googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(
				GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

		googleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {

			@Override
			public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
				if (task.isSuccessful()) {
					GoogleSignInAccount account = task.getResult();
					if (account != null) {
						achievementClient = Games.getAchievementsClient(AndroidLauncher.this, account);
						leaderboardsClient = Games.getLeaderboardsClient(AndroidLauncher.this, account);
					}
				} else {
					Log.e("Error", "signInError", task.getException());
				}
			}
		});
	}
}
