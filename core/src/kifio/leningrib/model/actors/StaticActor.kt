package kifio.leningrib.model.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import java.util.*

abstract class StaticActor(protected var region: TextureRegion?) : Actor() {

    var touched = false

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (region != null) batch.draw(region, x, y, width, height)
    }
}