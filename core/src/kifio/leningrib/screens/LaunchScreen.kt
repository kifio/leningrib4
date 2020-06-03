package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import kifio.leningrib.LGCGame
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.game.Mushroom
import kifio.leningrib.model.actors.launch.LaunchProgressBar
import kifio.leningrib.model.actors.launch.LaunchScreenLogo
import kifio.leningrib.model.actors.launch.LaunchScreenTree
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen.Companion.tileSize
import model.WorldMap

class LaunchScreen(game: LGCGame) : BaseScreen(game) {

    private var accumulatedTime = 0f
    private var launchTime = 2f
    private var halfLaunchTime = launchTime / 2
    private var finished: Boolean = false

    // Data, which will be passed to game screen
    private var gameScreen: GameScreen? = null

    private val actors = arrayOf(
            LaunchScreenLogo(camera),
            LaunchProgressBar(camera),

            LaunchScreenTree(
                    tileSize.toFloat(),
                    Gdx.graphics.height - tileSize * 3F,
                    camera
            ),

            LaunchScreenTree(
                    Gdx.graphics.width - tileSize * 3F,
                    tileSize * 2F,
                    camera
            ),


            Mushroom(Gdx.graphics.width - tileSize * 3,
                    Gdx.graphics.height - (tileSize * 3.5f).toInt(),
                    Mushroom.Effect.DEXTERITY),

            Mushroom(tileSize,
                    tileSize * 2,
                    Mushroom.Effect.SPEED)
    )

    override fun show() {
        super.show()
        for (actor in actors) {
            if (actor is Mushroom || actor is LaunchScreenTree) {
                actor.setScale(0F)
            }
            stage.addActor(actor)
        }
    }

    private var foo = false
    private var bar = false

    private var startTime: Long = 0L
    private var finishTime: Long = 0L

    override fun render(delta: Float) {
        stage.root.setOrigin(camera.position.x, camera.position.y)
        Gdx.gl.glClearColor(49 / 255f, 129 / 255f, 54f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (accumulatedTime >= 0.1f && !foo) {
            foo = true
            startTime = System.nanoTime()
            ResourcesManager.loadAssets()
        }

        if (ResourcesManager.isLoaded() && !bar) {
            bar = true
            finishTime = System.nanoTime()
            Gdx.app.log("kifio_time", "Loading assets took: ${(finishTime - startTime) / 1000000}")
            LabelManager.getInstance()

            executor.submit {
                startTime = System.nanoTime()
                ResourcesManager.buildRegions()
                ResourcesManager.initializeSpeeches()
                val worldMap = WorldMap()
                val levelAndPlayer = LGCGame.getLevelAndPlayer(worldMap)
                val level = levelAndPlayer.second
                val player = levelAndPlayer.first
                Gdx.app.postRunnable {
                    this.gameScreen = GameScreen(game, level, player, worldMap)
                    finishTime = System.nanoTime()
                    Gdx.app.log("kifio_time", "Init first level took: ${(finishTime - startTime) / 1000000}")
                }
            }
        }

        for (actor in actors) {
            if (actor is LaunchProgressBar) {

                if (accumulatedTime >= launchTime && gameScreen != null && !finished) {
                    game.showGameScreen(gameScreen)
                    this.gameScreen = null
                    finished = true
                }

                actor.setProgress((accumulatedTime / launchTime).coerceAtMost(1f))
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
}