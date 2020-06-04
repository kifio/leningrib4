package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.sun.org.apache.xpath.internal.operations.Bool
import generator.Config
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.levels.FirstLevel
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.platform.PlayGamesClientInterface
import kifio.leningrib.platform.StoreInterface
import kifio.leningrib.screens.BaseScreen
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.LaunchScreen
import model.LevelMap
import model.WorldMap
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom

class LGCGame() : Game() {

    companion object {

        const val LEVEL_WIDTH = 10
        const val ANIMATION_DURATION = 0.3f
        const val ANIMATION_DURATION_LONG = 0.6f

        const val PREFERENCES_NAME = "kifio.leningrib"
        const val MAX_SCORE = "MAX_SCORE"
        const val TUTORIAL_WAS_SHOWN = "TUTORIAL_WAS_SHOWN"

        const val MUSIC = 0
        const val SOUNDS = 1

        fun getLevelAndPlayer(worldMap: WorldMap): Pair<Player, Level> {
            val levelMap: LevelMap
            val level: Level
            val player: Player

            val config = Config(LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
            levelMap = worldMap.addLevel(0, 0, null, config)
            val room = levelMap.rooms[0]
            val x = 3f
            val y = ThreadLocalRandom.current().nextInt(room.y + 1, room.y + room.height - 3).toFloat()
            player = Player(x * GameScreen.tileSize, y * GameScreen.tileSize)
            level = CommonLevel(player, levelMap)

            return Pair(player, level)
        }
    }

    private lateinit var prefs: Preferences
    private var music: Music? = null

    private val onCompleteListener: Music.OnCompletionListener = Music.OnCompletionListener {
        if (prefs.getBoolean(MUSIC.toString())) {
            startMusic()
        }
    }

    override fun create() {
        prefs = Gdx.app.getPreferences(PREFERENCES_NAME)

        if (!prefs.contains(MUSIC.toString()) && !prefs.contains(SOUNDS.toString())) {
            prefs.putBoolean(MUSIC.toString(), true)
            prefs.putBoolean(SOUNDS.toString(), true)
            prefs.flush()
        }

        ResourcesManager.loadSplash()
        GameScreen.tileSize = Gdx.graphics.width / LEVEL_WIDTH
        showLaunchScreen()
    }

    override fun dispose() {
        getScreen()?.dispose()
    }

    private fun showLaunchScreen() {
        ResourcesManager.loadSplash()
        setScreen(LaunchScreen(this))
    }

    fun showGameScreen(gameScreen: GameScreen?) {
        if (music == null && prefs.getBoolean(MUSIC.toString())) {
            startMusic()
        }

        if (gameScreen == null) return
        gameScreen.activate()
        showScreen(gameScreen)
    }


    fun isSettingEnabled(index: Int): Boolean {
        return prefs.getBoolean(index.toString())
    }

    fun handleSetting(index: Int, enabled: Boolean) {
        prefs.putBoolean(index.toString(), enabled)
        prefs.flush()

        if (index == MUSIC) {
            resetMusic(enabled)
        }
    }

    private fun resetMusic(enabled: Boolean) {
        if (enabled) {
            startMusic()
        } else {
            stopMusic()
        }
    }

    private fun startMusic() {
        music?.setOnCompletionListener(null)
        music = ResourcesManager.getNextMusic()
        music?.setOnCompletionListener(onCompleteListener)
        music?.play()
    }

    private fun stopMusic() {
        music?.setOnCompletionListener(null)
        music?.stop()
        music?.dispose()
    }

    fun saveMaxScore(score: Long) {
        prefs.putLong(MAX_SCORE, score)
        prefs.flush()
    }

    fun getMaxScore() = prefs.getLong(MAX_SCORE, 0) ?: 0

    fun setTutorialWasShown() {
        prefs.putBoolean(TUTORIAL_WAS_SHOWN, true)
        prefs.flush()
    }

    fun wasTutorialShown() = prefs.getBoolean(TUTORIAL_WAS_SHOWN)

    private fun showScreen(screen: Screen) {
        val currentScreen = getScreen()
        createScreenOutAction(currentScreen, Runnable {
            createScreenInAction(screen)
        })
    }

    private fun createScreenOutAction(screen: Screen, runnable: Runnable) {
        val actor: Actor = (screen as BaseScreen).getTransitionActor()
        actor.color.a = 1f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.parallel(Actions.alpha(0f, ANIMATION_DURATION_LONG), Actions.scaleTo(1.5f, 1.5f, ANIMATION_DURATION_LONG, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run(runnable))
        actor.addAction(sequenceAction)
    }

    private fun createScreenInAction(screen: Screen) {
        val current = getScreen()
        setScreen(screen)
        val actor: Actor = (screen as BaseScreen).stage.root
        actor.setOrigin(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
        actor.color.a = 0f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, 0f))
        sequenceAction.addAction(Actions.parallel(Actions.alpha(1f, ANIMATION_DURATION_LONG), Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION_LONG, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run { current.dispose() })
        actor.addAction(sequenceAction)
    }
}