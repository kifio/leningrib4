package kifio.leningrib.levels

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import generator.Config
import kifio.leningrib.LGCGame.Companion.getLevelWidth
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Dialog
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.tutorial.Friend
import kifio.leningrib.model.actors.tutorial.Sign
import kifio.leningrib.screens.GameScreen
import model.LevelMap
import java.util.concurrent.ThreadLocalRandom

class FirstLevel(player: Player, levelMap: LevelMap?) : Level(player, levelMap) {

    var friend: Friend? = null
        private set

    var signs: com.badlogic.gdx.utils.Array<Sign>? = null

    override fun getActors(): com.badlogic.gdx.utils.Array<out Actor> {
        val arr = com.badlogic.gdx.utils.Array<Actor>()

        if (friend == null) {
            friend = Friend(FRIEND_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())
        }

        if (signs == null) {
            signs = com.badlogic.gdx.utils.Array<Sign>()
            signs?.add(Sign(GameScreen.tileSize * 7f, GameScreen.tileSize * 7f, null))
            signs?.add(Sign(GameScreen.tileSize * 5f, GameScreen.tileSize * 12f, null))
            signs?.add(Sign(GameScreen.tileSize * 5f, GameScreen.tileSize * 20f, roomsRectangles[2]))
            signs?.add(Sign(GameScreen.tileSize * 2f, GameScreen.tileSize * 32f, roomsRectangles[3]))
        }

        arr.add(friend)
        arr.addAll(signs)
        arr.addAll(forestersManager.foresters)
        return arr
    }

    override fun initMushrooms(config: Config, treesManager: TreesManager, mushroomsCount: Int): com.badlogic.gdx.utils.Array<Mushroom> {
        val mushrooms = com.badlogic.gdx.utils.Array<Mushroom>()
//        mushrooms.add(Mushroom(GameScreen.tileSize * 8, GameScreen.tileSize * 8, false))
//        mushrooms.add(Mushroom(GameScreen.tileSize * 8, GameScreen.tileSize * 11, false))
//        mushrooms.add(Mushroom(GameScreen.tileSize * 6, GameScreen.tileSize * 14, false))
//        mushrooms.add(Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * 14, false))
//        mushrooms.add(Mushroom(GameScreen.tileSize * 1, GameScreen.tileSize * 17, false))
        mushrooms.add(Mushroom(GameScreen.tileSize * 4, GameScreen.tileSize * 12, Mushroom.Effect.DEXTERITY, Float.POSITIVE_INFINITY))
        mushrooms.add(Mushroom(GameScreen.tileSize * 4, GameScreen.tileSize * 5, false))
        return mushrooms
    }

    override fun initForesters(levelMap: LevelMap, config: Config, player: Player, roomRectangles: Array<Rectangle>): com.badlogic.gdx.utils.Array<Forester> {
        val foresters = com.badlogic.gdx.utils.Array<Forester>(1)
        val bottom = roomRectangles[3].y
        val top = roomRectangles[3].y + roomRectangles[3].height

        foresters.add(Forester(
                (GameScreen.tileSize * 2).toFloat(),
                (GameScreen.tileSize * 18).toFloat(),
                (GameScreen.tileSize * (getLevelWidth() - 2)).toFloat(),
                ThreadLocalRandom.current().nextInt(1, 4),
                GameScreen.tileSize * bottom.toInt(),
                GameScreen.tileSize * (top.toInt() - 2),
                GameScreen.tileSize * 2,
                GameScreen.tileSize * (getLevelWidth() - 2),
                GameScreen.tileSize * 1.5f,
                Forester.NOTICE_AREA_SIZE_SMALL,
                Forester.PURSUE_AREA_SIZE_SMALL))
        return foresters
    }

    override fun movePlayerTo(x: Float, y: Float, player: Player?) {
        val signs: com.badlogic.gdx.utils.Array<Sign> = this.signs ?: return
        for (s in signs) {
            if (s.bounds.contains(x, y)) {
                super.movePlayerTo(s.x, s.y - GameScreen.tileSize, player)
                return
            }
        }
        super.movePlayerTo(x, y, player)
    }

    override fun update(delta: Float, gameScreen: GameScreen) {
        super.update(delta, gameScreen)
        friend?.isPaused = gameScreen.isPaused()

        val x = Utils.mapCoordinate(gameScreen.player.x);
        val y = Utils.mapCoordinate(gameScreen.player.y);

        signs?.let {

            for (i in 0 until it.size) {
                val sign = it.get(i)
                if (sign.shouldShowDialog(x, y)) {
                    gameScreen.showSignTutorial(i);
                } else if (sign.shouldHideDialog(x, y)) {
                    gameScreen.hideSignTutorial();
                } else if (i == 2 && sign.room.contains(
                                gameScreen.player.x / GameScreen.tileSize,
                                gameScreen.player.y / GameScreen.tileSize
                        )) {
                    gameScreen.player.clearEffect()
                }
            }
        }

    }

    fun moveToExit() {
        friend?.moveToExit(forestGraph)
    }

    fun getFirstDialog(camera: OrthographicCamera,
                       disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "В моих беспокойных снах, я все чаще вижу этот лес..",
                "Лол, братан, чe ты несешь вообще? Мы с тобой сюда за грибами шторящими приехали.",
                "Только они не хранятся от слова совсем и их надо прямо на месте есть, иначе эффекта не будет.",
                "Ладно, ладно. Понял.\nА как их отличить?",
                "Ты не стремайся, все подряд ешь. Когда накроет, почувствуешь.",
                "Эти алкаши кайфоломы еще те. Но за бутылку водки сделают вид, что тебя не видели.",
                "Где я им здесь водку возьму? Ты бы хоть сказал когда мы собирались!",
                "Да я хз. Забыл что-то. Ты начинай собирать тут.\nЗа деревьями там тоже.",
                "А я в машине кое-что гляну и догоню. Слишком далеко только не уходи."
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

        return Dialog(camera, false, speeches, characters).apply {
            this.disposeHandler = disposeHandler
        }
    }
//
//    fun getGrandmaDialog(camera: OrthographicCamera,
//                       disposeHandler: () -> Unit): Dialog {
//
//        val speeches = arrayOf(
//                "Ты гляди че делает!",
//                "Грибы с земли ест!",
//                "На хоть рот самогоном пополощи..",
//                "Лесникам его не давай!",
//                "Он их ума лишает.."
//        )
//
//        val characters = arrayOf(
//                ResourcesManager.GRANDMA_DIALOG_FACE,
//                ResourcesManager.GRANDMA_DIALOG_FACE,
//                ResourcesManager.GRANDMA_DIALOG_FACE,
//                ResourcesManager.GRANDMA_DIALOG_FACE,
//                ResourcesManager.GRANDMA_DIALOG_FACE
//        )
//
//        return Dialog(camera, speeches, characters).apply {
//            this.disposeHandler = disposeHandler
//        }
//    }
//
//    fun getGrandmaMushroom(): Vector2 {
//        return Vector2(GameScreen.tileSize * 3f , GameScreen.tileSize * 20f)
//    }

    companion object {

        private val SIGN_DIALOG_SPEECHES = arrayOf(
                "Нажимай на траву, чтобы перемещать персонажа и на грибы, чтобы собирать их.",
                "Некоторые грибы оказывают на персонажа особые эффекты.\nСъешь гриб рядом со мной, чтобы пробраться между деревьями.",
                "Лесники, и не только, будут пытаться тебя поймать. Постарайся им не попасться.",
                "Через такие выходы ты сможешь выбраться на соседние опушки.\nНо помни, вернуться через них нельзя."
        )

        private val PLAYER_INITIAL_Y = GameScreen.tileSize * 5
        private val PLAYER_INITIAL_X = GameScreen.tileSize * 3
        private val FRIEND_INITIAL_X = GameScreen.tileSize * 6

        fun getPlayer() = Player(PLAYER_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())

        fun getDialog(index: Int, camera: OrthographicCamera) =
                getDialog(camera, arrayOf(SIGN_DIALOG_SPEECHES[index]))

        private fun getDialog(
                camera: OrthographicCamera,
                speeches: Array<String>
        ) = Dialog(camera, true, speeches, emptyArray())
    }
}