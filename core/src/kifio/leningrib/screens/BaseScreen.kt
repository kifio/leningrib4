package kifio.leningrib.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kifio.leningrib.LGCGame
import kifio.leningrib.model.actors.ShaderStage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseScreen(var game: LGCGame) : Screen {

    private val spriteBatch = SpriteBatch()
    protected val executor: ExecutorService = Executors.newSingleThreadExecutor()

    val camera: OrthographicCamera = OrthographicCamera()

    lateinit var stage: Stage

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    override fun show() {
        stage = if (this is GameScreen) {
            ShaderStage(ScreenViewport(camera), spriteBatch, this.lutController)
        } else {
            Stage(ScreenViewport(camera), spriteBatch)
        }
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}

    override fun dispose() {
        stage.dispose()
        spriteBatch.dispose()
    }

    open fun getTransitionActor(): Group = stage.root

    protected fun isStageInitialized() = ::stage.isInitialized
}