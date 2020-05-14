package kifio.leningrib.model.actors.launch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import kifio.leningrib.Utils
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.model.actors.WideAssetActor
import kifio.leningrib.screens.GameScreen

class LaunchScreenLogo(camera: OrthographicCamera) : WideAssetActor(ResourcesManager.getRegion(ResourcesManager.LENIN_GRIB), camera) {

    init {
        region?.let { region ->
            val logoOffset = 55 * Gdx.graphics.density
            this.width = Gdx.graphics.width - (2 * logoOffset)
            this.height = (this.width / region.regionWidth) * region.regionHeight

            this.x = logoOffset
            this.y = (Gdx.graphics.height * 0.5f)
        }
    }
}