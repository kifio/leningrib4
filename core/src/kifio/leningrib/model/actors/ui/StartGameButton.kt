
package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager.getClickSound
import kifio.leningrib.model.speech.LabelManager

class StartGameButton(
        private val offsetsCount: Int,
        private val title: String,
        lutController: LUTController,
        camera: OrthographicCamera,
        private val pressedState: TextureRegion,
        private val unpressedState: TextureRegion,
        private val labelColor: Color
) : WideAssetActor(unpressedState, camera, lutController) {

    var onTouchHandler: (() -> Unit)? = null

    private val labelX: Float
    private var offset: Float = 0f

    init {
        val labelWidth = LabelManager.getInstance().getTextWidth(title, LabelManager.getInstance().largeFont)
        labelX = x + (width - labelWidth) / 2

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
        batch.shader = null
        y = camera.position.y - (height / 2) - ((0.75f * height) * offsetsCount)
        if (region != null) batch.draw(region, x, y, width, height)
        offset = if (touched) 0.5f else 0.6f
        LabelManager.getInstance().largeFont.color = labelColor
        LabelManager.getInstance().largeFont.draw(batch, title, labelX, this.y + (offset * this.height))

        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }
}