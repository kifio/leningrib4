package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import generator.Config
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.levels.FirstLevel
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.screens.BaseScreen
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.LaunchScreen
import model.LevelMap
import model.WorldMap
import java.lang.reflect.Array.set
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom

class LGCGame(isDebug: Boolean) : Game() {

    companion object {
        const val LEVEL_WIDTH = 10
        const val ANIMATION_DURATION = 0.3f
        const val ANIMATION_DURATION_LONG = 0.6f
        const val PREFERENCES_NAME = "kifio.leningrib"
        const val FIRST_LEVEL_PASSED = "FIRST_LEVEL_PASSED"

        var isDebug = false

        @JvmStatic
        var lastKnownCameraPosition = 0f

        private var firstLevelPassed = false

        private var prefs: Preferences? = null

        fun isFirstLevelPassed() = firstLevelPassed

        fun setFirstLevelPassed(passed: Boolean) {
            firstLevelPassed = passed
            prefs?.putBoolean(FIRST_LEVEL_PASSED, passed)
            prefs?.flush()
        }

        fun getPreferences() = prefs

        fun getLevelAndPlayer(worldMap: WorldMap): Pair<Player, Level> {
            val levelMap: LevelMap
            val level: Level
            val player: Player
            if (isFirstLevelPassed()) {
                val config = Config(LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
                levelMap = worldMap.addLevel(0, 0, null, config)
                val room = levelMap.rooms[0]
                val x = ThreadLocalRandom.current().nextInt(2, LEVEL_WIDTH - 2).toFloat()
                val y = ThreadLocalRandom.current().nextInt(room.y + 1, room.y + room.height - 2).toFloat()
                player = Player(x * GameScreen.tileSize, y * GameScreen.tileSize)
                level = CommonLevel(player, levelMap)
            } else {
                val firstRoomHeight = (Gdx.graphics.height / GameScreen.tileSize) - 2
                val config = Config(LEVEL_WIDTH, firstRoomHeight + 20)
                levelMap = worldMap.addFirstLevel(config, firstRoomHeight)
                player = FirstLevel.getPlayer()
                level = FirstLevel(player, levelMap)
            }
            return Pair(player, level)
        }
    }

    val camera: OrthographicCamera = OrthographicCamera()
    val executor = Executors.newSingleThreadExecutor()

    private var halfWidth = 0f
    private var halfHeight = 0f

    init {
        LGCGame.isDebug = isDebug
    }

    override fun create() {
        prefs = Gdx.app.getPreferences(PREFERENCES_NAME)
        firstLevelPassed = prefs?.getBoolean(FIRST_LEVEL_PASSED) ?: false
        halfWidth = Gdx.graphics.width / 2f
        halfHeight = Gdx.graphics.height / 2f
        lastKnownCameraPosition = Gdx.graphics.height / 2f
        ResourcesManager.loadSplash()
        GameScreen.tileSize = Gdx.graphics.width / LEVEL_WIDTH
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        showLaunchScreen()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun dispose() {
        getScreen()?.dispose()
    }

    private fun showLaunchScreen() {
        ResourcesManager.loadSplash()
        setScreen(LaunchScreen(this))
    }

    fun showGameScreen(gameScreen: GameScreen?) {
        if (gameScreen == null) return
        gameScreen.activate()
        showScreen(gameScreen)
    }

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
        actor.setOrigin(halfWidth, halfHeight)
        actor.color.a = 0f
        lastKnownCameraPosition = Gdx.graphics.height / 2f
        camera.position.y = (screen as? GameScreen)?.player?.y ?: Gdx.graphics.height / 2f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, 0f))
        sequenceAction.addAction(Actions.parallel(Actions.alpha(1f, ANIMATION_DURATION_LONG), Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION_LONG, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run { current.dispose() })
        actor.addAction(sequenceAction)
    }
}