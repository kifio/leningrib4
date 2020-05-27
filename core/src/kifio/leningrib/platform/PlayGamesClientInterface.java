package kifio.leningrib.platform;

public interface PlayGamesClientInterface {

    void initGoogleClientAndSignIn(OnInitListener listener);

    void openAchievements();

    void openLeaderBoards();

    void submitScore(long score);

    void queryScore(OnGetScoreListener listener);
}