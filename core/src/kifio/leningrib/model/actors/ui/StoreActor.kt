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
import kifio.leningrib.model.ResourcesManager.*
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.platform.StoreInterface
import kifio.leningrib.platform.items.StoreItem
import kifio.leningrib.screens.GameScreen

class StoreActor(camera: OrthographicCamera,
                 lutController: LUTController,
                 store: StoreInterface
) : Overlay(camera, 0f, lutController, getRegion(SETTINGS_BACKGROUND)) {

    var onTouchHandler: ((item: StoreItem?) -> Unit)? = null

    private val innerOffset = 16 * Gdx.graphics.density
    private val imageSize = GameScreen.tileSize * 2f

    private val textOffset = offset + (innerOffset * 2f) + imageSize
    private val textWidth = Gdx.graphics.width - (offset + (innerOffset * 3f) + imageSize)

    private val topTexture = getTexture(STORE_TOP)
    private val topTextureWidth: Float
    private val topTextureHeight: Float

    private var items: List<StoreItem>? = null
    private var descriptionsHeights: List<Float>? = null

    private var buyLabel = "КУПИТЬ"
    private var buyLabelTexture: Texture = ResourcesManager.getTexture(GREEN_BG)
    private var buyLabelHeight = LabelManager.getInstance().getTextHeight(buyLabel, LabelManager.getInstance().mediumFont)
    private var buyLabelWidth = LabelManager.getInstance().getTextWidth(buyLabel, LabelManager.getInstance().mediumFont) * 1.4f
    private var buyLabelOffset = LabelManager.getInstance().getTextWidth(buyLabel, LabelManager.getInstance().mediumFont) * 0.2f

    init {
        val scale = Gdx.graphics.width.toFloat() / topTexture.width.toFloat()
        topTextureWidth = topTexture.width * scale
        topTextureHeight = topTexture.height * scale
        if (items == null) {
            store.loadPurchases { items ->
                val lm = LabelManager.getInstance()
                this.items = items
                this.descriptionsHeights = items.map { item ->
                    lm.getTextHeight(item.description, lm.smallFont)
                }
            }

            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    touched = true
                    return true
                }

                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    val top = Gdx.graphics.height - topTextureHeight
                    val item = getTouchedItem(x, top - y)
                    if (item != null) {
                        onTouchHandler?.invoke(item)
                        touched = false
                    }
                }
            })
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        y = camera.position.y - Gdx.graphics.height / 2f

        val top = camera.position.y + (Gdx.graphics.height / 2f) - topTextureHeight

        batch.shader = null
        if (region != null) batch.draw(region, x, y, width, height)

        batch.draw(topTexture, offset, top, topTextureWidth, topTextureHeight)

        items?.let { items ->
            descriptionsHeights?.let { heights ->
                for (index in items.indices) {
                    drawItem(batch, index, items[index], heights[index], top)
                }
            }
        }

        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }

    private fun drawItem(batch: Batch,
                         index: Int,
                         item: StoreItem,
                         height: Float,
                         top: Float) {

        val texture = getTexture(item.id) ?: return
        val xImage = offset + innerOffset

        val imageY = top - (imageSize + 3f * innerOffset) * (index + 1)
        val descriptionOffset = (imageSize - height)

        batch.draw(texture,
                xImage,
                imageY,
                imageSize,
                imageSize)

        LabelManager.getInstance().smallFont.draw(batch,
                item.description,
                textOffset,
                imageY + descriptionOffset,
                textWidth, Align.left, true)

        val buyY = imageY + (descriptionOffset - buyLabelHeight) / 2f

        batch.draw(buyLabelTexture, textOffset + imageSize, buyY - (0.8f * GameScreen.tileSize), buyLabelWidth, GameScreen.tileSize.toFloat())

        LabelManager.getInstance().mediumFont.draw(batch,
                item.price,
                textOffset,
                buyY - (0.25f * GameScreen.tileSize),
                imageSize, Align.left, true)

        LabelManager.getInstance().mediumFont.draw(batch,
                buyLabel,
                textOffset + buyLabelOffset + imageSize,
                buyY - (0.25f * GameScreen.tileSize),
                textWidth, Align.left, true)
    }

    private fun getTexture(id: Int): Texture? {
        return when (id) {
            0 -> getTexture(VODKA_3)
            1 -> getTexture(VODKA_5)
            2 -> getTexture(GUM_1)
            3 -> getTexture(GUM_2)
            else -> null
        }
    }

    private fun getTouchedItem(x: Float, y: Float): StoreItem? {
        val min = offset + innerOffset
        val max = Gdx.graphics.width - (offset + innerOffset)
        if (x <= min || x >= max) {
            return null
        }

        val index = (y / (3 * innerOffset + imageSize)).toInt()
        return items?.let {
            if (index < it.size) return it[index]
            else return null
        }
    }
}