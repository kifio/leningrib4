package kifio.leningrib.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kifio.leningrib.LGCGame

abstract class BaseScreen(var game: LGCGame) : Screen {

    val spriteBatch = SpriteBatch()
    var stage: Stage = Stage(ScreenViewport(game.camera), spriteBatch)

    override fun resize(width: Int, height: Int) {}
    override fun show() {}
    override fun hide() {}
    override fun pause() {}
    override fun resume() {}

    override fun dispose() {
        stage.dispose()
        spriteBatch.dispose()
    }
}