package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kifio.leningrib.LGCGame
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.launch.LaunchProgressBar
import kifio.leningrib.model.actors.launch.LaunchScreenLogo
import kifio.leningrib.model.actors.launch.LaunchScreenTree

class LaunchScreen(private var game: LGCGame?, camera: Camera) : InputAdapter(), Screen,
        ResourcesManager.ResourceLoadingListener {

    private val spriteBatch = SpriteBatch()
    private var stage: Stage = Stage(ScreenViewport(camera), spriteBatch)

    private val actors = arrayOf(
            LaunchScreenLogo(),
            LaunchProgressBar(),

            LaunchScreenTree(
                    GameScreen.tileSize.toFloat(),
                    Gdx.graphics.height - GameScreen.tileSize * 3F
            ),

            LaunchScreenTree(
                    Gdx.graphics.width - GameScreen.tileSize * 3F,
                    GameScreen.tileSize * 2F
            ),

            Mushroom(Gdx.graphics.width - GameScreen.tileSize * 3,
                    Gdx.graphics.height - (GameScreen.tileSize * 3.5f).toInt(),
                    Mushroom.Effect.DEXTERITY, 1.5f),

            Mushroom(GameScreen.tileSize,
                    GameScreen.tileSize * 2,
                    Mushroom.Effect.SPEED, 1.5f)
    )

    init {
        for (actor in actors) {
            stage.addActor(actor)
        }
    }

    override fun hide() {}

    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(49 / 255f, 129 / 255f, 54f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {}

    override fun dispose() {
        game = null
    }

    override fun onProgressChanged(progress: Int) {
        Gdx.app.log("kifio", "Loading progress: " + progress)
        Gdx.app.postRunnable {
            if (progress == 100) {
                game?.showGameScreen()
            } else {
                for (actor in actors) {
                    if (actor is LaunchProgressBar) {
                        actor.setProgress(progress)
                    }
                }
            }
        }
    }
}