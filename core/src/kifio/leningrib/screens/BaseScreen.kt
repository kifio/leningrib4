package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kifio.leningrib.LGCGame
import kifio.leningrib.levels.Level
import kifio.leningrib.levels.LevelFabric

abstract class BaseScreen(var game: LGCGame?,
                          var camera: OrthographicCamera?) : InputAdapter(), Screen {

    val spriteBatch = SpriteBatch()
    var stage: Stage = Stage(ScreenViewport(camera), spriteBatch)

    override fun dispose() {
        game = null
        camera = null
        stage.dispose()
        spriteBatch.dispose()
    }

    protected fun getCameraPositionY() = camera?.position?.y ?: Gdx.graphics.height / 2f
}