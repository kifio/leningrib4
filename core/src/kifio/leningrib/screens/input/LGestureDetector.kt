package kifio.leningrib.screens.input

import com.badlogic.gdx.input.GestureDetector
import kifio.leningrib.screens.GameScreen

class LGestureDetector(listener: GestureListener?,
                       private var gameScreen: GameScreen?) : GestureDetector(listener) {

    override fun keyDown(keycode: Int): Boolean {
        gameScreen?.handleKeyDown(keycode)
        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        gameScreen?.handleKeyUp(keycode)
        return super.keyUp(keycode)
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        gameScreen?.handleTouchUp(x, y, pointer, button)
        return super.touchUp(x, y, pointer, button)
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        gameScreen?.handleTouchDown(x, y, pointer, button)
        return super.touchDown(x, y, pointer, button)
    }

    fun dispose() {
        gameScreen = null
    }
}