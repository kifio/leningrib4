package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen

class LeaderboardButton(camera: OrthographicCamera,
                        lutController: LUTController?,
                        private val offset: Float,
                        targetHeight: Float) : StaticActor(ResourcesManager.getRegion(ResourcesManager.LEADERBOARD), camera, lutController) {

    private val label = "Таблица лидеров"

    val textWidth = LabelManager.getInstance().getTextWidth(
            label, LabelManager.getInstance().common.medium)

    val textHeight = LabelManager.getInstance().getTextHeight(
            label, LabelManager.getInstance().common.medium)

    var onTouchHandler: (() -> Unit)? = null

    init {

        val logoOffset = GameScreen.tileSize * 2f
        val w = (Gdx.graphics.width - (4 * GameScreen.tileSize))
        this.width = w.toFloat()
        this.height = targetHeight * 0.8f

        this.x = logoOffset
        this.y = (camera.position.y + offset) - ((0.9f * targetHeight) + 16 * Gdx.graphics.density)

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                ResourcesManager.getClickSound()?.play()
                region =  ResourcesManager.getRegion(ResourcesManager.LEADERBOARD_PRESSED)
                touched = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                region = ResourcesManager.getRegion(ResourcesManager.LEADERBOARD)
                touched = false
                onTouchHandler?.invoke()
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        val labelY = this.y + this.height - 0.5f * (this.height - textHeight)
        LabelManager.getInstance().common.medium.draw(batch,
                label,
                (Gdx.graphics.width - textWidth) / 2f,
                labelY,
                width,
                Align.left, false)
    }
}