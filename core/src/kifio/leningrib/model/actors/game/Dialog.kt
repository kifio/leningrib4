package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen


class Dialog(
        private val camera: Camera
) : Actor() {

    private val instructions = arrayOf(
            "Нажимай на экран\nдля перемещения\nперсонажа",
            "Твоя задача:\nсобрать как можно больше грибов",
            "Разные грибы\nимеют разные эффекты",
            "В лесу будут те,\nкто захочет тебя остановить"
    )

    var onTouchHandler: (() -> Unit)? = null

    private val labelColor = Color.BLACK

    private var labelX: Float = 0f
    private var labelY: Float = 0f

    private var index = 0
//    private var background: Pixmap? = null
    private var backgroundTexture: Texture? = null

    init {

        this.width = Gdx.graphics.width - (2f * GameScreen.tileSize)
        this.x = GameScreen.tileSize.toFloat()

        setTextPosition(instructions[index])
        setBackground()

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                index += 1
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {

                onTouchHandler?.invoke()
            }
        })
    }

    private fun setTextPosition(text: String) {
        val lm = LabelManager.getInstance()
        val w = lm.getTextWidth(text, lm.largeFont)
        val h = lm.getTextHeight(text, lm.largeFont)

        height = h + 1.5f * GameScreen.tileSize
        labelX = this.x + (this.width - w) / 2
        labelY = (Gdx.graphics.height / 2 + h)
    }

    private fun setBackground() {
        backgroundTexture?.dispose()
        val background = Pixmap(width.toInt(), height.toInt(), Pixmap.Format.RGB565)
        background.setColor(Color.WHITE)
        background.fillRectangle(0, 0, width.toInt(), height.toInt())
        background.setColor(Color.BLACK)
        background.drawRectangle(0, 0, width.toInt(), height.toInt())
        backgroundTexture = Texture(background, false)
        background.dispose()
    }

    // TODO: Animate fade for changing text
    override fun draw(batch: Batch, parentAlpha: Float) {
        y = camera.position.y
        super.draw(batch, parentAlpha)
        batch.draw(backgroundTexture, x, y);
        LabelManager.getInstance().largeFont.color = labelColor
        LabelManager.getInstance().largeFont.draw(batch, instructions[index], labelX, this.y + (0.6f * this.height))
    }
}