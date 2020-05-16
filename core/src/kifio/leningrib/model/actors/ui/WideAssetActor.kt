package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kifio.leningrib.LUTController
import kifio.leningrib.model.actors.ui.StaticActor

abstract class WideAssetActor(region: TextureRegion,
                              camera: OrthographicCamera,
                              lutController: LUTController?) : StaticActor(region, camera, lutController) {

    init {

        val logoOffset = 40 * Gdx.graphics.density
        this.width = Gdx.graphics.width - (2 * logoOffset)
        this.height = (this.width / region.regionWidth) * region.regionHeight

        this.x = logoOffset
        this.y = (Gdx.graphics.height * 0.45f)
    }

}