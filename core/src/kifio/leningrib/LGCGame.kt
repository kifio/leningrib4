package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import generator.Config
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.MenuScreen

class LGCGame : Game() {
    private var currentScreen: Screen? = null
    private val constantsConfig = Config(10, 46)
    override fun create() {
        ResourcesManager.init(constantsConfig.levelWidth, constantsConfig.levelHeight)
        showMenuScreen()
    }

    override fun dispose() {
        currentScreen!!.dispose()
    }

    fun showGameScreen() {
        currentScreen?.dispose()
        currentScreen = GameScreen(this, constantsConfig)
        setScreen(currentScreen)
    }

    fun showMenuScreen() {
        currentScreen?.dispose()
        currentScreen = MenuScreen(this)
        setScreen(currentScreen)
    }
}