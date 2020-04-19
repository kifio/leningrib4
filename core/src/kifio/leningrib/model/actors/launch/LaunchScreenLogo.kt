package kifio.leningrib.model.actors.launch

import com.badlogic.gdx.Gdx
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.screens.GameScreen

class LaunchScreenLogo() : StaticActor(ResourcesManager.getRegion(ResourcesManager.LENIN_GRIB)) {

    init {

        val logoOffset = 40 * Gdx.graphics.density
        this.width = Gdx.graphics.width - (2 * logoOffset)
        this.height = (this.width / region.regionWidth) * region.regionHeight

        this.x = logoOffset
        this.y = (Gdx.graphics.height * 0.45f)
    }
}