package kifio.leningrib.levels

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.LGCGame.Companion.lastKnownCameraPosition
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.screens.GameScreen
import model.LevelMap
import java.util.concurrent.ThreadLocalRandom

class CommonLevel() : Level() {

    constructor(player: Player, levelMap: LevelMap) : this() {
        super.setup(player, levelMap, Config(LGCGame.LEVEL_WIDTH, LEVEL_HEIGHT))
    }

    constructor(level: Level) : this() {
        super.copy(level)
    }

    override fun getLevelHeight() = LEVEL_HEIGHT

    override fun updateCamera(camera: OrthographicCamera, player: Player) {
        super.updateCamera(camera, player)
        camera.position.y = player.y.coerceAtLeast(lastKnownCameraPosition)
        camera.update()
    }

    override fun initMushrooms(config: Config,
                               treesManager: TreesManager,
                               mushroomsCount: Int): com.badlogic.gdx.utils.Array<Mushroom> {
        val mushrooms = com.badlogic.gdx.utils.Array<Mushroom>()
        val levelHeight = config.levelHeight
        val levelWidth = config.levelWidth
        val step = MIN_STEP.coerceAtLeast(MAX_STEP - mushroomsCount / 10)
        var i = 1
        while (i < levelHeight - 1) {
            val x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2))
            val y = GameScreen.tileSize * (i + getLevelHeight() * nextLevel)
            if (!Utils.isOverlapsWithActors(treesManager.getInnerBordersTrees(), x, y)) {
                val foo = ThreadLocalRandom.current().nextInt(64)
                val hasEffect = foo % 4 == 0
                mushrooms.add(Mushroom(x, y, hasEffect))
            }
            i += step
        }
        return mushrooms
    }

    override fun initForesters(levelMap: LevelMap,
                               config: Config,
                               player: Player?,
                               roomsRectangles: Array<Rectangle>): com.badlogic.gdx.utils.Array<Forester> {
        val gameObjects = com.badlogic.gdx.utils.Array<Forester>()
        for (roomsRectangle in roomsRectangles) {
            if (player == null || !Utils.isInRoom(roomsRectangle,
                            player.x / GameScreen.tileSize,
                            player.y / GameScreen.tileSize)) {
                val left = (roomsRectangle.x + 1).toInt()
                val top = (roomsRectangle.y + roomsRectangle.height - 2).toInt()
                val right = (roomsRectangle.width - 2).toInt()
                val bottom = roomsRectangle.y.toInt()
                val originalFromY = ThreadLocalRandom.current().nextInt(bottom, top)
                val ltr = ThreadLocalRandom.current().nextBoolean()
                val f = Forester(
                        (GameScreen.tileSize * if (ltr) left else right).toFloat(),
                        (GameScreen.tileSize * originalFromY).toFloat(),
                        (GameScreen.tileSize * if (ltr) right else left).toFloat(),
                        ThreadLocalRandom.current().nextInt(1, 4),
                        GameScreen.tileSize * bottom,
                        GameScreen.tileSize * top,
                        GameScreen.tileSize,
                        GameScreen.tileSize * (config.levelWidth - 2))
                gameObjects.add(f)
            }
        }
        return gameObjects
    }

    override fun update(delta: Float, camera: OrthographicCamera, gameScreen: GameScreen) {
        accumulatedTime += delta
        if (gameScreen.player.mushroomsCount > 0) {
            LGCGame.lutController.updateLut(accumulatedTime);
        }
        super.update(delta, camera, gameScreen)
    }

    companion object {
        private const val MIN_STEP = 1
        private const val MAX_STEP = 4
        const val LEVEL_HEIGHT = 46
    }
}