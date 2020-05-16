package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.PLAYER_DIALOG_FACE
import kifio.leningrib.model.speech.LabelManager


class Dialog(camera: OrthographicCamera,
             lutController: LUTController,
             private val speeches: Array<String>,
             private val characters: Array<String>) : StaticActor(null, camera, lutController) {

    private val next = "Далее"
    private val ok = "Ок"

    var disposeHandler: (() -> Unit)? = null

    private val labelColor = Color(249 / 255f, 218 / 255f, 74f / 255f, 1f)
    private val bgColor = Color.valueOf("#3C3C3C")
    private val renderer = ShapeRenderer()

    private var labelX: Float = 0f
    private var labelOffset: Float = 0f
    private var labelWidth: Float = 0f

    private var buttonX: Float = 0f
    private var buttonWidth: Float = 0f
    private var buttonHeight: Float = 0f

    private var faceWidth: Float = 0f
    private var faceHeight: Float = 0f

    private var index = 0
    private var accumulatedTime = 0f
    private val halfHeight = (camera.viewportHeight * camera.zoom) / 2
    private val font = LabelManager.getInstance().mediumFont

    init {
        measure()
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touched = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                index += 1
                touched = false
            }
        })
    }

    private fun measure() {

        this.width = (camera.viewportWidth * camera.zoom) - 0.1f * camera.viewportWidth
        this.height = halfHeight / 2

        this.x = camera.position.x - this.width / 2
        this.y = camera.position.y - (height / 2)

        val lm = LabelManager.getInstance()

        buttonWidth = lm.getTextWidth(next, lm.mediumFont)
        buttonHeight = lm.getTextHeight(next, lm.mediumFont)

        if (characters.isEmpty()) {
            faceWidth = 0f
            faceHeight = this.height
        } else {
            val region = ResourcesManager.getRegion(PLAYER_DIALOG_FACE)
            val scale = height / region.regionHeight
            faceWidth = region.regionWidth * scale
            faceHeight = region.regionHeight * scale
        }

        buttonX = this.x + this.width - buttonWidth - buttonWidth / 4

        labelWidth = (this.width - faceWidth) * if (characters.isEmpty()) 0.8f else 0.6f
        labelOffset = labelWidth * 0.1f
        labelX = this.x + faceWidth + labelOffset
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        this.y = camera.position.y - (height / 2)
        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = bgColor
        renderer.rect(x, y, width, height)
        renderer.end()

        if (index == speeches.size) {
            disposeHandler?.invoke()
            index--
        }

        val index = index.coerceAtMost(speeches.size - 1)
        val shader = batch.shader
        batch.shader = null
        batch.projectionMatrix = camera.combined

        if (characters.isNotEmpty()) {
            val region = ResourcesManager.getRegion(characters[index])
            batch.draw(region,
                    this.x + 0.25f * faceWidth,
                    this.y + 0.25f * faceHeight,
                    faceWidth / 2,
                    faceHeight / 2)
        }

        font.color = labelColor
        font.draw(batch,
                speeches[index],
                labelX,
                this.y + 0.9f * height,
                labelWidth, Align.left, true)

        font.draw(batch,
                if (index == speeches.size - 1) ok else next,
                buttonX,
                (this.y + 2f * buttonHeight),
                buttonWidth,
                Align.right, false)
        batch.shader = shader
    }

    override fun act(delta: Float) {
        super.act(delta)
        accumulatedTime += delta
    }
}