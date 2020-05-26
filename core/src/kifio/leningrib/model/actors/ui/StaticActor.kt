package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import kifio.leningrib.LUTController

abstract class StaticActor(protected var region: TextureRegion?,
                           protected var camera: OrthographicCamera,
                           protected val lutController: LUTController?) : Actor() {

    var touched = false

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.shader = null
        batch.draw(region, x, y, width, height)
        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }
}