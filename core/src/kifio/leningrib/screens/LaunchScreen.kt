package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.levels.LevelFabric
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.launch.LaunchProgressBar
import kifio.leningrib.model.actors.launch.LaunchScreenLogo
import kifio.leningrib.model.actors.launch.LaunchScreenTree
import model.LevelMap
import model.WorldMap

class LaunchScreen(game: LGCGame?, camera: OrthographicCamera,
                   private var config: Config?) :
        BaseScreen(game, camera),
        ResourcesManager.ResourceLoadingListener {

    private var accumulatedTime = 0f
    private var finished: Boolean = false

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

        if (accumulatedTime >= 2f && !finished) {
            finished = true
            LevelGenerationThread(config, this).start()
        } else if (accumulatedTime >= 1.5f) {
            ResourcesManager.initializeSpeeches()
            onProgressChanged(100)
        } else if (accumulatedTime >= 1f) {
            ResourcesManager.buildRegions()
            onProgressChanged(60)
        } else if (accumulatedTime >= 0.5) {
            ResourcesManager.loadAssets()
            onProgressChanged(30)
        }

        accumulatedTime += delta
        stage.act(delta)
        stage.draw()
    }

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {}

    override fun onProgressChanged(progress: Int) {
        for (actor in actors) {
            if (actor is LaunchProgressBar) {
                actor.setProgress(progress)
            }
        }
    }

    private fun onFirstLevelCreated(worldMap: WorldMap, levelMap: LevelMap) {
        Gdx.app.postRunnable {
            config = null
            game?.showGameScreen(worldMap, levelMap)
        }
    }

    private class LevelGenerationThread(
            private var config: Config?,
            private var launchScreen: LaunchScreen?
    ) : Thread() {

        override fun run() {
            super.run()
            config?.let {
                val worldMap = WorldMap()
                val levelMap = worldMap.addLevel(0, 0, it)

                launchScreen?.onFirstLevelCreated(worldMap, levelMap)
            }

            config = null
            launchScreen = null
        }
    }
}