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

    private var selectedItemIndex: Int? = null
    private var items: List<StoreItem>? = null
    private var descriptionsHeights: List<Float>? = null

    private var buyLabel = "КУПИТЬ"
    private var buyLabelTexture: Texture = getTexture(GREEN_BG)
    private var buyLabelHeight = LabelManager.getInstance().getTextHeight(buyLabel, LabelManager.getInstance().mediumFont)
    private var buyLabelWidth = LabelManager.getInstance().getTextWidth(buyLabel, LabelManager.getInstance().mediumFont) * 1.4f
    private var buyLabelOffset = LabelManager.getInstance().getTextWidth(buyLabel, LabelManager.getInstance().mediumFont) * 0.2f

    private val overlay = getTexture(STORE_ITEM_BG)

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
                    val top = Gdx.graphics.height - topTextureHeight
                    selectedItemIndex = getTouchedItemIndex(x, top - y)
                    return true
                }

                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    val index = selectedItemIndex
                    val items = items
                    if (index != null && items != null) {
                        onTouchHandler?.invoke(items[index])
                        selectedItemIndex = null
                    }
                    touched = false
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

        val yImage = top - (imageSize + 3f * innerOffset) * (index + 1)
        val descriptionOffset = (imageSize - height)

        if (index == selectedItemIndex) {
            batch.draw(overlay,
                    0f,
                    yImage - innerOffset,
                    Gdx.graphics.width.toFloat(),
                    imageSize + 2 * innerOffset)
        }

        batch.draw(texture,
                xImage,
                yImage,
                imageSize,
                imageSize)

        LabelManager.getInstance().smallFont.draw(batch,
                item.description,
                textOffset,
                yImage + descriptionOffset,
                textWidth, Align.left, true)

        val buyY = yImage + (descriptionOffset - buyLabelHeight) / 2f

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

    private fun getTouchedItemIndex(x: Float, y: Float): Int? {
        val min = offset + innerOffset
        val max = Gdx.graphics.width - (offset + innerOffset)
        if (x <= min || x >= max) {
            return null
        }

        val index = (y / (3 * innerOffset + imageSize)).toInt()
        return items?.let {
            return if (index < it.size) index else null
        }
    }
}