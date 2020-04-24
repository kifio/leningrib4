package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.TreePart
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.launch.LaunchProgressBar
import kifio.leningrib.model.actors.launch.LaunchScreenLogo
import kifio.leningrib.model.actors.launch.LaunchScreenTree
import model.LevelMap
import model.WorldMap

class LaunchScreen(game: LGCGame) : BaseScreen(game) {

    private var accumulatedTime = 0f
    private var launchTime = 1f
    private var finished: Boolean = false

    // Data, which will be passed to game screen
    private var gameScreen: GameScreen? = null

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
                    Mushroom.Effect.DEXTERITY),

            Mushroom(GameScreen.tileSize,
                    GameScreen.tileSize * 2,
                    Mushroom.Effect.SPEED)
    )

    init {
        for (actor in actors) {
            if (actor is Mushroom || actor is LaunchScreenTree) { actor.setScale(0F) }
            stage.addActor(actor)
        }
    }

    override fun show() {
        ResourcesManager.loadAssets()
        LevelGenerationThread(this).start()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(49 / 255f, 129 / 255f, 54f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        for (actor in actors) {
            if (actor is LaunchProgressBar) {

                if (accumulatedTime >= 2f && gameScreen != null && !finished) {
                    game.showGameScreen(gameScreen)
                    this.gameScreen = null
                    finished = true
                }

                actor.setProgress((accumulatedTime / launchTime).coerceAtMost(1F))
            } else if (actor is Mushroom) {
                if (accumulatedTime > 0.5F) {
                    val scaleBasedOnTime = ((accumulatedTime - 0.5F) / launchTime).coerceAtMost(0.3F)
                    actor.setScale(5F * scaleBasedOnTime)
                }
            } else if (actor is LaunchScreenTree) {
                if (accumulatedTime > 0.5F) {
                    val scaleBasedOnTime = ((accumulatedTime - 0.5F) / launchTime).coerceAtMost(0.2F)
                    actor.setScale(5F * scaleBasedOnTime)
                }
            }
        }

        accumulatedTime += delta
        stage.act(delta)
        stage.draw()
    }

    private fun onFirstLevelCreated(worldMap: WorldMap, levelMap: LevelMap) {
        Gdx.app.postRunnable {
            val start = System.nanoTime()
            gameScreen = GameScreen(game, worldMap, levelMap)
            val finish = System.nanoTime()
            Gdx.app.log("kifio", "show game screen took: ${(finish - start) / 1_000_000}")
        }
    }

    private class LevelGenerationThread(
            private var launchScreen: LaunchScreen?
    ) : Thread() {

        override fun run() {
            super.run()
            ResourcesManager.buildRegions()
            ResourcesManager.initializeSpeeches()
            val worldMap = WorldMap()
            launchScreen?.onFirstLevelCreated(worldMap, worldMap.addLevel(0, 0,
                    Config(LGCGame.LEVEL_WIDTH, LGCGame.LEVEL_HEIGHT)))
            launchScreen = null
        }
    }
}