package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.PixmapTextureData
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.launch.LaunchProgressBar
import kifio.leningrib.model.actors.launch.LaunchScreenLogo
import kifio.leningrib.model.actors.launch.LaunchScreenTree
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen.Companion.tileSize
import model.LevelMap
import model.WorldMap

class LaunchScreen(game: LGCGame) : BaseScreen(game) {

    private var accumulatedTime = 0f
    private var launchTime = 1f
    private var halfLaunchTime = launchTime / 2
    private var finished: Boolean = false

    // Data, which will be passed to game screen
    private var gameScreen: GameScreen? = null

    private val actors = arrayOf(
            LaunchScreenLogo(),
            LaunchProgressBar(),

            LaunchScreenTree(
                    tileSize.toFloat(),
                    Gdx.graphics.height - tileSize * 3F
            ),

            LaunchScreenTree(
                    Gdx.graphics.width - tileSize * 3F,
                    tileSize * 2F
            ),


            Mushroom(Gdx.graphics.width - tileSize * 3,
                    Gdx.graphics.height - (tileSize * 3.5f).toInt(),
                    Mushroom.Effect.DEXTERITY),

            Mushroom(tileSize,
                    tileSize * 2,
                    Mushroom.Effect.SPEED)
    )

    init {
        for (actor in actors) {
            if (actor is Mushroom || actor is LaunchScreenTree) {
                actor.setScale(0F)
            }
            stage.addActor(actor)
        }
    }

    override fun show() {
        ResourcesManager.loadAssets()
        game.executor.submit {
            prepare()
        }
    }

    private fun prepare() {
        ResourcesManager.buildRegions()
        ResourcesManager.initializeSpeeches()
        val worldMap = WorldMap()
        val level: LevelMap
        level = if (LGCGame.isFirstLevelPassed()) {
            val config = Config(LGCGame.LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
            worldMap.addLevel(0, 0, config)
        } else {
            val firstRoomHeight = (Gdx.graphics.height / tileSize) - 2
            val config = Config(LGCGame.LEVEL_WIDTH, firstRoomHeight + 26)
            worldMap.addFirstLevel(config, firstRoomHeight)
        }
        Gdx.app.postRunnable {
            LabelManager.getInstance()
            gameScreen = GameScreen(game, level, worldMap)
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(49 / 255f, 129 / 255f, 54f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        for (actor in actors) {
            if (actor is LaunchProgressBar) {

                if (accumulatedTime >= launchTime && gameScreen != null && !finished) {
                    val start = System.nanoTime()
                    game.showGameScreen(gameScreen)
                    val finish = System.nanoTime()
                    Gdx.app.log("kifio", "Show game screen took: ${(finish - start) / 1_000_000}")
                    this.gameScreen = null
                    finished = true
                }

                actor.setProgress((accumulatedTime / launchTime).coerceAtMost(launchTime))
            } else if (actor is Mushroom) {
                if (accumulatedTime > halfLaunchTime) {
                    val scaleBasedOnTime = ((accumulatedTime - halfLaunchTime) / launchTime).coerceAtMost(0.3F)
                    actor.setScale(5F * scaleBasedOnTime)
                }
            } else if (actor is LaunchScreenTree) {
                if (accumulatedTime > halfLaunchTime) {
                    val scaleBasedOnTime = ((accumulatedTime - halfLaunchTime) / launchTime).coerceAtMost(0.2F)
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
        }
    }
}