package kifio.leningrib.model

import com.badlogic.gdx.Gdx
import kifio.leningrib.screens.GameScreen

class PauseDisplay(cameraY: Float) {

    val menu = ResourcesManager.getRegion(ResourcesManager.MENU)
    val menuX: Float
    val menuY: Float
    val menuWidth: Float
    val menuHeight: Float

    val resume = ResourcesManager.getRegion(ResourcesManager.RESUME)
    val resumeX: Float
    val resumeY: Float
    val resumeWidth: Float
    val resumeHeight: Float

    init {

        val x = Gdx.graphics.width / 2f
        val y = cameraY
        val buttonWidth = (menu.regionWidth * Gdx.graphics.density)
        val buttonHeight = (menu.regionHeight * Gdx.graphics.density)

        menuX = x - buttonWidth / 2f
        menuY = y + GameScreen.tileSize
        menuWidth = buttonWidth
        menuHeight = buttonHeight

        resumeX = x - buttonWidth / 2f
        resumeY = y - GameScreen.tileSize
        resumeWidth = buttonWidth
        resumeHeight = buttonHeight
    }
    
    fun isResumeTouched(x: Float, y: Float): Boolean {
        val isXValid = x > resumeX && x < resumeX + resumeWidth
        val isYValid = y > resumeY && y < resumeY + resumeHeight
        return isXValid && isYValid
    }
    
    fun isMenuTouched(x: Float, y: Float): Boolean {
        val isXValid = x > menuX && x < menuX + menuWidth
        val isYValid = y > menuY && y < menuY + menuHeight
        return isXValid && isYValid
    }
}