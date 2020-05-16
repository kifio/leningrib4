package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.LUTController
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.screens.GameScreen

class SquareButton(
        private val pressedState: TextureRegion,
        private val unpressedState: TextureRegion,
        camera: OrthographicCamera,
        lutController: LUTController,
        private var order: Int = BUTTON,
        private val orientation: Int = RIGHT
) : StaticActor(unpressedState, camera, lutController) {

    companion object {
        const val LEFT = 0
        const val RIGHT = 1

        const val BUTTON = 1
        const val VODKA = 2
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
        val offset = order * height + (order * (16 * Gdx.graphics.density))
        val y = camera.position.y + (Gdx.graphics.height / 2f) - offset
        setBounds(x, y, width, height)
        super.draw(batch, parentAlpha)
    }
}