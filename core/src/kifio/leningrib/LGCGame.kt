package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import generator.Config
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.LaunchScreen
import kifio.leningrib.screens.MenuScreen

class LGCGame() : Game() {

    companion object {
        @JvmStatic var isDebug = false
    }

    private var currentScreen: Screen? = null
    private val constantsConfig = Config(10, 46)
    private val camera: OrthographicCamera = OrthographicCamera()

    override fun create() {
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
        currentScreen = LaunchScreen(this, camera)
        setScreen(currentScreen)
        ResourcesManager.loadGameAssets(currentScreen as ResourcesManager.ResourceLoadingListener)
    }

    fun showGameScreen() {
        currentScreen?.dispose()
        currentScreen = GameScreen(this, camera, constantsConfig)
        setScreen(currentScreen)
    }

    fun showMenuScreen() {
        currentScreen = MenuScreen(this)
        setScreen(currentScreen)
    }
}