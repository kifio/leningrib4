package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.*
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen

class MushroomsCountView(camera: OrthographicCamera,
                         count: Long,
                         max: Long,
                         lutController: LUTController
) : StaticActor(getRegion(RESULT_MUSHROOM), camera, lutController) {

    private val labelColor = Color.WHITE
    private val label = "х $count"
    private val bg = ResourcesManager.getRegion(GAME_OVER_RESULTS_OVERLAY)

    private val labelX: Float
    private val labelYOffset: Float
    private val tileSize = GameScreen.tileSize.toFloat()
    private  val lm = LabelManager.getInstance()
    private val maxCountLabel = "РЕКОРД: $max"
    private val maxWidth = lm.getTextWidth(maxCountLabel, lm.common.medium)

    init {
        this.height = tileSize * 3f
        this.width = tileSize + lm.getTextWidth(label, lm.common.large)

        this.x = (Gdx.graphics.width - this.width) / 2f
        labelX = this.x + tileSize * 1.5f

        val labelHeight = lm.getTextHeight(label, lm.common.large)
        labelYOffset = 1.5f * labelHeight
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.projectionMatrix = camera.combined
        y = camera.position.y - 2 * tileSize
        batch.shader = null
        batch.draw(bg, 0f, camera.position.y - 3f * tileSize, Gdx.graphics.width.toFloat(), tileSize * 4.5f)
        batch.draw(region, this.x, this.y + 2f * tileSize, tileSize, tileSize)
        val lm = lm

        lm.common.large.color = labelColor
        lm.common.large.draw(batch, label, labelX, (this.y + 2f * tileSize) + labelYOffset)

        lm.common.medium.draw(batch, maxCountLabel, (Gdx.graphics.width - maxWidth) / 2f, (this.y + 0.8f * tileSize) + labelYOffset)

        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }
}