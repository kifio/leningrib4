package kifio.leningrib.levels

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.LUTController
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Mushroom
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.ui.Dialog
import kifio.leningrib.model.pathfinding.ForestGraph
import kifio.leningrib.screens.GameScreen
import model.LevelMap
import java.util.concurrent.ThreadLocalRandom

class CommonLevel() : Level() {

    constructor(player: Player,levelMap: LevelMap) : this() {
        super.setup(player, levelMap, Config(LGCGame.LEVEL_WIDTH, LEVEL_HEIGHT))
    }

    constructor(level: Level) : this() {
        super.copy(level)
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
            val max = ((top - bottom) * (levelWidth - 2))

            val mushroomsLimit = (MIN + (mushroomsCount / 10) + ThreadLocalRandom.current().nextInt(2)).coerceAtMost(max)

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

    fun showTutorialIfNeeded(camera: OrthographicCamera,
                             lutController: LUTController,
                             stage: Stage,
                             game: LGCGame,
                             callback: () -> Unit)  {

        if (game.wasTutorialShown()) return

        val speeches = arrayOf(
                "СОБЕРИ КАК МОЖНО БОЛЬШЕ ГРИБОВ И НЕ ДАЙ ЛЕСНИКАМ СЕБЯ ПОЙМАТЬ!",
                "Используй стрелки на клавиатуре, чтобы перемещать персонажа.",
                "В трудной ситуации жми SPACE, чтобы отвлечь лесников водкой, но помни что у тебя лишь две бутылки.")

        stage.addAction(Actions.delay(0.5f,
                Actions.run {
                    stage.addActor(Dialog(camera, lutController, speeches, arrayOf("Да я понял все", "Ок", "Да"), Array(speeches.size) { i -> ResourcesManager.GRANDMA_DIALOG_FACE }).apply {
                        this.disposeHandler = {
                            remove()
                            callback.invoke()
                            game.setTutorialWasShown()
                        }
                    })
                }))
    }

    companion object {
        private const val MIN = 1
        const val LEVEL_HEIGHT = 48
    }
}