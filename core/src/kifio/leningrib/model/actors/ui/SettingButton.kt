package kifio.leningrib.model.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kifio.leningrib.LGCGame
import kifio.leningrib.LUTController
import kifio.leningrib.model.ResourcesManager.*
import kifio.leningrib.model.speech.LabelManager

class SettingButton(
        camera: OrthographicCamera,
        lutController: LUTController,
        private val game: LGCGame,
        private val index: Int,
        private val unpressedState: TextureRegion = getRegion(SETTING)
) : StaticActor(unpressedState, camera, lutController) {

    var onTouchHandler: (() -> Unit)? = null

    private val enabledIcon = getRegion(SETTING_ENABLED)
    private val enabledIconPressed = getRegion(SETTING_ENABLED_PRESSED)
    private val disabledIcon = getRegion(SETTING_DISABLED)
    private val disabledIconPressed = getRegion(SETTING_DISABLED_PRESSED)

    private var enabled: Boolean
    private var switchIcon: TextureRegion

    private val labelColor = Color(249 / 255f, 218 / 255f, 74f / 255f, 1f)

    private val labelWidth: Float
    private val labelHeight: Float

    private val iconWidth: Float
    private val iconHeight: Float

    private val iconX: Float
    private var iconY: Float
    private val labelX: Float

    init {
        region?.let { region ->
            val offset = 40 * Gdx.graphics.density
            this.width = Gdx.graphics.width - (3 * offset)
            this.height = (this.width / region.regionWidth) * region.regionHeight
            this.x = 2 * offset
        }

        enabled = game.isSettingEnabled(index)

        switchIcon = if (enabled) enabledIcon else disabledIcon

        iconWidth = this.height - (16 * Gdx.graphics.density)
        iconHeight = (iconWidth / enabledIcon.regionWidth) * enabledIcon.regionHeight

        labelWidth = this.width - iconWidth
        labelHeight = this.height

        iconY = this.y + 0.6f * (this.height - iconHeight)
        iconX = this.x + (this.width - iconWidth - 16 * Gdx.graphics.density)
        labelX = this.x + (16 * Gdx.graphics.density)

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touched = true
                if (isSwitchTouched(x, y)) {
                    getClickSound()?.play()
                    if (switchIcon == enabledIcon) {
                        switchIcon = enabledIconPressed
                    } else if (switchIcon == disabledIcon) {
                        switchIcon = disabledIconPressed
                    }
                    return true
                }
                return false
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                region = unpressedState
                touched = false
                enabled = !enabled

                switchIcon = if (enabled) {
                    enabledIcon
                } else {
                    disabledIcon
                }

                game.handleSetting(index, enabled)

                onTouchHandler?.invoke()
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        y = camera.position.y + when (index) {
            0 -> this.height
            1 -> -this.height / 2F
            else -> -2 * this.height
        }
        iconY = this.y + 0.6f * (this.height - iconHeight)
        batch.shader = null
        if (region != null) batch.draw(region, x, y, width, height)
        batch.draw(switchIcon, iconX, iconY, iconWidth, iconHeight)
        LabelManager.getInstance().common.large.color = labelColor
        LabelManager.getInstance().common.large.draw(batch, label, labelX, this.y + (0.6f * this.height))
        if (lutController?.lutTexture != null) {
            batch.shader = lutController.shader
        }
    }

    private fun isSwitchTouched(x: Float, y: Float): Boolean {
        val left = iconX
        val right = iconX + iconWidth
        val top = iconY
        val bottom = iconY - iconHeight

        val mappedX = x + this.x
        val mappedY = y + this.y - iconHeight

        return (mappedX in left..right && mappedY in bottom..top)
    }

    private val label = when (index) {
        0 -> "МУЗЫКА"
        1 -> "ЗВУКИ"
        else -> "ОБУЧЕНИЕ"
    }

}