package kifio.leningrib.levels

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.ForestersManager
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.actors.game.Mushroom
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.fixed.Grandma
import kifio.leningrib.model.pathfinding.ForestGraph
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
                               mushroomsCount: Int,
                               roomsRectangles: Array<Rectangle>): com.badlogic.gdx.utils.Array<Mushroom> {
        val mushrooms = com.badlogic.gdx.utils.Array<Mushroom>()
        val mushroomCoordinates = com.badlogic.gdx.utils.Array<Pair<Int, Int>>()

        val levelHeight = config.levelHeight
        val levelWidth = config.levelWidth

        var count = 0

        for (roomsRectangle in roomsRectangles) {
            val top = (roomsRectangle.y + roomsRectangle.height - 1).toInt()
            val bottom = roomsRectangle.y.toInt()

            var x: Int
            var y: Int

            val mushroomsLimit = (MIN + (mushroomsCount / 10) + ThreadLocalRandom.current().nextInt(2)).coerceAtMost(Int.MAX_VALUE)

            while (count < mushroomsLimit) {

                do {
                    x = GameScreen.tileSize * (1 + ThreadLocalRandom.current().nextInt(levelWidth - 2));
                    y = (GameScreen.tileSize * ThreadLocalRandom.current().nextInt(bottom, top))
                } while (
                        Utils.isOverlapsWithActors(treesManager.getObstacleTrees(), x, y)
                        || mushroomCoordinates.contains(Pair(x, y)));

                mushroomCoordinates.add(Pair(x, y))
                count++

                val foo = ThreadLocalRandom.current().nextInt(64)
                val hasEffect = foo % 4 == 0 && mushroomsCount > 5
                val movable = mushroomsCount > 5 && ThreadLocalRandom.current().nextBoolean()
                mushrooms.add(Mushroom(x, y, hasEffect, movable,
                        if (movable) getNeighbours(x.toFloat(), y.toFloat(), treesManager) else null))
            }

            count = 0
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
                               roomsRectangles: Array<Rectangle>,
                               forestGraph: ForestGraph): com.badlogic.gdx.utils.Array<Forester> {
        val gameObjects = com.badlogic.gdx.utils.Array<Forester>()
        for (roomsRectangle in roomsRectangles) {
            if (player == null || !Utils.isInRoom(roomsRectangle,
                            player.x / GameScreen.tileSize,
                            player.y / GameScreen.tileSize)) {

                val left = (roomsRectangle.x).toInt()
                val top = (roomsRectangle.y + roomsRectangle.height - 1).toInt()
                val right = (roomsRectangle.width).toInt()
                val bottom = roomsRectangle.y.toInt()
                val ltr = ThreadLocalRandom.current().nextBoolean()
                val originalFromX = (GameScreen.tileSize * if (ltr) left else right).toFloat()
                val originalFromY = (GameScreen.tileSize * ThreadLocalRandom.current().nextInt(bottom, top)).toFloat()

                val f = Forester(
                        originalFromX,
                        originalFromY,
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
            super.movePlayerTo(x, y, player, null)
            return
        }

        val px = player.onLevelMapX
        val py = player.onLevelMapY

        var tx = Utils.mapCoordinate(x).toInt()
        var ty = Utils.mapCoordinate(y).toInt()

        val gx = Utils.mapCoordinate(grandma.x).toInt()
        val gy = Utils.mapCoordinate(grandma.y).toInt()

        forestGraph?.let {
            if (gx == tx && gy == ty) {
                val nearest = it.findNearest(tx, ty, px, py)
                tx = nearest.x.toInt()
                ty = nearest.y.toInt()
                if (tx == px && ty == py) {
                    player.addAction(Actions.run(callback))
                } else {
                    super.movePlayerTo(tx.toFloat(), ty.toFloat(), player, callback)
                }
            } else {
                super.movePlayerTo(tx.toFloat(), ty.toFloat(), player, null)
            }
        }
    }

    companion object {
        private const val MIN = 1
        private const val MAX = 6
        const val LEVEL_HEIGHT = 48
    }
}