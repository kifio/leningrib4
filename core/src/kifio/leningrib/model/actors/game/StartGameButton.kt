package kifio.leningrib.model.actors.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.model.actors.WideAssetActor

class StartGameButton(
        private val pressedState: TextureRegion,
        private val unpressedState: TextureRegion
) : WideAssetActor(unpressedState) {

    init {
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                region = pressedState
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                region = unpressedState
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }
}