package kifio.leningrib.levels

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import generator.Config
import kifio.leningrib.LGCGame.Companion.getLevelHeight
import kifio.leningrib.LGCGame.Companion.getLevelWidth
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.Overlay
import kifio.leningrib.model.actors.game.*
import kifio.leningrib.screens.GameScreen
import model.LevelMap
import java.util.concurrent.ThreadLocalRandom

class FirstLevel(player: Player, levelMap: LevelMap?) : Level(player, levelMap) {

    var grandma: Grandma? = null
        private set
    var friend: Friend? = null
        private set

    override fun getActors(): com.badlogic.gdx.utils.Array<out Actor> {
        val arr = com.badlogic.gdx.utils.Array<Actor>()
        if (grandma == null) {
            var rectangle: Rectangle? = null
            val grandmaX = 5f
            val grandmaY = 20f
            for (r in roomsRectangles) {
                if (Utils.isInRoom(r, grandmaX, grandmaY)) {
                    rectangle = r
                    break
                }
            }
            grandma = Grandma(GameScreen.tileSize * grandmaX, GameScreen.tileSize * grandmaY, rectangle)
        }
        if (friend == null) friend = Friend(FRIEND_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())
        arr.add(grandma)
        arr.add(friend)
        arr.addAll(forestersManager.foresters)
        return arr
    }

    override fun initMushrooms(config: Config, treesManager: TreesManager, mushroomsCount: Int): com.badlogic.gdx.utils.Array<Mushroom> {
        val mushrooms = com.badlogic.gdx.utils.Array<Mushroom>()
        mushrooms.add(Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 20, false))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 12, false))
        return mushrooms
    }

    override fun initForesters(levelMap: LevelMap, config: Config, player: Player, roomRectangles: Array<Rectangle>): com.badlogic.gdx.utils.Array<Forester> {
        val foresters = com.badlogic.gdx.utils.Array<Forester>(1)
        foresters.add(Forester(
                (GameScreen.tileSize * 2).toFloat(),
                (GameScreen.tileSize * 24).toFloat(),
                (GameScreen.tileSize * (getLevelWidth() - 2)).toFloat(),
                ThreadLocalRandom.current().nextInt(1, 4),
                GameScreen.tileSize * 23,
                GameScreen.tileSize * (getLevelHeight() - 2),
                GameScreen.tileSize * 2,
                GameScreen.tileSize * (getLevelWidth() - 2)))
        return foresters
    }

    override fun update(delta: Float, gameScreen: GameScreen) {
        super.update(delta, gameScreen)
        friend!!.isPaused = gameScreen.isPaused()
        grandma!!.isPaused = gameScreen.isPaused()
        val x = gameScreen.player.x / GameScreen.tileSize
        val y = gameScreen.player.y / GameScreen.tileSize
        if (Utils.isInRoom(grandma!!.rectangle, x, y)) {
            if (grandma!!.player == null) {
                grandma!!.player = gameScreen.player
            }
        } else {
            grandma!!.player = null
        }
    }

    val grandmaLabel: Label
        get() = grandma!!.grandmaLabel

    fun moveToExit() {
        friend!!.moveToExit(forestGraph)
    }

    fun shouldStartDialogWithGrandma(): Boolean {
        return if (grandma!!.shouldStartDialog()) {
            grandma!!.stopTalking()
            true
        } else {
            false
        }
    }

    fun getFirstDialog(camera: OrthographicCamera,
                       disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "В моих беспокойных снах, я все чаще вижу этот лес..",
                "Лол, братан, чe ты несешь вообще? Мы с тобой сюда за грибами шторящими приехали.",
                "Только они не хранятся от слова совсем и их надо прямо на месте есть, иначе эффекта не будет.",
                "Ладно, ладно. Понял.\nА как их отличить?",
                "Да ты все подряд собирай, когда накроет, почувствуешь. Главное лесникам не попадайся.",
                "Эти алкаши кайфоломы еще те. Но за бутылку водки сделают вид, что тебя не видели.",
                "Где я им здесь водку возьму? Ты бы хоть сказал когда мы собирались!",
                "Да я хз. Забыл что-то. Ты иди нпчинай собирать там наверху за деревьями.\nА я в машине кое-что посмотрю и догоню.",
                "Слишком далеко только не уходи."
        )

        val characters = arrayOf(
                ResourcesManager.PLAYER_DIALOG_FACE,
                ResourcesManager.FRIEND_DIALOG_FACE,
                ResourcesManager.FRIEND_DIALOG_FACE,
                ResourcesManager.PLAYER_DIALOG_FACE,
                ResourcesManager.FRIEND_DIALOG_FACE,
                ResourcesManager.FRIEND_DIALOG_FACE,
                ResourcesManager.PLAYER_DIALOG_FACE,
                ResourcesManager.FRIEND_DIALOG_FACE,
                ResourcesManager.FRIEND_DIALOG_FACE
        )

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = disposeHandler
        }
    }

    fun getGrandmaDialog(camera: OrthographicCamera,
                       disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Ты гляди че делает!",
                "Грибы с земли ест!",
                "На хоть рот самогоном пополощи..",
                "Лесникам его не давай!",
                "Он их ума лишает.."
        )

        val characters = arrayOf(
                ResourcesManager.GRANDMA_DIALOG_FACE,
                ResourcesManager.GRANDMA_DIALOG_FACE,
                ResourcesManager.GRANDMA_DIALOG_FACE,
                ResourcesManager.GRANDMA_DIALOG_FACE,
                ResourcesManager.GRANDMA_DIALOG_FACE
        )

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = disposeHandler
        }
    }

    companion object {
        private val PLAYER_INITIAL_Y = GameScreen.tileSize * 8
        private val PLAYER_INITIAL_X = GameScreen.tileSize * 3
        private val FRIEND_INITIAL_X = GameScreen.tileSize * 6

        fun getPlayer() = Player(PLAYER_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())
    }
}