package kifio.leningrib.levels

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Dialog
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.tutorial.TutorialForester
import kifio.leningrib.screens.GameScreen
import model.LevelMap

class FirstLevel(player: Player, levelMap: LevelMap?) : Level(player, levelMap) {

    var guards: com.badlogic.gdx.utils.Array<TutorialForester>? = null
        private set

    private val characters = arrayOf(
            ResourcesManager.PLAYER_DIALOG_FACE,
            ResourcesManager.PLAYER_DIALOG_FACE,
            ResourcesManager.PLAYER_DIALOG_FACE
    )

    private var shownDialogs = arrayOf(
            false, false, false, false, false
    )

    override fun getActors(): com.badlogic.gdx.utils.Array<out Actor> {
        val arr = com.badlogic.gdx.utils.Array<Actor>()

        if (guards == null) {
            guards = com.badlogic.gdx.utils.Array<TutorialForester>()
            guards?.add(TutorialForester(
                    GameScreen.tileSize * 1f,
                    GameScreen.tileSize * 28f,
                    "enemy_3",
                    "Проход в лес запрещен"))
        }
        arr.addAll(guards)
        return arr
    }

    override fun initMushrooms(config: Config, treesManager: TreesManager, mushroomsCount: Int): com.badlogic.gdx.utils.Array<Mushroom> {
        val mushrooms = com.badlogic.gdx.utils.Array<Mushroom>()
        mushrooms.add(Mushroom(GameScreen.tileSize * 4, GameScreen.tileSize * 7))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 12))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 23))
        mushrooms.add(Mushroom(GameScreen.tileSize * 4,
                GameScreen.tileSize * 18,
                Mushroom.Effect.DEXTERITY,
                Float.POSITIVE_INFINITY))
        mushrooms.addAll(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 34, Mushroom.Effect.PATHFINDER))
        return mushrooms
    }

    override fun initForesters(levelMap: LevelMap, config: Config, player: Player, roomRectangles: Array<Rectangle>): com.badlogic.gdx.utils.Array<Forester> {
        return com.badlogic.gdx.utils.Array<Forester>(0)
    }

    override fun update(delta: Float, camera: OrthographicCamera, gameScreen: GameScreen) {
        super.update(delta, camera, gameScreen)

        val x = gameScreen.player.x / GameScreen.tileSize;
        val y = gameScreen.player.y / GameScreen.tileSize;

        if (mushrooms[0] == null && (mushrooms[1] != null && !mushrooms[1].hasStableSpeech())) {
            mushrooms[1].stableSpeech = "Теперь на меня нажимай!"
        }

        if (mushrooms[0] == null && mushrooms[1] == null && !shownDialogs[1]) {
            gameScreen.stage.addAction(showDialog(1, camera, gameScreen.stage, gameScreen.player, false))
        }

        if (gameScreen.player.isDexterous && mushrooms[3] == null && shouldUpdateProvocation()) {
            mushroomsSpeeches[2].apply {
                clear()
                remove()
            }
            mushroomsSpeeches[2] = null
            mushrooms[2].stableSpeech = PROVOCATION_1
        }

        if (Utils.isInRoom(roomsRectangles[1], x, y) && !shownDialogs[2]) {
            gameScreen.player.clearActions()
            gameScreen.stage.addAction(showDialog(2, camera, gameScreen.stage, gameScreen.player, false))
        }

        if (Utils.isInRoom(roomsRectangles[2], x, y) && !shownDialogs[3]) {
            gameScreen.player.clearActions()
            gameScreen.player.clearEffect()
            gameScreen.stage.addAction(showDialog(3, camera, gameScreen.stage, gameScreen.player, false))
        }

        if (bottleManager.bottles.isNotEmpty()) {
            val bottle = bottleManager.bottles[0]

            guards?.forEach {
                it.runToBottle(bottle, forestGraph)
            }

            guards?.get(0)?.apply {
                if (!bottle.hasDrinker(this)) {
                    val fx = Utils.mapCoordinate(this.x).toInt()
                    val fy = Utils.mapCoordinate(this.y).toInt()

                    val bx = Utils.mapCoordinate(bottle.x).toInt()
                    val by = Utils.mapCoordinate(bottle.y).toInt()

                    if (fx == bx && fy == by) {
                        bottle.addDrinker(this)
                        this.setIdleState()
                        this.label.setText("Грех за такое не выпить!")
                    }
                }
            }
        }

        if (Utils.isInRoom(roomsRectangles[3], x, y) && !shownDialogs[4]) {
            gameScreen.player.clearActions()
            gameScreen.player.clearEffect()
            gameScreen.stage.addAction(showDialog(4, camera, gameScreen.stage, gameScreen.player, false))
        }

    }

    private fun shouldUpdateProvocation(): Boolean {
        return mushrooms[2] != null && mushrooms[2].hasStableSpeech() && PROVOCATION_1 != mushrooms[2].stableSpeech
    }

    private fun getFirstDialog(camera: OrthographicCamera,
                               disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Привет!\nМеня зовут Lenin.",
                "В этот лес я приехал расширять свое сознание."
        )

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = {
                mushrooms[0].stableSpeech = "Нажми на меня"
                disposeHandler.invoke()
            }
        }
    }

    private fun getSecondDialog(camera: OrthographicCamera,
                                disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Отлично!",
                "Ты можешь отправить меня в любую точку экрана, указав на нее!.",
                "Давай посмотрим, что там на соседней поляне."
        )

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = {
                mushrooms[2].stableSpeech = PROVOCATION_0
                disposeHandler.invoke()
            }
        }
    }

    private fun getThirdDialog(camera: OrthographicCamera,
                               disposeHandler: () -> Unit): Dialog {

        shownDialogs[2] = true

        val speeches = arrayOf(
                "Здесь мне просто так не пройти!",
                "Хорошо, что есть грибы убеждающие меня в обратном..")

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = {
                mushrooms[3].stableSpeech = "Со мной ты тут пролезешь!"
                disposeHandler.invoke()
            }
        }
    }

    private fun getFourthDialog(camera: OrthographicCamera,
                                disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "В зарослях нашлась бутылка водки.",
                "Если бросить такую перед лесником, он ошалеет и забудет обо мне.",
                "Думаю это отличный способ отвлечь тех двух ребят."
        )

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = {
                disposeHandler.invoke()
            }
        }
    }

    private fun getLastDialog(camera: OrthographicCamera,
                              disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Получилось! Этим ребятам лучше не попадаться.",
                "Дальше начинается настоящий лес.",
                "А этот гриб поможет мне найти дорогу к следующей опушке с грибами."
        )

        return Dialog(camera, speeches, characters).apply {
            this.disposeHandler = {
                disposeHandler.invoke()
            }
        }
    }

    fun showDialog(i: Int,
                   camera: OrthographicCamera,
                   stage: Stage,
                   player: Player,
                   withDelay: Boolean): Action? {

        player.clearActions()
        shownDialogs[i] = true
        val sequence = SequenceAction()

        if (withDelay) {
            sequence.addAction(Actions.delay(LGCGame.ANIMATION_DURATION * 2))
        }

        sequence.addAction(Actions.run {
            stage.addActor(
                    if (i == 0) {
                        getFirstDialog(camera) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    } else if (i == 1) {
                        getSecondDialog(camera) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    } else if (i == 2) {
                        getThirdDialog(camera) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    } else if (i == 3) {
                        getFourthDialog(camera) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                } else if (actor is Player) {
                                    actor.bottlesCount += 1
                                }

                            }
                        }
                    } else {
                        getLastDialog(camera) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    }
            )
        })
        return sequence
    }

    override fun movePlayerTo(x: Float, y: Float, player: Player) {
        if (mushrooms[0] != null) {
            if (isMushroomTouched(mushrooms[0], x, y)) {
                super.movePlayerTo(x, y, player)
            }
        } else if (mushrooms[1] != null) {
            if (isMushroomTouched(mushrooms[1], x, y)) {
                super.movePlayerTo(x, y, player)
            }
        } else {
            super.movePlayerTo(x, y, player)
        }
    }

    private fun isMushroomTouched(m: Mushroom?, x: Float, y: Float): Boolean {
        return m != null && m.bounds.contains(x, y)
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

        private const val PROVOCATION_0 = "А не получишь! А не получишь!"
        private const val PROVOCATION_1 = "Ну давай, иди сюда!"

        private val SIGN_DIALOG_SPEECHES = arrayOf(
                "Нажимай на траву, чтобы перемещать персонажа и на грибы, чтобы собирать их.",
                "Некоторые грибы оказывают на персонажа особые эффекты.\nСъешь гриб рядом со мной, чтобы пробраться между деревьями.",
                "Лесники, и не только, будут пытаться тебя поймать. Постарайся им не попасться.",
                "Через такие выходы ты сможешь выбраться на соседние опушки.\nНо помни, вернуться через них нельзя."
        )

        private val PLAYER_INITIAL_Y = GameScreen.tileSize * 3
        private val PLAYER_INITIAL_X = GameScreen.tileSize * 5
        private val FRIEND_INITIAL_X = GameScreen.tileSize * 6

        fun getPlayer() = Player(PLAYER_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())

        fun getDialog(index: Int, camera: OrthographicCamera) =
                getDialog(camera, arrayOf(SIGN_DIALOG_SPEECHES[index]))

        private fun getDialog(
                camera: OrthographicCamera,
                speeches: Array<String>
        ) = Dialog(camera, speeches, emptyArray())
    }
}