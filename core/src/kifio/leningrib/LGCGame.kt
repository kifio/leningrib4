package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import generator.Config
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.screens.BaseScreen
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.LaunchScreen
import model.LevelMap
import model.WorldMap


class LGCGame() : Game() {

    companion object {
        @JvmStatic var isDebug = false
    }

    private var currentScreen: Screen? = null
    private val constantsConfig = Config(10, 46)
    private val camera: OrthographicCamera = OrthographicCamera()

    private var halfWidth = 0f
    private var halfHeight = 0f
    private val screenSwitchDuration = 1f

    override fun create() {
        halfWidth = Gdx.graphics.width / 2f
        halfHeight = Gdx.graphics.height / 2f
        ResourcesManager.loadSplash()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        GameScreen.tileSize = width / constantsConfig.levelWidth
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        showLaunchScreen()
    }

    override fun dispose() {
        currentScreen!!.dispose()
    }

    private fun showLaunchScreen() {
        ResourcesManager.loadSplash()
        currentScreen = LaunchScreen(this, camera, constantsConfig)
        setScreen(currentScreen)
    }

    fun showGameScreen(worldMap: WorldMap, levelMap: LevelMap) {
        currentScreen?.dispose()
        currentScreen = GameScreen(this, camera, worldMap, levelMap, constantsConfig)
        showScreen(currentScreen)
    }

    private fun showScreen(screen: Screen?) {
        createScreenOutAction(getScreen(), Runnable { createScreenInAction(screen!!) })
    }

    private fun createScreenOutAction(screen: Screen, runnable: Runnable) {
        val actor: Actor = (screen as BaseScreen).stage.root
        actor.setOrigin(halfWidth, halfHeight)
        actor.color.a = 1f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.parallel(Actions.alpha(0f, screenSwitchDuration), Actions.scaleTo(1.1f, 1.1f, screenSwitchDuration, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run(runnable))
        actor.addAction(sequenceAction)
    }

    private fun createScreenInAction(screen: Screen) {
        setScreen(screen)
        val actor: Actor = (screen as BaseScreen).stage.root
        actor.setOrigin(halfWidth, halfHeight)
        actor.color.a = 0f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, 0f))
        sequenceAction.addAction(Actions.parallel(Actions.alpha(1f, screenSwitchDuration), Actions.scaleTo(1.0f, 1.0f, screenSwitchDuration, Interpolation.exp5)))
        actor.addAction(sequenceAction)
    }
}