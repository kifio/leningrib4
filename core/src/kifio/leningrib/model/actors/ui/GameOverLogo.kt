package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager

class GameOverLogo(camera: OrthographicCamera, lutController: LUTController
) : WideAssetActor(ResourcesManager.getRegion(ResourcesManager.GAME_OVER), camera, lutController) {


    init {
        region?.let { region ->
            this.width = Gdx.graphics.width * 0.6f
            this.height = (this.width / region.regionWidth) * region.regionHeight
            this.x = (Gdx.graphics.width - width) / 2f
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        this.y = (camera.position.y - Gdx.graphics.height / 2f) + (Gdx.graphics.height * 0.65f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.shader = null
        batch.draw(region, x, y, width, height)
        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }
}