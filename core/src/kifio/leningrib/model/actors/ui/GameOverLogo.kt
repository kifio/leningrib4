package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager

class GameOverLogo(camera: OrthographicCamera, lutController: LUTController
) : WideAssetActor(ResourcesManager.getRegion(ResourcesManager.GAME_OVER), camera, lutController) {

    private val logoOffset = 40 * Gdx.graphics.density

    init {
        region?.let { region ->
            this.width = Gdx.graphics.width - (2 * logoOffset)
            this.height = (this.width / region.regionWidth) * region.regionHeight
            this.x = logoOffset
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        val w = ((Gdx.graphics.width) - width) / 2
        this.x = logoOffset + w
        this.y = (camera.position.y - Gdx.graphics.height / 2f) + (Gdx.graphics.height * 0.65f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.shader = null
        batch.draw(region, x, y, width * 0.7f, height * 0.7f)
        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }
}