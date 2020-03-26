package kifio.leningrib.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import kifio.leningrib.LGCGame
import kifio.leningrib.model.GameOverDisplay
import kifio.leningrib.model.speech.SpeechManager
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import java.util.*

class MenuScreen(game: LGCGame?) : InputAdapter(), Screen {

    private var game: LGCGame?

    private var stage: Stage
    private var camera: OrthographicCamera? = null

    private var renderer = ShapeRenderer()
    private var batch = SpriteBatch()

    private val backgroundColor = Color.valueOf("#449852")
    private val textColor = Color.valueOf("#FADB94")

    init {
        Gdx.input.inputProcessor = this
        this.game = game
        renderer.color = backgroundColor
        initCamera()
        stage = Stage(ScreenViewport(camera), batch)
        stage.addActor(getMenuLabel("НАЧАТЬ ИГРУ", 0))
    }

    // Инициализирует камеру ортгональную карте
    private fun initCamera() {
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height

        // create the camera and the batch
        camera = OrthographicCamera()
        camera!!.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    override fun render(delta: Float) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera!!.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        renderer.end()
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun dispose() {
        game = null
    }

    override fun resize(width: Int, height: Int) {}
    override fun show() {
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return super.touchDown(x, y, pointer, button)
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return if (game != null) {
            game!!.showGameScreen()
            true
        } else {
            super.touchUp(x, y, pointer, button)
        }
    }

    private fun getMenuLabel(text: String, order: Int): Label {
        val scale = 2f
        val w = SpeechManager.getInstance().getTextWidth(text) * scale
        val h = SpeechManager.getInstance().getTextHeight(text) * scale
        val x = Gdx.graphics.width / 2f - w / 2
        val y = Gdx.graphics.height / 2f - h / 2
        return SpeechManager.getInstance().getLabel(text, x, y, scale, w, textColor)
    }
}