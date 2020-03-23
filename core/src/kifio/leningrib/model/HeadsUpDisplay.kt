package kifio.leningrib.model

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import kifio.leningrib.screens.GameScreen

class HeadsUpDisplay {

    private val hudBottle = ResourcesManager.getRegion(ResourcesManager.HUD_BOTTLE)
    private val hudBottlePressed = ResourcesManager.getRegion(ResourcesManager.HUD_BOTTLE_PRESSED)
    private val hudPause = ResourcesManager.getRegion(ResourcesManager.HUD_PAUSE)
    private val hudPausePressed = ResourcesManager.getRegion(ResourcesManager.HUD_PAUSE_PRESSED)
    private val hudBackground = ResourcesManager.getRegion(ResourcesManager.HUD_BACKGROUND)

    var isPauseButtonPressed = false
    var selectedItem = -1

    private val pauseButtonPosition = Rectangle()
    private val itemsButtonsPositions = mutableListOf<Rectangle>(Rectangle())

    fun setPauseButtonPosition(screenRightX: Float,
                               screenTopY: Float) {

        val x = screenRightX - 1.1f * GameScreen.tileSize
        val y = screenTopY - 1.1f * GameScreen.tileSize
        val w = GameScreen.tileSize * 1f
        val h = GameScreen.tileSize * 1f

        pauseButtonPosition.set(x, y, w, h)
    }

    // TODO: Pass list of items which player have
    fun setItemsPosition(screenRightX: Float,
                         screenTopY: Float,
                         items: List<Object>? = null) {   // TODO: pass items which player have

        val x = screenRightX - 1.1f * GameScreen.tileSize
        val y = screenTopY - 2.2f * GameScreen.tileSize

        val w = GameScreen.tileSize * 1f
        val h = GameScreen.tileSize * 1f

        itemsButtonsPositions[0].set(x, y, w, h)
    }

    fun getBottleTexture(): TextureRegion = if (selectedItem == 0) hudBottlePressed else hudBottle

    fun getPauseButtonTexture(): TextureRegion = if (isPauseButtonPressed) hudPausePressed else hudPause

    fun getBackgroundTexture(): TextureRegion = hudBackground

    fun getPauseButtonPosition(): Rectangle  = pauseButtonPosition

    fun getItemsPositions(): List<Rectangle> = itemsButtonsPositions
}