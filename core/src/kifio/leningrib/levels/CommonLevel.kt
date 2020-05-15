package kifio.leningrib.levels

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.tutorial.Grandma
import kifio.leningrib.screens.GameScreen
import model.LevelMap
import java.util.concurrent.ThreadLocalRandom

class CommonLevel() : Level() {

    var grandma: Grandma? = null

    constructor(player: Player, grandma: Grandma?, levelMap: LevelMap) : this() {
        super.setup(player, grandma, levelMap, Config(LGCGame.LEVEL_WIDTH, LEVEL_HEIGHT))
        this.grandma = grandma
    }

    constructor(level: Level) : this() {
        super.copy(level)
        if (level is CommonLevel) {
            this.grandma = level.grandma
        }
    }

    override fun getLevelHeight() = LEVEL_HEIGHT


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
                val hasEffect = foo % 4 == 0 && mushroomsCount > 5
                val movable = mushroomsCount > 5 && ThreadLocalRandom.current().nextBoolean()
                mushrooms.add(Mushroom(x, y, hasEffect, movable,
                        if (movable) getNeighbours(x.toFloat(), y.toFloat(), treesManager) else null))
            }
            i += step
        }
        return mushrooms
    }

    private fun getNeighbours(x: Float, y: Float, treesManager: TreesManager): Array<Vector2?> {
        val arr = arrayOfNulls<Vector2>(4)
        arr[0] = treesManager.getFreeNeighbour(x - GameScreen.tileSize, y)
        arr[1] = treesManager.getFreeNeighbour(x + GameScreen.tileSize, y)
        arr[2] = treesManager.getFreeNeighbour(x, y - GameScreen.tileSize)
        arr[3] = treesManager.getFreeNeighbour(x, y + GameScreen.tileSize)
        return arr
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

    override fun movePlayerTo(x: Float, y: Float, player: Player, callback: Runnable?) {
        val grandma = this.grandma
        if (grandma == null) {
            super.movePlayerTo(x, y, player)
            return
        }

        val px = player.onLevelMapX
        val py = player.onLevelMapY

        val tx = Utils.mapCoordinate(x).toInt()
        val ty = Utils.mapCoordinate(y).toInt()

        val gx = Utils.mapCoordinate(grandma.x).toInt()
        val gy = Utils.mapCoordinate(grandma.y).toInt()

        forestGraph?.let {
            if (gx == tx && gy == ty) {
                val nearest = it.findNearest(tx, ty, px, py)
                super.movePlayerTo(nearest.x, nearest.y, player, callback)
            } else {
                super.movePlayerTo(tx.toFloat(), ty.toFloat(), player, null)
            }
        }
    }

    companion object {
        private const val MIN_STEP = 1
        private const val MAX_STEP = 4
        const val LEVEL_HEIGHT = 46
    }
}