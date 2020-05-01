package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen


class Dialog(private val camera: Camera) : StaticActor(null) {

    private val instructions = arrayOf(
            "Нажимай на экран\nдля перемещения\nперсонажа",
            "Твоя задача:\nсобрать как можно\nбольше грибов",
            "Разные грибы\nимеют разные\nэффекты",
            "В лесу будут те,\nкто захочет\nтебя остановить"
    )

    private val next = "Далее"

    var disposeHandler: (() -> Unit)? = null

    private val labelColor = Color.BLACK
    private val renderer = ShapeRenderer()
    private val batch = SpriteBatch()

    private var labelX: Float = 0f
    private var labelWidth: Float = 0f
    private var labelHeight: Float = 0f

    private var buttonX: Float = 0f
    private var buttonWidth: Float = 0f
    private var buttonHeight: Float = 0f

    private var index = 0

    private val animationTime = 0.3f

    private var hideAnimationStartTime = 0f
    private var accumulatedTime = 0f
    private var delay = 0.2f
    private val halfHeight = Gdx.graphics.height / 2

    init {

        this.width = Gdx.graphics.width - (2f * GameScreen.tileSize)
        this.x = GameScreen.tileSize.toFloat()

        measure(instructions[index])
        renderer.projectionMatrix = camera.combined
        this.y = camera.position.y + (Gdx.graphics.height / 2) + height

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

    private fun measure(text: String) {
        val lm = LabelManager.getInstance()
        labelWidth = lm.getTextWidth(text, lm.largeFont)
        labelHeight = lm.getTextHeight(text, lm.largeFont)

        buttonWidth = lm.getTextWidth(next, lm.largeFont)
        buttonHeight = lm.getTextHeight(next, lm.largeFont)

        height = labelHeight + buttonHeight + 1.5f * GameScreen.tileSize
        labelX = this.x + (this.width - labelWidth) / 2
        buttonX = this.x + this.width - buttonWidth - buttonWidth / 4
    }

    // TODO: Animate fade for changing text
    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        Gdx.gl.glLineWidth(Gdx.graphics.density * 4)

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.WHITE
        renderer.rect(x, y, width, height)
        renderer.end()

        renderer.begin(ShapeRenderer.ShapeType.Line)
        renderer.color = Color.BLACK
        renderer.rect(x, y, width, height)
        renderer.end()

        if (accumulatedTime > animationTime + delay) {

            if (index == instructions.size) {
                hideAnimationStartTime = accumulatedTime
                index--
            }

            if (hideAnimationStartTime > 0) {
                y = camera.position.y + (halfHeight * (accumulatedTime - hideAnimationStartTime) / animationTime) - (labelHeight / 2)
                if (accumulatedTime > hideAnimationStartTime + animationTime) {
                    disposeHandler?.invoke()
                }
            }

            this.batch.begin()

            LabelManager.getInstance().largeFont.color = labelColor

            LabelManager.getInstance().largeFont.draw(this.batch,
                    instructions[index.coerceAtMost(instructions.size - 1)],
                    labelX, this.y + buttonHeight + labelHeight)

            LabelManager.getInstance().largeFont.draw(this.batch,
                    next, buttonX, this.y)
            this.batch.end()

        } else if (accumulatedTime > delay) {
            y = camera.position.y + halfHeight - (halfHeight * (accumulatedTime - delay) / animationTime) - (labelHeight / 2)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        accumulatedTime += delta
    }
}