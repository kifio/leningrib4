package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.HUD_BOTTLE
import kifio.leningrib.model.ResourcesManager.SETTINGS_BACKGROUND
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.platform.StoreInterface
import kifio.leningrib.platform.items.StoreItem
import kifio.leningrib.screens.GameScreen

class StoreActor(camera: OrthographicCamera,
                 lutController: LUTController,
                 private val store: StoreInterface
) : Overlay(camera, 40 * Gdx.graphics.density, lutController, ResourcesManager.getRegion(SETTINGS_BACKGROUND)) {

    companion object {
        private const val DESCRIPTION = "List of in app purchases"
    }

    var onTouchHandler: (() -> Unit)? = null

    private val innerOffset = 16 * Gdx.graphics.density
    private val imageSize = GameScreen.tileSize * 2f

    private val textOffset = offset + (innerOffset * 2f) + imageSize
    private val textWidth = Gdx.graphics.width - (offset + (innerOffset * 3f) + imageSize)

    private var items: List<StoreItem>? = null

    init {
        if (items == null) {
            store.loadPurchases {
                this.items = it
            }
        }

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touched = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                val item = getTouchedItem(x, Gdx.graphics.height - y)
                if (item != null) {
                    onTouchHandler?.invoke()
                    touched = false
                }
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        y = camera.position.y - Gdx.graphics.height / 2f
        batch.shader = null
        if (region != null) batch.draw(region, x, y, width, height)

        items?.let {
            for (index in it.indices) {
                drawItem(batch, index, it[index])
            }
        }

        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }

    private fun drawItem(batch: Batch, index: Int, item: StoreItem) {
        val texture = getTexture(item.id)
        val xImage = offset + innerOffset

        batch.draw(texture,
                xImage,
                (height - imageSize - 2 * innerOffset) + (imageSize * index),
                imageSize,
                imageSize)

        LabelManager.getInstance().smallFont.draw(batch,
                item.price,
                xImage,
                (height - imageSize - 3 * innerOffset) + (imageSize * index),
                imageSize, Align.center, true)

        LabelManager.getInstance().smallFont.draw(batch,
                item.description,
                textOffset,
                (height - 2 * innerOffset - (imageSize / 2)) + (imageSize * index),
                textWidth, Align.left, true)
    }

    private fun getTexture(id: Int): Texture {
        return ResourcesManager.getTexture(HUD_BOTTLE)
    }

    private fun getTouchedItem(x: Float, y: Float): StoreItem? {
        val index = (y / (innerOffset + imageSize)).toInt()
        return items?.let {
            if (index < it.size) return it[index]
            else return null
        }
    }
}