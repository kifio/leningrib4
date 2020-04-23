package kifio.leningrib.model.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.OVERLAY

class Overlay(private val camera: Camera,
              private val offset: Float = 0F,
              region: TextureRegion = ResourcesManager.getRegion(OVERLAY)) : StaticActor(region) {

    init {
        x = offset
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.draw(region, x, camera.position.y - Gdx.graphics.height / 2f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }
}