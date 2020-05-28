package kifio.leningrib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import kifio.leningrib.platform.OnGetScoreListener;
import kifio.leningrib.platform.OnInitListener;
import kifio.leningrib.platform.PlayGamesClientInterface;

public class PlayGamesClient implements PlayGamesClientInterface {

    private static final int SIGN_IN_REQUEST_CODE = 99;
    private static final int LEADERBOARDS_REQUEST_CODE = 100;
    private static final int ACHIEVEMENTS_REQUEST_CODE = 101;

    private static final String AUTH_ERR = "auth_err";
    private static final String STATUS = "status";
    private static final String STATUS_CODE = "status_code";

    private static final String LEADERBOAD_ID = "CgkIrqeKmpgIEAIQAQ";

    private GoogleSignInClient googleSignInClient;
    private AchievementsClient achievementClient;
    private LeaderboardsClient leaderboardsClient;

    private Activity ctx;
    private OnInitListener onInitListener;
    private FirebaseAnalytics firebaseAnalytics;

    PlayGamesClient(Activity ctx, FirebaseAnalytics firebaseAnalytics) {
        this.ctx = ctx;
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public void initGoogleClientAndSignIn(OnInitListener listener) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();
        this.onInitListener = listener;
        googleSignInClient = GoogleSignIn.getClient(ctx, gso);
        googleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {

            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                if (task.isSuccessful()) {
                    initClients(task.getResult());
                } else {
                    signInWithUI();
                }
            }
        });
    }

    void handleSignInResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PlayGamesClient.SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null && result.isSuccess()) {
                // The signed in account is stored in the result.
                initClients(result.getSignInAccount());
            } else {
                if (result != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(STATUS, result.getStatus().getStatusMessage());
                    bundle.putInt(STATUS_CODE, result.getStatus().getStatusCode());
                    firebaseAnalytics.logEvent(AUTH_ERR, bundle);
                }
                String message = "Произошла ошибка при авторизации через Play Games";
                new AlertDialog.Builder(ctx).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
                onInitListener.onInit();
            }
        }
    }

    private void initClients(GoogleSignInAccount account) {
        if (account != null && !ctx.isDestroyed()) {
            achievementClient = Games.getAchievementsClient(ctx, account);
            leaderboardsClient = Games.getLeaderboardsClient(ctx, account);
        }

        if (onInitListener != null) {
            onInitListener.onInit();
            onInitListener = null;
        }
    }

    private void signInWithUI() {
        Intent intent = googleSignInClient.getSignInIntent();
        ctx.startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
    }

    @Override
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

    @Override
    public void openLeaderBoards() {
        if (leaderboardsClient != null) {
            if (!ctx.isDestroyed()) {
                leaderboardsClient.getAllLeaderboardsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        ctx.startActivityForResult(intent, LEADERBOARDS_REQUEST_CODE);
                    }
                });
            }
        } else {
            signInWithUI();
        }
    }

    @Override
    public void submitScore(final long score) {
        if (leaderboardsClient != null && !ctx.isDestroyed()) {
            leaderboardsClient.submitScoreImmediate(LEADERBOAD_ID, score).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Gdx.app.log("kPlayGamesClient", "Cannot submit score: " + score);
                }
            });
        }
    }

    @Override
    public void queryScore(final OnGetScoreListener listener) {
        if (leaderboardsClient != null && !ctx.isDestroyed()) {
            leaderboardsClient.loadCurrentPlayerLeaderboardScore(LEADERBOAD_ID,
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC).addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardScore>>() {
                @Override
                public void onSuccess(AnnotatedData<LeaderboardScore> leaderboardScoreAnnotatedData) {
                    LeaderboardScore score = leaderboardScoreAnnotatedData.get();
                    if (score != null) {
                        listener.onGetScore(score.getRawScore());
                    }
                }
            });
        }
    }
}
