package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import kifio.leningrib.LGCGame
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.PLAYER_DIALOG_FACE
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen


class Dialog(private val camera: OrthographicCamera,
             private val speeches: Array<String>,
             private val characters: Array<String>) : StaticActor(null) {

    private val next = "Далее"

    var disposeHandler: (() -> Unit)? = null

    private val labelColor = Color(249 / 255f, 218 / 255f, 74f / 255f, 1f)
    private val bgColor = Color.valueOf("#3C3C3C")
    private val renderer = ShapeRenderer()
    private val batch = SpriteBatch()

    private var labelX: Float = 0f
    private var labelOffset: Float = 0f
    private var labelWidth: Float = 0f

    private var buttonX: Float = 0f
    private var buttonWidth: Float = 0f
    private var buttonHeight: Float = 0f

    private var faceWidth: Float = 0f
    private var faceHeight: Float = 0f

    private var index = 0
    private var hideAnimationStartTime = 0f
    private var accumulatedTime = 0f
    private var delay = 0.2f
    private val halfHeight = (camera.viewportHeight * camera.zoom) / 2


    init {
        renderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        measure()
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touched = true
                index += 1
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                touched = false
            }
        })
    }

    private fun measure() {

        this.width = (camera.viewportWidth * camera.zoom)
        this.height = halfHeight / 2

        this.x = camera.position.x - this.width / 2
        this.y = camera.position.y - halfHeight - height

        val lm = LabelManager.getInstance()

        buttonWidth = lm.getTextWidth(next, lm.mediumFont)
        buttonHeight = lm.getTextHeight(next, lm.mediumFont)

        val region = ResourcesManager.getRegion(PLAYER_DIALOG_FACE)
        val scale = height / region.regionHeight
        faceWidth = region.regionWidth * scale
        faceHeight = region.regionHeight * scale

        buttonX = this.x + this.width - buttonWidth - buttonWidth / 4

        labelWidth = (this.width - faceWidth) * 0.6f
        labelOffset = labelWidth * 0.1f
        labelX = this.x + faceWidth + labelOffset

    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = bgColor
        renderer.rect(x, y, width, height)

        renderer.color = Color.RED
        renderer.rect(this.x , this.y, faceWidth, faceHeight)
        renderer.end()

        if (accumulatedTime > LGCGame.ANIMATION_DURATION + delay) {

            if (index == speeches.size) {
                hideAnimationStartTime = accumulatedTime
                index--
            }

            if (hideAnimationStartTime > 0) {
                y = (camera.position.y - halfHeight) - (height * (accumulatedTime - hideAnimationStartTime) / LGCGame.ANIMATION_DURATION)
                if (accumulatedTime > hideAnimationStartTime + LGCGame.ANIMATION_DURATION) {
                    disposeHandler?.invoke()
                }
            }

            val index = index.coerceAtMost(speeches.size - 1)
            val region = ResourcesManager.getRegion(characters[index])

            this.batch.begin()

            this.batch.draw(region,
                    this.x + 0.25f * faceWidth,
                    this.y + 0.25f * faceHeight,
                    faceWidth / 2,
                    faceHeight / 2)

            LabelManager.getInstance().mediumFont.color = labelColor
            LabelManager.getInstance().mediumFont.draw(this.batch,
                    speeches[index],
                    labelX,
                    this.y + 0.9f * height,
                    labelWidth, Align.left, true)

            LabelManager.getInstance().mediumFont.draw(this.batch,
                    next, buttonX, (this.y - 2 * this.height))

            this.batch.end()

        } else if (accumulatedTime > delay) {
            y = (camera.position.y - halfHeight - height) + (height * (accumulatedTime - delay) / LGCGame.ANIMATION_DURATION)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        accumulatedTime += delta
    }
}