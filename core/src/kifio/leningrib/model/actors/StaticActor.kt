package kifio.leningrib.model.actors

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import kifio.leningrib.LGCGame
import java.util.*

abstract class StaticActor(protected var region: TextureRegion?, protected var camera: OrthographicCamera) : Actor() {

    var touched = false

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.shader = null
        if (region != null) batch.draw(region, x, y, width, height)
        if (LGCGame.lutController.lutTexture != null) {
            batch.shader = LGCGame.lutController.shader
        }
    }
}