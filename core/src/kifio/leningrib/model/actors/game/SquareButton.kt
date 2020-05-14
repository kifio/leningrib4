package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.screens.GameScreen
import java.awt.ComponentOrientation

class SquareButton(
        private val pressedState: TextureRegion,
        private val unpressedState: TextureRegion,
        camera: OrthographicCamera,
        private val orientation: Int = 1
) : StaticActor(unpressedState, camera) {

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
    }

    var onTouchHandler: (() -> Unit)? = null

    init {

        region?.let {region ->
            val scale = GameScreen.tileSize.toFloat() / region.regionWidth.toFloat()
            this.width = region.regionWidth * scale
            this.height = scale * region.regionHeight
        }

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                region = pressedState
                touched = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                region = unpressedState
                touched = false
                onTouchHandler?.invoke()
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val x: Float = if (orientation == LEFT) {
            8 * Gdx.graphics.density
        } else {
            Gdx.graphics.width - (width + 16 * Gdx.graphics.density)
        }
        val y = camera.position.y + (Gdx.graphics.height / 2f) - (height + 16 * Gdx.graphics.density)
        setBounds(x, y, width, height)
        super.draw(batch, parentAlpha)
    }
}