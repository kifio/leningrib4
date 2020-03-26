package kifio.leningrib.model

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import kifio.leningrib.model.speech.SpeechManager
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.view.WorldRenderer
import java.util.*

class GameOverDisplay(val mushroomsCount: Int,
                      cameraY: Float) {

    companion object {
        private const val GAME_OVER_TEXT = "ЯДРЕНА КОЧЕРЫЖКА\nТЫ СОБРАЛ %s ГРИБОВ"
    }

    val menu = ResourcesManager.getRegion(ResourcesManager.BACK)
    val menuX: Float
    val menuY: Float
    val menuSize: Float = GameScreen.tileSize.toFloat()

    val restart = ResourcesManager.getRegion(ResourcesManager.RESTART)
    val restartX: Float
    val restartY: Float
    val restartSize: Float = GameScreen.tileSize.toFloat()

    val label: String
    val labelX: Float
    val labelY: Float

    init {
        label = String.format(Locale.getDefault(), GAME_OVER_TEXT, mushroomsCount)
        labelX = Gdx.graphics.width / 2f - SpeechManager.getInstance().getTextWidth(label) / 2
        labelY = cameraY - SpeechManager.getInstance().getTextHeight(label) / 2

        val x = Gdx.graphics.width / 2f
        val y = (cameraY - Gdx.graphics.height / 2f) + 3f * GameScreen.tileSize

        menuX = x - GameScreen.tileSize
        menuY = y

        restartX = x
        restartY = y
    }
    
    fun isRestartTouched(x: Float, y: Float): Boolean {
        val isXValid = x > restartX && x < restartX + restartSize
        val isYValid = y > restartY && y < restartY + restartSize
        Gdx.app.log("kifio", "Restart: isXValid: $isXValid; isYValid: $isYValid")
        return isXValid && isYValid
    }
    
    fun isMenuTouched(x: Float, y: Float): Boolean {
        val isXValid = x > menuX && x < menuX + menuSize
        val isYValid = y > menuY && y < menuY + menuSize
        Gdx.app.log("kifio", "Menu: isXValid: $isXValid; isYValid: $isYValid")
        return isXValid && isYValid
    }
}