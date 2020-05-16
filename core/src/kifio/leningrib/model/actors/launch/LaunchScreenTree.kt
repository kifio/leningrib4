package kifio.leningrib.model.actors.launch

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.ui.StaticActor
import kifio.leningrib.screens.GameScreen

class LaunchScreenTree(x: Float, y: Float, camera: OrthographicCamera) : StaticActor(
        ResourcesManager.getRegion(ResourcesManager.LAUNCH_TREES), camera, null
) {

    private var regionScale = 1F

    init {
        this.x = x
        this.y = y
        this.width = GameScreen.tileSize * 2F
        this.height = GameScreen.tileSize * 2F
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val size: Float = width * regionScale
        batch.draw(region, x + (width - size) / 2, y, size, size)
    }

    override fun setScale(scaleXY: Float) {
        regionScale = scaleXY
    }
}