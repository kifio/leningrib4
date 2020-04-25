package kifio.leningrib.model.actors.game

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.model.actors.WideAssetActor
import kifio.leningrib.model.speech.LabelManager

class StartGameButton(
        private val offsetsCount: Int,
        private val title: String,
        private val camera: Camera,
        private val pressedState: TextureRegion,
        private val unpressedState: TextureRegion,
        private val labelColor: Color
) : WideAssetActor(unpressedState) {

    var onTouchHandler: (() -> Unit)? = null

    private val labelX: Float

    init {
        val labelWidth = LabelManager.getInstance().getTextWidth(title, LabelManager.getInstance().largeFont)
        labelX = x + (width - labelWidth) / 2

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
        y = camera.position.y - (height / 2) - ((0.75f * height) * offsetsCount)
        super.draw(batch, parentAlpha)
        LabelManager.getInstance().largeFont.color = labelColor
        LabelManager.getInstance().largeFont.draw(batch, title, labelX, this.y + (0.6f * this.height))
    }
}