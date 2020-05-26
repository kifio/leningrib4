package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager.getClickSound
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
        const val GUM = 3
    }

    var onTouchHandler: (() -> Unit)? = null
    private var hiddenOffset = 0
    private var animationStartTime = 0f
    private var animationTime = 0f

    init {

        region?.let {region ->
            val scale = GameScreen.tileSize.toFloat() / region.regionWidth.toFloat()
            this.width = region.regionWidth * scale
            this.height = scale * region.regionHeight
        }

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                getClickSound()?.play()
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
        val w = width * scaleX
        val h = height * scaleY

        val wOffset = ((width - w) / 2)
        val hOffset = ((height - h) / 2)

        val x: Float = if (orientation == LEFT) {
            16 * Gdx.graphics.density + wOffset
        } else {
            Gdx.graphics.width - (width + 16 * Gdx.graphics.density) + wOffset
        }

        val offset = order * height + (order * (16 * Gdx.graphics.density))

        if (hiddenOffset != 0) {
            animationTime += Gdx.app.graphics.deltaTime
        }

        val y = camera.position.y + (Gdx.graphics.height / 2f) - offset + hOffset

        setBounds(x, y, width, height)
        batch.shader = null
        if (region != null) batch.draw(region, x, y, w, h)
        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }

    fun getHeightWithOffsets(): Float {
        val w = width * scaleX
        val h = height * scaleY

        val wOffset = ((width - w) / 2)
        val hOffset = ((height - h) / 2)

        return h + (hOffset * 2)
    }
}