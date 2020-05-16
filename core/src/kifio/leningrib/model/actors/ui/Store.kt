package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.SETTINGS_BACKGROUND
import kifio.leningrib.model.items.StoreItem
import kifio.leningrib.model.speech.LabelManager

class Store(camera: OrthographicCamera,
            lutController: LUTController) : Overlay(camera, 40 * Gdx.graphics.density, lutController, ResourcesManager.getRegion(SETTINGS_BACKGROUND)) {

    companion object {
        private const val DESCRIPTION = "List of in app purchases"
    }

    private val font = LabelManager.getInstance().mediumFont
    private val labelWidth = LabelManager.getInstance().getTextWidth(DESCRIPTION, font)

    private val items = mutableListOf<StoreItem>()

    override fun draw(batch: Batch, parentAlpha: Float) {
        y = camera.position.y - Gdx.graphics.height / 2f
        super.draw(batch, parentAlpha)
        LabelManager.getInstance().smallFont.draw(batch, DESCRIPTION,
                offset + (width - labelWidth) / 2,
                this.y + 0.9f * height,
                labelWidth, Align.left, true)
    }
}