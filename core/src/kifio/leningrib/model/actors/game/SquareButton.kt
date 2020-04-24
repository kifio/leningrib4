package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.screens.GameScreen

class SquareButton(
        private val pressedState: TextureRegion,
        private val unpressedState: TextureRegion,
        private val camera: Camera
) : StaticActor(unpressedState) {

    var onTouchHandler: (() -> Unit)? = null

    init {

        this.width = GameScreen.tileSize.toFloat()
        this.height = (this.width / region.regionWidth) * region.regionHeight

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
        val x = Gdx.graphics.width - (width + 16 * Gdx.graphics.density)
        val y = camera.position.y + (Gdx.graphics.height / 2f) - (height + 16 * Gdx.graphics.density)
        setBounds(x, y, width, height)
        batch.draw(region, x, y, width, height)
    }
}