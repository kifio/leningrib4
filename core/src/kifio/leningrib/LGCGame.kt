package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.sun.org.apache.xpath.internal.operations.Bool
import generator.Config
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.screens.BaseScreen
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.LaunchScreen
import model.LevelMap
import model.WorldMap


class LGCGame(isDebug: Boolean) : Game() {

    companion object {

        private const val FIRST_LEVEL_PASSED = "FIRST_LEVEL_PASSED"
        const val PREFERENCES_NAME = "kifio.leningrib"

        var isDebug = false

        const val LEVEL_WIDTH = 10
        const val LEVEL_HEIGHT = 46

        @JvmStatic
        fun getConfig(): Config  = Config(LEVEL_WIDTH, LEVEL_HEIGHT)

        fun firstLevelPassed(): Boolean {
            return Gdx.app.getPreferences(PREFERENCES_NAME).getBoolean(FIRST_LEVEL_PASSED)
        }
    }

    val camera: OrthographicCamera = OrthographicCamera()

    private var halfWidth = 0f
    private var halfHeight = 0f
    private val screenSwitchDuration = 0.3f

    init {
        LGCGame.isDebug = isDebug
    }

    override fun create() {
        halfWidth = Gdx.graphics.width / 2f
        halfHeight = Gdx.graphics.height / 2f
        ResourcesManager.loadSplash()

        GameScreen.tileSize = Gdx.graphics.width / LEVEL_WIDTH

        GameScreen.bottomCameraThreshold = Gdx.graphics.height / 2f
        GameScreen.topCameraThreshold = LEVEL_HEIGHT * GameScreen.tileSize - Gdx.graphics.height / 2f

        GameScreen.xLimit = Gdx.graphics.width - GameScreen.tileSize.toFloat()
        GameScreen.yLimit = (LEVEL_HEIGHT - 1) * GameScreen.tileSize.toFloat()

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
        val actor: Actor = (screen as BaseScreen).stage.root
        actor.setOrigin(halfWidth, halfHeight)
        actor.color.a = 1f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.parallel(Actions.alpha(0f, screenSwitchDuration), Actions.scaleTo(1.5f, 1.5f, screenSwitchDuration, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run(runnable))
        actor.addAction(sequenceAction)
    }

    private fun createScreenInAction(screen: Screen) {
        val current = getScreen()
        setScreen(screen)
        val actor: Actor = (screen as BaseScreen).stage.root
        actor.setOrigin(halfWidth, halfHeight)
        actor.color.a = 0f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, 0f))
        sequenceAction.addAction(Actions.parallel(Actions.alpha(1f, screenSwitchDuration), Actions.scaleTo(1.0f, 1.0f, screenSwitchDuration, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run { current.dispose() })
        actor.addAction(sequenceAction)
    }
}