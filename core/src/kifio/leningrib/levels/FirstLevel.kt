package kifio.leningrib.levels

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Array
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.LUTController
import kifio.leningrib.Utils
import kifio.leningrib.levels.helpers.TreesManager
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.ShaderStage
import kifio.leningrib.model.actors.fixed.TutorialForester
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Mushroom
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.ui.Dialog
import kifio.leningrib.model.pathfinding.ForestGraph
import kifio.leningrib.screens.GameScreen
import model.LevelMap

class FirstLevel() : Level() {

    private var yLimit = ((levelHeight - 1) * GameScreen.tileSize) - 1
    var passed = false

    constructor(player: Player, levelMap: LevelMap) : this() {
        super.setup(player, levelMap, Config(LGCGame.LEVEL_WIDTH, levelHeight))
        if (guards == null) {
            guards = Array<TutorialForester>()
            guards?.add(TutorialForester(
                    GameScreen.tileSize * 1f,
                    GameScreen.tileSize * (firstRoomHeight + 13f),
                    "enemy_3")
            )
        }
    }

    var guards: Array<TutorialForester>? = null
        private set

    private var shownDialogs = arrayOf(
            false, false, false, false, false
    )

    private var isLastRoomAvailable = false

    override fun initMushrooms(config: Config, treesManager: TreesManager, mushroomsCount: Int,
                               roomsRectangles: kotlin.Array<Rectangle>): Array<Mushroom> {
        val mushrooms = Array<Mushroom>()
        mushrooms.add(Mushroom(GameScreen.tileSize * 4, GameScreen.tileSize * 7))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 12))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * (firstRoomHeight + 8)))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5,
                GameScreen.tileSize * (firstRoomHeight + 4),
                Mushroom.Effect.DEXTERITY,
                Float.POSITIVE_INFINITY))
        mushrooms.add(Mushroom(GameScreen.tileSize * 3, GameScreen.tileSize * (levelHeight - 1)))
        mushrooms.add(Mushroom(GameScreen.tileSize * 4, GameScreen.tileSize * (levelHeight - 1)))

        return mushrooms
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

                    val fx = Utils.mapCoordinate(this.x)
                    val fy = Utils.mapCoordinate(this.y)

                    val bx = Utils.mapCoordinate(bottle.x).toInt()
                    val by = Utils.mapCoordinate(bottle.y).toInt()

                    isLastRoomAvailable = Utils.isInRoom(roomsRectangles[2], fx / GameScreen.tileSize, fy / GameScreen.tileSize)

                    if (fx.toInt() == bx && fy.toInt() == by) {
                        bottle.addDrinker(this)
                        this.setIdleState()
                    }
                }
            }
        }

        if (Utils.isInRoom(roomsRectangles[3], x, y) && !shownDialogs[4]) {
            gameScreen.player.clearActions()
            gameScreen.player.clearEffect()
            gameScreen.stage.addAction(showDialog(4, camera, gameScreen.stage, gameScreen.player, false))
        }


        mushrooms[4]?.stableSpeech = ""
        mushrooms[5]?.stableSpeech = ""

        passed = Utils.mapCoordinate(gameScreen.player.y) >= yLimit
    }

    private fun shouldUpdateProvocation(): Boolean {
        return mushrooms[2] != null && mushrooms[2].hasStableSpeech() && PROVOCATION_1 != mushrooms[2].stableSpeech
    }

    private fun getFirstDialog(camera: OrthographicCamera,
                               lutController: LUTController,
                               disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Привет!\nМеня зовут Lenin.",
                "Я приехал в лес за грибами расширяющими сознание.",
                "Для начала осмотримся."
        )

        val next = arrayOf("Понятно", "Ок", "Давай")

        return Dialog(camera, lutController, speeches, next, Array(speeches.size) { i -> ResourcesManager.PLAYER_DIALOG_FACE }).apply {
            this.disposeHandler = {
                mushrooms[0].stableSpeech = "Нажми на меня"
                disposeHandler.invoke()
            }
        }
    }

    private fun getSecondDialog(camera: OrthographicCamera,
                                lutController: LUTController,
                                disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Отлично!\nГрибы здесь сами говорят куда идти.",
                "Отправь меня в любую точку экрана, указав на нее.",
                "Посмотрим, что там на соседней поляне"
        )

        return Dialog(camera, lutController, speeches, arrayOf("Понятно", "Хорошо", "Ок"), Array(speeches.size) { i -> ResourcesManager.PLAYER_DIALOG_FACE }).apply {
            this.disposeHandler = {
                mushrooms[2].stableSpeech = PROVOCATION_0
                disposeHandler.invoke()
            }
        }
    }

    private fun getThirdDialog(camera: OrthographicCamera,
                               lutController: LUTController,
                               disposeHandler: () -> Unit): Dialog {

        shownDialogs[2] = true

        val speeches = arrayOf(
                "Здесь мне просто так не пройти!",
                "Хорошо, что этот гриб убеждает меня в обратном..")

        return Dialog(camera, lutController, speeches, arrayOf("Это да", "Ок"), Array(speeches.size) { i -> ResourcesManager.PLAYER_DIALOG_FACE }).apply {
            this.disposeHandler = {
                mushrooms[3].stableSpeech = "Со мной ты тут пролезешь!"
                disposeHandler.invoke()
            }
        }
    }

    private fun getFourthDialog(camera: OrthographicCamera,
                                lutController: LUTController,
                                disposeHandler: () -> Unit): Dialog {

        val speeches = arrayOf(
                "Некоторые грибы дают мне сверхспобосности",
                "Ну или мне так кажется..",
                "Нажми на кнопку с бутылкой, чтобы отвлечь ей лесника."
        )


        return Dialog(camera, lutController, speeches, arrayOf("Ок", "Да", "Окей"), Array(speeches.size) { i -> ResourcesManager.PLAYER_DIALOG_FACE }).apply {
            this.disposeHandler = {
                disposeHandler.invoke()
            }
        }
    }

    private fun getLastDialog(camera: OrthographicCamera,
                              lutController: LUTController,
                              disposeHandler: () -> Unit): Dialog {
        val speeches = arrayOf(
                "Получилось! " +
                        "Дальше начинается настоящий лес.",
                "А эти грибы помогут мне найти дорогу к следующей опушке."
        )

        return Dialog(camera, lutController, speeches, arrayOf("Да!", "Окей", "Погнали"), Array(speeches.size) { i -> ResourcesManager.PLAYER_DIALOG_FACE }).apply {
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

        if (stage !is ShaderStage) return null

        player.clearActions()
        shownDialogs[i] = true
        val sequence = SequenceAction()

        if (withDelay) {
            sequence.addAction(Actions.delay(LGCGame.ANIMATION_DURATION * 2))
        }

        sequence.addAction(Actions.run {
            stage.addActor(
                    if (i == 0) {
                        getFirstDialog(camera, stage.lutController) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    } else if (i == 1) {
                        getSecondDialog(camera, stage.lutController) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    } else if (i == 2) {
                        getThirdDialog(camera, stage.lutController) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                }
                            }
                        }
                    } else if (i == 3) {
                        getFourthDialog(camera, stage.lutController) {
                            stage.actors.forEach { actor ->
                                if (actor is Dialog) {
                                    actor.remove()
                                } else if (actor is Player) {
                                    actor.increaseBottlesCount()
                                }
                            }
                        }
                    } else {
                        getLastDialog(camera, stage.lutController) {
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

    override fun movePlayerTo(x: Float, y: Float, player: Player, callback: Runnable?) {
        if (mushrooms[0] != null) {
            if (isMushroomTouched(mushrooms[0], x, y)) {
                super.movePlayerTo(x, y, player, callback)
            }
        } else if (mushrooms[1] != null) {
            if (isMushroomTouched(mushrooms[1], x, y)) {
                super.movePlayerTo(x, y, player, callback)
            }
        } else {
            if (isLastRoomAvailable || (roomsRectangles[2].y + roomsRectangles[2].height) > y / GameScreen.tileSize) {
                super.movePlayerTo(x, y, player, callback)
            }
        }
    }

    override fun initForesters(levelMap: LevelMap?, config: Config?, player: Player?, roomRectangles: kotlin.Array<out Rectangle>?, forestGraph: ForestGraph): Array<Forester> {
        return Array<Forester>(0)
    }

    override fun getLevelHeight() = FirstLevel.getLevelHeight()

    private fun isMushroomTouched(m: Mushroom?, x: Float, y: Float): Boolean {
        return m != null && m.bounds.contains(x, y)
    }

    companion object {

        private const val PROVOCATION_0 = "А не получишь! А не получишь!"
        private const val PROVOCATION_1 = "Ну давай, иди сюда!"

        private val PLAYER_INITIAL_Y = GameScreen.tileSize * 3
        private val PLAYER_INITIAL_X = GameScreen.tileSize * 5

        private val firstRoomHeight = (Gdx.graphics.height / GameScreen.tileSize) - 2

        fun getPlayer() = Player(PLAYER_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())

        fun getLevelHeight(): Int {
            return firstRoomHeight + 20
        }
    }
}