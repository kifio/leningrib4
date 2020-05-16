package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager.RESULT_MUSHROOM
import kifio.leningrib.model.ResourcesManager.getRegion
import kifio.leningrib.model.actors.ui.StaticActor
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen

class MushroomsCountView(camera: OrthographicCamera, count: Int, lutController: LUTController
) : StaticActor(getRegion(RESULT_MUSHROOM), camera, lutController) {

    private val labelColor = Color.WHITE
    private val label = "х $count"

    private val labelX: Float
    private val labelYOffset: Float

    init {
        this.height = GameScreen.tileSize * 1f
        this.width = GameScreen.tileSize + LabelManager.getInstance().getTextWidth(label, LabelManager.getInstance().largeFont)

        this.x = (Gdx.graphics.width - this.width) / 2f
        labelX = this.x + GameScreen.tileSize * 1.5f

        val labelHeight = LabelManager.getInstance().getTextHeight(label, LabelManager.getInstance().largeFont)
        labelYOffset = 1.5f * labelHeight
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.projectionMatrix = camera.combined
        y = camera.position.y - 2 * GameScreen.tileSize
        batch.shader = null
        batch.draw(region, this.x, this.y, this.height, this.height)
        LabelManager.getInstance().largeFont.color = labelColor
        LabelManager.getInstance().largeFont.draw(batch, label, labelX, this.y + labelYOffset)
        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }
}