package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.LGCGame.Companion.ANIMATION_DURATION
import kifio.leningrib.LGCGame.Companion.ANIMATION_DURATION_LONG
import kifio.leningrib.LUTController
import kifio.leningrib.Utils
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.model.ResourcesManager.*
import kifio.leningrib.model.actors.fixed.Bottle
import kifio.leningrib.model.actors.game.MovableActor
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.ui.*
import kifio.leningrib.screens.input.LGestureDetector
import kifio.leningrib.screens.input.LInputListener
import model.WorldMap

class GameScreen(game: LGCGame,
                 private var level: CommonLevel,
                 @JvmField var player: Player,
                 private var worldMap: WorldMap    // Уровень конструируется с координатами 0,0. Карта уровня долго генерируется первый раз, передаем ее снаружи.
) : BaseScreen(game) {

    private val gestureListener: LInputListener? = LInputListener(this)
    private val gestureDetector: LGestureDetector? = LGestureDetector(gestureListener, this)
    private var blackScreenTime = 0f
    private var screenEnterTime = 0f
    private var screenOut = false
    private var startGame = false
    private var paused = true
    private var storeOpened = false
    private var active = false
    private var lastKnownCameraPosition = 0f
    private var settings: Group? = null
    private var vodkaButton: SquareButton? = null
    private var settingsButton: SquareButton? = null
    private val levelSize = CommonLevel.LEVEL_HEIGHT * tileSize
    private var accumulatedTime = 0f
    private var dialogs = mutableListOf<Dialog>()
    private var pauseButton: SquareButton? = null
    private var restartGameButton: StartGameButton? = null
    private var resumeGameButton: StartGameButton? = null
    private var overlay: Overlay? = null

    val lutController = LUTController()
    var gameOver = false

    override fun show() {
        super.show()
        resetStage()
        makeActorsVisible()
        pauseGame(false)
    }

    fun activate() {
        this.active = true
    }

    fun isPaused() = paused || storeOpened

    fun getCameraPostion() = camera.position

    private fun updateCamera() {
        if (camera.position.y > lastKnownCameraPosition) {
            lastKnownCameraPosition = camera.position.y
        }
        camera.position.y = player.y.coerceAtLeast(lastKnownCameraPosition)
        camera.update()
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        accumulatedTime += delta
        transitionActor?.setOrigin(camera.position.x, camera.position.y)

        update(delta)
        updateCamera()
        stage.act(delta)
        stage.draw()

        if (screenEnterTime < ANIMATION_DURATION) {
            screenEnterTime += delta
            renderBlackScreen(screenEnterTime, ANIMATION_DURATION, true)
        } else if (startGame && blackScreenTime >= ANIMATION_DURATION_LONG) {
            renderBlackScreen(blackScreenTime, ANIMATION_DURATION_LONG, false)
            startGame = false
            blackScreenTime = 0f
            screenEnterTime = 0f
            makeActorsVisible()
        }
    }

    private var bottleWasUpdated = false

    private fun update(delta: Float) {
        if (player.bottlesCount > 0) {
            if (!bottleWasUpdated) {
                bottleWasUpdated = true
                vodkaButton?.isVisible = true
                vodkaButton?.addAction(getScaleAnimSequence())
            }
        }

        if (player.mushroomsCount > 0) {
            player.updateLabel(lutController.updateLut(delta, player.mushroomsCount))
        }

        player.bottomThreshold = camera.position.y - (Gdx.graphics.height / 2f);
        level.updatePlayerPath(player)

        level.mushroomsSpeeches?.let { addSpeechesToStage(it) }
        level.forestersSpeeches?.let { addSpeechesToStage(it) }
        updateWorld(delta)
    }

    private fun getScaleAnimSequence(): SequenceAction {
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.scaleTo(2f, 2f, ANIMATION_DURATION, Interpolation.exp5))
        sequenceAction.addAction(Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION, Interpolation.exp5))
        sequenceAction.addAction(Actions.scaleTo(2f, 2f, ANIMATION_DURATION, Interpolation.exp5))
        sequenceAction.addAction(Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION, Interpolation.exp5))
        sequenceAction.addAction(Actions.scaleTo(2f, 2f, ANIMATION_DURATION, Interpolation.exp5))
        sequenceAction.addAction(Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION, Interpolation.exp5))
        return sequenceAction
    }

    fun getDialogs(): List<Dialog> {
        dialogs.clear()
        for (actor in stage.actors) {
            if (actor is Dialog) {
                dialogs.add(actor)
            }
        }
        return dialogs
    }

    private fun updateWorld(delta: Float) {
        camera.let { camera ->
            level.update(delta, camera, this)

            if (level is CommonLevel) {

                val currentLevel = camera.position.y.toInt() / levelSize

                if (level.nextLevel == currentLevel) {
                    level.nextLevel += 1
//                    Gdx.app.log("kifio_level", "next level should be: ${level.nextLevel}")
                    val config = Config(LGCGame.LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
                    level.clearPassedLevels(currentLevel)

                    executor.submit {
                        val newLevel = CommonLevel(level)
                        val levelMap = worldMap.addLevel(0, level.nextLevel + 1, null, config)
                        newLevel.addLevelMapIfNeeded(levelMap, player, config)

                        Gdx.app.postRunnable {
                            this.level.dispose()
                            this.level = newLevel
                            updateStage()
                            stage.actors.sort { actor, actor2 -> actor.zIndex.compareTo(actor2.zIndex) }
                        }
                    }
                }
            }
        }
    }

    private fun getSettingsButton(): SquareButton {
        return SquareButton(
                getRegion(SETTINGS_PRESSED),
                getRegion(SETTINGS),
                camera,
                lutController
        ).apply {
            onTouchHandler = {
                openSettings()
            }
        }
    }

    private fun addSpeechesToStage(speeches: Array<Label?>?) {
        if (speeches == null) return
        for (speech in speeches) {
            if (speech != null && speech.stage == null) {
                stage.addActor(speech)
            }
        }
    }

    private fun resetStage() {
        stage.clear()
        for (mushroom in level.mushrooms) {
            stage.addActor(mushroom)
        }
        val treesManager = level.treesManager
        for (tree in treesManager.getObstacleTrees()) {
            stage.addActor(tree)
        }

        stage.addActor(player)

        for (i in 0 until level.foresters.size) {
            stage.addActor(level.foresters[i])
        }
    }

    private fun updateStage() {

        for (mushroom in level.mushrooms) {
            if (mushroom != null && mushroom.stage != this.stage) {
                stage.addActor(mushroom)
            }
        }

        val treesManager = level.treesManager

        for (tree in treesManager.getObstacleTrees()) {
            if (tree.stage != this.stage) {
                stage.addActor(tree)
            }
        }

        stage.addActor(player)

        for (i in 0 until level.foresters.size) {
            if (level.foresters[i].stage != this.stage) {
                stage.addActor(level.foresters[i])
            }
        }

        stage.actors.filter { it is SquareButton }
                .forEach { it.zIndex = stage.actors.count() - 1 }
    }

    override fun dispose() {
        level.dispose()
        stage.dispose()

        gestureListener?.dispose()
        gestureDetector?.dispose()
    }

    fun showGameOver() {
        if (gameOver) return
        gameOver = true

        var maxScore = game.getMaxScore()
        if (player.score > maxScore) {
            game.saveMaxScore(player.score)
            maxScore = player.score
        }

        stage.actors
                .filterIsInstance<SquareButton>()
                .forEach {
                    it.clear()
                    it.remove()
                    vodkaButton = null
                }

        transitionActor = Group()
        overlay = Overlay(camera, 0f, lutController)

        val gameOverLogo = GameOverLogo(camera, lutController)

        restartGameButton = StartGameButton(
                3,
                "НАЧАТЬ СНАЧАЛА",
                lutController,
                camera,
                getRegion(START_GAME_PRESSED),
                getRegion(START_GAME),
                Color(110 / 255f, 56 / 255f, 22 / 255f, 1f)
        )

        restartGameButton?.onTouchHandler = {
            if (settings == null) {
                restartGame()
            }
        }

        val settingsButton = getSettingsButton()

        transitionActor?.addActor(overlay)
        transitionActor?.addActor(settingsButton)
        transitionActor?.addActor(gameOverLogo)
        transitionActor?.addActor(MushroomsCountView(camera, player.score, maxScore, lutController))
        transitionActor?.addActor(restartGameButton)

        stage.addActor(transitionActor)
    }

    private fun pauseGame(withRestartOption: Boolean) {
        paused = true
        vodkaButton?.isVisible = false
        overlay = Overlay(camera, 0f, lutController)

        var resumeOffsetsCount = 0
        var restartOffsetsCount = 0

        if (withRestartOption) {
            resumeOffsetsCount = 1
            restartOffsetsCount = -1
        }
        resumeGameButton = StartGameButton(
                resumeOffsetsCount,
                if (withRestartOption) "ПРОДОЛЖИТЬ ИГРУ" else "НАЧАТЬ ИГРУ",
                lutController,
                camera,
                getRegion(START_GAME_PRESSED),
                getRegion(START_GAME),
                Color(110 / 255f, 56 / 255f, 22 / 255f, 1f)
        )

        if (withRestartOption) {
            restartGameButton = StartGameButton(
                    restartOffsetsCount,
                    "НАЧАТЬ СНАЧАЛА",
                    lutController,
                    camera,
                    getRegion(RESTART_BUTTON_PRESSED),
                    getRegion(RESTART_BUTTON),
                    Color(249 / 255f, 218 / 255f, 74f / 255f, 1f)
            )

            restartGameButton?.onTouchHandler = {
                restartGame()
            }
        }

        settingsButton = getSettingsButton()
        resumeGameButton?.onTouchHandler = {
            pressResume()
        }

        stage.addActor(overlay)
        stage.addActor(settingsButton)
        stage.addActor(resumeGameButton)

        if (restartGameButton != null) {
            stage.addActor(restartGameButton)
        }
    }

    private fun pressResume() {
        if (settings == null) {
            settingsButton?.remove()
            overlay?.remove()
            resumeGameButton?.remove()
            restartGameButton?.remove()
            resumeGame()
            settingsButton = null
            overlay = null
        }
    }

    private fun openSettings() {
        if (settings == null) {
            settings = Group().apply {
                x = Gdx.graphics.width.toFloat()
                addActor(Overlay(camera, 40 * Gdx.graphics.density, lutController,
                        getRegion(SETTINGS_BACKGROUND)))
                addActor(SettingButton(camera, lutController, game, 0))
                addAction(Actions.moveTo(0F, 0F, ANIMATION_DURATION))

                val closeStoreButton = SquareButton(
                        getRegion(CLOSE_STORE_PRESSED),
                        getRegion(CLOSE_STORE),
                        camera, lutController, 1
                )

                closeStoreButton.onTouchHandler = {
                    removeSettings()
                }

                addActor(closeStoreButton)
            }

            stage.addActor(settings)
        }
    }

    private fun resumeGame() {
        paused = false
        resumeGameButton = null
        restartGameButton = null
        pauseButton = SquareButton(
                getRegion(PAUSE_PRESSED),
                getRegion(PAUSE),
                camera,
                lutController
        ).apply {
            onTouchHandler = {
                remove()
                pauseGame(true)
            }
        }

        if (vodkaButton == null) {
            vodkaButton = SquareButton(
                    getRegion(HUD_BOTTLE_PRESSED),
                    getRegion(HUD_BOTTLE),
                    camera, lutController,
                    SquareButton.VODKA,
                    SquareButton.RIGHT)

            vodkaButton?.isVisible = player.bottlesCount > 0
            vodkaButton?.onTouchHandler = {
                throwBottle()
            }
        } else {
            vodkaButton?.isVisible = player.bottlesCount > 0
        }

        stage.addActor(pauseButton)
        stage.addActor(vodkaButton)

        pauseButton?.zIndex = stage.actors.count() - 1
        vodkaButton?.zIndex = stage.actors.count() - 1

        (level as? CommonLevel)?.showTutorialIfNeeded(camera, lutController, stage, game) {
            player.increaseBottlesCount();
            bottleWasUpdated = false
        }
    }

    private fun throwBottle() {
        if (vodkaButton?.isVisible == true) {
            setupVodka()
        }

        player.decreaseBottlesCount();

        if (player.bottlesCount == 0) {
            vodkaButton?.remove()
            vodkaButton = null
        }
    }

    private var transitionActor: Group? = null
    override fun getTransitionActor(): Group = if (transitionActor == null) stage.root else transitionActor!!

    private fun restartGame() {
        blackScreenTime = 0f
        lutController.stop()
        screenOut = true  // Чтобы рисовать черный экран
        val worldMap = WorldMap()
        val levelAndPlayer = LGCGame.getLevelAndPlayer(worldMap)
        val level = levelAndPlayer.second
        val player = levelAndPlayer.first
        restartGameButton = null
        resumeGameButton = null
        stage.actors
                .filterIsInstance<Overlay>()
                .forEach { it.remove() }
        stage.addAction(Actions.run {
            game.showGameScreen(GameScreen(game, level, player, worldMap))
        })
    }

    private fun setupVodka() {
        val playerX = Utils.mapCoordinate(player.x)
        val playerY = Utils.mapCoordinate(player.y)
        val bottle = Bottle(playerX, playerY)
        stage.addActor(bottle)
        level.addBottle(bottle)
        bottle.addAction(if (player.goLeft) {
            Actions.moveTo(playerX, playerY, 0.5f, Interpolation.circle)
        } else {
            Actions.moveTo(playerX, playerY, 0.5f, Interpolation.circle)
        })
    }

    companion object {
        @JvmField
        var tileSize = 0
    }

    internal fun handleFlingRight() {
        if (!storeOpened) {
            removeSettings()
        }
    }

    internal fun handleKeyUp(keycode: Int) {
        when (keycode) {
            Input.Keys.LEFT,
            Input.Keys.RIGHT,
            Input.Keys.DOWN,
            Input.Keys.UP -> {
                player.removeMovementDirection(keycode)
            }
        }
    }

    internal fun handleKeyDown(keycode: Int): Boolean {
        var handled = true

        when (keycode) {
            Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN -> {
                player.addMovementDirection(keycode)
            }
            Input.Keys.BACK -> {
                removeSettings()
            }
            Input.Keys.SPACE -> {
                throwBottle()
            }
            Input.Keys.ESCAPE -> {
                if (settings != null) {
                    removeSettings()
                } else if (restartGameButton != null && resumeGameButton != null) {
                    pressResume()
                } else if (!paused && !gameOver) {
                    pauseButton?.remove()
                    pauseGame(true)
                }
            }
            Input.Keys.ENTER -> {
                if (resumeGameButton != null && restartGameButton == null) {
                    pressResume()
                } else if (gameOver) {
                    restartGame()
                } else {
                    (stage.actors.find { it is Dialog } as? Dialog)?.handleTouch()
                }
            }
            else -> {
                handled = false
            }
        }

        return handled
    }

    private fun removeSettings() {
        val sequenceAction = SequenceAction()
        val x = if (storeOpened) -Gdx.graphics.width.toFloat() else Gdx.graphics.width.toFloat()

        sequenceAction.addAction(Actions.moveTo(x, 0f, ANIMATION_DURATION))
        sequenceAction.addAction(Actions.run {
            settings?.remove()
            settings = null
            storeOpened = false
        })

        settings?.addAction(sequenceAction)
    }

    internal fun handleTouchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return isStageInitialized() && stage.touchDown(x, y, pointer, button)
    }

    internal fun handleTouchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        if (!isStageInitialized()) return false
        val actorIsTouched = settings != null || stage.actors
                .filterIsInstance<StaticActor>()
                .find { it.touched } != null


        val haveDialogs = stage.actors
                .filterIsInstance<Dialog>()
                .isNotEmpty()

        stage.touchUp(x, y, pointer, button)
        if (actorIsTouched || haveDialogs)
            return true

        if (x < 40 * Gdx.graphics.density && settings != null && settings?.actions?.isEmpty == true) {
            removeSettings()
            return true
        }

        return true
    }

    private fun Int.mapYToScreen(): Float = Gdx.graphics.height - 1f - this;
    private fun Int.mapYToLevel(): Float = camera.position.y - Gdx.graphics.height / 2f + mapYToScreen()

    private fun makeActorsVisible() {
        stage.actors.forEach {
            if (it is MovableActor) it.isVisible = true
        }
    }

    private val renderer: ShapeRenderer = ShapeRenderer()

    private fun renderBlackScreen(currentTime: Float,
                                  maximumTime: Float,
                                  inverted: Boolean) {
        var alpha = (currentTime / maximumTime).coerceAtMost(1f)
        if (inverted) alpha = 1f - alpha
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.setColor(0f, 0f, 0f, alpha)
        renderer.rect(0f, camera.position.y - Gdx.graphics.height / 2f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        renderer.end()
    }

    init {
        Gdx.input.inputProcessor = gestureDetector
        Gdx.input.isCatchBackKey = true
        lutController.setup()
        lastKnownCameraPosition = Gdx.graphics.height / 2f
        camera.position.y = player.y
    }
}