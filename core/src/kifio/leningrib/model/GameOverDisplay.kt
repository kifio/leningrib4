package kifio.leningrib.model

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import kifio.leningrib.model.speech.SpeechManager
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.view.WorldRenderer
import java.util.*

class GameOverDisplay(mushroomsCount: Int,
                      cameraY: Float) {

    companion object {
        private const val GAME_OVER_TEXT = "ЯДРЕНА КОЧЕРЫЖКА\nТЫ СОБРАЛ %s ГРИБОВ"
    }

    val btnSize = GameScreen.tileSize.toFloat()

    val back = ResourcesManager.getRegion(ResourcesManager.BACK)
    val backX: Float
    val backY: Float

    val restart = ResourcesManager.getRegion(ResourcesManager.RESTART)
    val restartX: Float
    val restartY: Float

    val label: String
    val labelX: Float
    val labelY: Float

    init {
        label = String.format(Locale.getDefault(), GAME_OVER_TEXT, mushroomsCount)
        labelX = Gdx.graphics.width / 2f - SpeechManager.getTextWidth(label) / 2
        labelY = cameraY - SpeechManager.getTextHeight(label) / 2

        val x = Gdx.graphics.width / 2f
        val y = (cameraY + Gdx.graphics.height / 2f) - 3f * GameScreen.tileSize

        backX = x - GameScreen.tileSize
        backY = y

        restartX = x + GameScreen.tileSize
        restartY = y
    }
}