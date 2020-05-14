package kifio.leningrib.model.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.OVERLAY

class Overlay(camera: OrthographicCamera, offset: Float = 0F,
              region: TextureRegion = ResourcesManager.getRegion(OVERLAY)) : StaticActor(region, camera) {

    init {
        x = offset
        width = Gdx.graphics.width - x
        height = Gdx.graphics.height.toFloat()
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touched = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                touched = false
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        y = camera.position.y - Gdx.graphics.height / 2f
        super.draw(batch, parentAlpha)
    }
}