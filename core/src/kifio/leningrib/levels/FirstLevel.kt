package kifio.leningrib.levels

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Array
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

class FirstLevel() : Level() {

    private val topCameraThreshold = levelHeight * GameScreen.tileSize - Gdx.graphics.height / 2f
    private var yLimit = ((levelHeight - 1) * GameScreen.tileSize) - 1
    var passed = false

    constructor(player: Player, levelMap: LevelMap): this() {
        super.setup(player, levelMap, Config(LGCGame.LEVEL_WIDTH, levelHeight))
    }

    var guards: Array<TutorialForester>? = null
        private set

    private val characters = arrayOf(
            ResourcesManager.PLAYER_DIALOG_FACE,
            ResourcesManager.PLAYER_DIALOG_FACE,
            ResourcesManager.PLAYER_DIALOG_FACE
    )

    private var shownDialogs = arrayOf(
            false, false, false, false, false
    )

    override fun getActors(): Array<out Actor> {
        val arr = Array<Actor>()

        if (guards == null) {
            guards = Array<TutorialForester>()
            guards?.add(TutorialForester(
                    GameScreen.tileSize * 1f,
                    GameScreen.tileSize * 28f,
                    "enemy_3",
                    "Проход в лес запрещен"))
        }
        arr.addAll(guards)
        return arr
    }

    override fun initMushrooms(config: Config, treesManager: TreesManager, mushroomsCount: Int): Array<Mushroom> {
        val mushrooms = Array<Mushroom>()
        mushrooms.add(Mushroom(GameScreen.tileSize * 4, GameScreen.tileSize * 7))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 12))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5, GameScreen.tileSize * 23))
        mushrooms.add(Mushroom(GameScreen.tileSize * 5,
                GameScreen.tileSize * 18,
                Mushroom.Effect.DEXTERITY,
                Float.POSITIVE_INFINITY))
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

        passed = Utils.mapCoordinate(gameScreen.player.y) >= yLimit
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
            Gdx.app.log("kifio_path", "eLimit: $yLimit; to y: ${Utils.mapCoordinate(y)}")
            super.movePlayerTo(x, y, player)
        }
    }

    override fun updateCamera(camera: OrthographicCamera, player: Player) {
        super.updateCamera(camera, player)
        camera.position.y = if (player.y < bottomCameraThreshold) {
            bottomCameraThreshold
        } else {
            player.y.coerceAtMost(topCameraThreshold)
        }
        camera.update()
    }

    override fun initForesters(levelMap: LevelMap?, config: Config?, player: Player?, roomRectangles: kotlin.Array<out Rectangle>?): Array<Forester> {
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

        fun getPlayer() = Player(PLAYER_INITIAL_X.toFloat(), PLAYER_INITIAL_Y.toFloat())

        fun getLevelHeight(): Int {
            val firstRoomHeight = (Gdx.graphics.height / GameScreen.tileSize) - 2
            return firstRoomHeight + 20
        }
    }
}