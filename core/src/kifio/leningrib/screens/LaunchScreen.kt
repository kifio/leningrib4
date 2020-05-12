package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.PixmapTextureData
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.levels.FirstLevel
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.launch.LaunchProgressBar
import kifio.leningrib.model.actors.launch.LaunchScreenLogo
import kifio.leningrib.model.actors.launch.LaunchScreenTree
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen.Companion.tileSize
import model.LevelMap
import model.WorldMap
import java.util.concurrent.ThreadLocalRandom

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

    }

    private fun prepare() {

    }

    private var foo = false
    private var bar = false

    override fun render(delta: Float) {
        stage.root.setOrigin(game.camera.position.x, game.camera.position.y)
        Gdx.gl.glClearColor(49 / 255f, 129 / 255f, 54f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (accumulatedTime >= 0.1f && !foo) {
            foo = true
            ResourcesManager.loadAssets()
        }

        if (ResourcesManager.isLoaded() && !bar) {
            bar = true
            LabelManager.getInstance()

            game.executor.submit {
                ResourcesManager.buildRegions()
                ResourcesManager.initializeSpeeches()

                val worldMap = WorldMap()
                val levelMap: LevelMap
                val level: Level
                val player: Player

                if (LGCGame.isFirstLevelPassed()) {
                    val config = Config(LGCGame.LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
                    levelMap = worldMap.addLevel(0, 0, config)
                    val room = levelMap.rooms[0]
                    val x = ThreadLocalRandom.current().nextInt(2, LGCGame.LEVEL_WIDTH - 2).toFloat()
                    val y = ThreadLocalRandom.current().nextInt(room.y + 1, room.y + room.height - 2).toFloat()
                    player = Player(x * tileSize, y * tileSize)
                    level = CommonLevel(player, levelMap)
                } else {
                    val firstRoomHeight = (Gdx.graphics.height / tileSize) - 2
                    val config = Config(LGCGame.LEVEL_WIDTH, firstRoomHeight + 26)
                    levelMap = worldMap.addFirstLevel(config, firstRoomHeight)
                    player = FirstLevel.getPlayer()
                    level = FirstLevel(player, levelMap)
                }

                Gdx.app.postRunnable {
                    val start = System.nanoTime()
                    this.gameScreen = GameScreen(game, level, player, worldMap)
                    Gdx.app.log("kifio_time", "create gameScreen: ${(System.nanoTime() - start) / 1000000}")
                }
            }
        }

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



        if (finished) {

        }
    }
}