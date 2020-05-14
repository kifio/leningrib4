package kifio.leningrib.model.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import java.util.*

abstract class WideAssetActor(region: TextureRegion, camera: OrthographicCamera) : StaticActor(region, camera) {

    init {

        val logoOffset = 40 * Gdx.graphics.density
        this.width = Gdx.graphics.width - (2 * logoOffset)
        this.height = (this.width / region.regionWidth) * region.regionHeight

        this.x = logoOffset
        this.y = (Gdx.graphics.height * 0.45f)
    }

}