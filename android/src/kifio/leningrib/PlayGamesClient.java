package kifio.leningrib;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class PlayGamesClient {

    static final int LEADERBOARDS_REQUEST_CODE = 100;
    static final int ACHIEVEMENTS_REQUEST_CODE = 101;

    private GoogleSignInClient googleSignInClient;
    private AchievementsClient achievementClient;
    private LeaderboardsClient leaderboardsClient;

    private Activity ctx;

    PlayGamesClient(Activity ctx) {
        this.ctx = ctx;
    }

    void initGoogleClientAndSignIn() {
        googleSignInClient = GoogleSignIn.getClient(ctx, new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        googleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {

            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                if (task.isSuccessful()) {
                    GoogleSignInAccount account = task.getResult();
                    if (account != null && !ctx.isDestroyed()) {
                        achievementClient = Games.getAchievementsClient(ctx, account);
                        leaderboardsClient = Games.getLeaderboardsClient(ctx, account);
                    }
                } else {
                    Log.e("Error", "signInError", task.getException());
                }
            }
        });
    }

    public void openAchievements() {
        if (achievementClient != null && !ctx.isDestroyed()) {
            achievementClient.getAchievementsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    ctx.startActivityForResult(intent, ACHIEVEMENTS_REQUEST_CODE);
                }
            });
        }
    }

    public void openLeaderBoards() {
        if (leaderboardsClient != null && !ctx.isDestroyed()) {
            leaderboardsClient.getAllLeaderboardsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    ctx.startActivityForResult(intent, LEADERBOARDS_REQUEST_CODE);
                }
            });
        }
    }
}
