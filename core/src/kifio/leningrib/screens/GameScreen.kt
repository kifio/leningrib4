package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.LGCGame.Companion.ANIMATION_DURATION
import kifio.leningrib.LGCGame.Companion.ANIMATION_DURATION_LONG
import kifio.leningrib.LGCGame.Companion.isFirstLevelPassed
import kifio.leningrib.Utils
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.levels.FirstLevel
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager.*
import kifio.leningrib.model.actors.MovableActor
import kifio.leningrib.model.actors.Overlay
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.model.actors.game.*
import kifio.leningrib.model.items.Bottle
import kifio.leningrib.screens.input.LGestureDetector
import kifio.leningrib.screens.input.LInputListener
import kifio.leningrib.view.WorldRenderer
import model.LevelMap
import model.WorldMap
import java.util.concurrent.ThreadLocalRandom
import javax.rmi.CORBA.Util

class GameScreen(game: LGCGame,
                 private var level: Level,
                 @JvmField var player: Player,
                 private var worldMap: WorldMap    // Уровень конструируется с координатами 0,0. Карта уровня долго генерируется первый раз, передаем ее снаружи.
) : BaseScreen(game) {

    private val gestureListener: LInputListener? = LInputListener(this)
    private val gestureDetector: LGestureDetector? = LGestureDetector(gestureListener, this)
    private var worldRenderer: WorldRenderer?

    private var blackScreenTime = 0f
    private var screenEnterTime = 0f

    private var screenOut = false
    private var startGame = false
    private var shouldShowTutorial = true

    private var paused = true
    private var active = false

    var gameOver = false

    private var settings: Group? = null

    private var vodkaButton: SquareButton? = null

    private val levelSize = CommonLevel.LEVEL_HEIGHT * tileSize

    fun activate() {
        if (level is FirstLevel) {
            makeActorsVisible(false)
        } else {
            makeActorsVisible(true)
        }
        pauseGame(false)
        this.active = true
    }

    fun isPaused() = paused

    fun getCameraPostion() = game.camera.position

    private fun updateCamera() {
        level.updateCamera(game.camera, player)
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    override fun render(delta: Float) {
        transitionActor?.setOrigin(game.camera.position.x, game.camera.position.y)

        if (active) {
            update(delta)
            updateCamera()
            stage.act(delta)
            worldRenderer?.render(level, stage)
        }

        val isTutorialPassed = (level as? FirstLevel)?.passed == true

        if (active && screenEnterTime < ANIMATION_DURATION) {
            screenEnterTime += delta
            worldRenderer?.renderBlackScreen(screenEnterTime, ANIMATION_DURATION, true)
        } else if ((blackScreenTime < ANIMATION_DURATION_LONG && startGame)
                || screenOut
                || (blackScreenTime < ANIMATION_DURATION_LONG && isTutorialPassed)) {
            blackScreenTime += delta
            worldRenderer?.renderBlackScreen(blackScreenTime, ANIMATION_DURATION_LONG, false)
        } else if (isTutorialPassed && blackScreenTime >= ANIMATION_DURATION) {
            worldRenderer?.renderBlackScreen(blackScreenTime, ANIMATION_DURATION, false)
            player.resetPosition()
            level = getNextLevel(0, 0)
            resetStage()
            resumeGame()
            LGCGame.lastKnownCameraPosition = Gdx.graphics.height / 2f
            game.camera.position.y = player?.y ?: Gdx.graphics.height / 2f
            blackScreenTime = 0f
            screenEnterTime = 0f
        } else if (startGame && blackScreenTime >= ANIMATION_DURATION_LONG) {
            worldRenderer?.renderBlackScreen(blackScreenTime, ANIMATION_DURATION_LONG, false)
            startGame = false
            blackScreenTime = 0f
            screenEnterTime = 0f
            makeActorsVisible(true)
        }
    }

    private fun getNextLevel(x: Int, y: Int): Level {
        LGCGame.setFirstLevelPassed(true)
        return CommonLevel(player,
                worldMap.addLevel(x, y, (player.x / GameScreen.tileSize).toInt(), Config(LGCGame.LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)))
    }

    private fun update(delta: Float) {
        if (player.bottlesCount > 0) {
            vodkaButton?.isVisible = true
        }

        level.mushroomsSpeeches?.let { addSpeechesToStage(it) }
        level.forestersSpeeches?.let { addSpeechesToStage(it) }
        updateWorld(delta)
    }

    private fun updateWorld(delta: Float) {
        game.camera.let { camera ->
            level.update(delta, camera, this)

            if (level is CommonLevel) {

                val currentLevel = game.camera.position.y.toInt() / levelSize

                if (level.nextLevel == currentLevel) {
                    level.nextLevel += 1

                    val config = Config(LGCGame.LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
                    level.clearPassedLevels(currentLevel)

                    game.executor.submit {
                        val newLevel = CommonLevel(level)
                        val levelMap = worldMap.addLevel(0, level.nextLevel + 1, null, config)
                        newLevel.addLevelMapIfNeeded(levelMap, player, config)

                        Gdx.app.postRunnable {
                            this.level.dispose()
                            this.level = newLevel
                            updateStage()
                            stage.actors.sortedBy { it.zIndex }
                        }
                    }
                }
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
//        for (tree in treesManager.getTopBorderNonObstaclesTrees()) {
//            stage.addActor(tree)
//        }
        stage.addActor(player)
//        for (tree in treesManager.getBottomBorderNonObstaclesTrees()) {
//            stage.addActor(tree)
//        }
        for (i in 0 until level.foresters.size) {
            stage.addActor(level.foresters[i])
        }

        (level as? FirstLevel)?.let { it ->
            it.guards?.forEach {
                stage.addActor(it)
                stage.addActor(it.label)
            }
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

        (level as? FirstLevel)?.let { it ->
            it.guards?.forEach {
                stage.addActor(it)
                stage.addActor(it.label)
            }
        }
    }

    override fun dispose() {
        if (worldRenderer != null) {
            worldRenderer!!.dispose()
            worldRenderer = null
        }

        level.dispose()
        stage.dispose()

        gestureListener?.dispose()
        gestureDetector?.dispose()
    }

    fun showGameOver() {
        if (gameOver) return
        gameOver = true;

        transitionActor = Group()
        val overlay = Overlay(game.camera)

        val gameOverLogo = GameOverLogo(game.camera)
        val settingsButton = SquareButton(
                getRegion(SETTINGS_PRESSED),
                getRegion(SETTINGS),
                game.camera
        )

        settingsButton.zIndex = Int.MAX_VALUE

        val restartGameButton = StartGameButton(
                3,
                "НАЧАТЬ СНАЧАЛА",
                game.camera,
                getRegion(START_GAME_PRESSED),
                getRegion(START_GAME),
                Color(110 / 255f, 56 / 255f, 22 / 255f, 1f)
        )

        settingsButton.onTouchHandler = {
            if (settings == null) {
                settings = Group().apply {
                    x = Gdx.graphics.width.toFloat()
                    addActor(Overlay(game.camera, 40 * Gdx.graphics.density, getRegion(SETTINGS_BACKGROUND)))
                    addActor(SettingButton(game.camera, 0))
                    addActor(SettingButton(game.camera, 1))
                    addActor(SettingButton(game.camera, 2))
                    addAction(Actions.moveTo(0F, 0F, ANIMATION_DURATION))
                }

                stage.addActor(settings)
            }
        }

        restartGameButton.onTouchHandler = {
            if (settings == null) {
                restartGame()
            }
        }

        transitionActor?.addActor(overlay)
        transitionActor?.addActor(settingsButton)
        transitionActor?.addActor(gameOverLogo)
        transitionActor?.addActor(MushroomsCountView(game.camera, player.mushroomsCount))
        transitionActor?.addActor(restartGameButton)

        stage.addActor(transitionActor)
    }

    private fun pauseGame(withRestartOption: Boolean) {
        paused = true
        vodkaButton?.isVisible = false
        val overlay = Overlay(game.camera)

        var resumeOffsetsCount = 0
        var restartOffsetsCount = 0

        if (withRestartOption) {
            resumeOffsetsCount = 1
            restartOffsetsCount = -1
        }

        val resumeGameButton = StartGameButton(
                resumeOffsetsCount,
                if (withRestartOption) "ПРОДОЛЖИТЬ ИГРУ" else "НАЧАТЬ ИГРУ",
                game.camera,
                getRegion(START_GAME_PRESSED),
                getRegion(START_GAME),
                Color(110 / 255f, 56 / 255f, 22 / 255f, 1f)
        )

        var restartGameButton: StartGameButton? = null

        if (withRestartOption) {
            restartGameButton = StartGameButton(
                    restartOffsetsCount,
                    "НАЧАТЬ СНАЧАЛА",
                    game.camera,
                    getRegion(RESTART_BUTTON_PRESSED),
                    getRegion(RESTART_BUTTON),
                    Color(249 / 255f, 218 / 255f, 74f / 255f, 1f)
            )

            restartGameButton.onTouchHandler = {
                restartGame()
            }
        }

        val settingsButton = SquareButton(
                getRegion(SETTINGS_PRESSED),
                getRegion(SETTINGS),
                game.camera
        )

        settingsButton.onTouchHandler = {
            if (settings == null) {
                settings = Group().apply {
                    x = Gdx.graphics.width.toFloat()
                    addActor(Overlay(game.camera, 40 * Gdx.graphics.density, getRegion(SETTINGS_BACKGROUND)))
                    addActor(SettingButton(game.camera, 0))
                    addActor(SettingButton(game.camera, 1))
                    addActor(SettingButton(game.camera, 2))
                    addAction(Actions.moveTo(0F, 0F, ANIMATION_DURATION))
                }

                stage.addActor(settings)
            }
        }

        resumeGameButton.onTouchHandler = {
            if (!isFirstLevelPassed() && shouldShowTutorial) {
                startGame = true  // Чтобы рисовать черный экран
            }

            if (settings == null) {
                settingsButton.remove()
                overlay.remove()
                resumeGameButton.remove()
                restartGameButton?.remove()
                resumeGame()
            }
        }

        stage.addActor(overlay)
        stage.addActor(settingsButton)
        stage.addActor(resumeGameButton)

        if (restartGameButton != null) {
            stage.addActor(restartGameButton)
        }
    }

    private fun resumeGame() {
        paused = false

        val pauseButton = SquareButton(
                getRegion(PAUSE_PRESSED),
                getRegion(PAUSE),
                game.camera
        ).apply {
            zIndex = Int.MAX_VALUE
            onTouchHandler = {
                remove()
                pauseGame(true)
            }
        }

        if (vodkaButton == null) {
            vodkaButton = SquareButton(
                    getRegion(HUD_BOTTLE_PRESSED),
                    getRegion(HUD_BOTTLE),
                    game.camera,
                    SquareButton.LEFT)

            vodkaButton?.isVisible = false
            vodkaButton?.onTouchHandler = {

                if (vodkaButton?.isVisible == true) {
                    setupVodka()
                }

                player.bottlesCount -= 1

                if (player.bottlesCount == 0) {
                    vodkaButton?.remove()
                    vodkaButton = null
                }
            }
        }
        stage.addActor(pauseButton)
        stage.addActor(vodkaButton)

        if (shouldShowTutorial && !isFirstLevelPassed()) {
            shouldShowTutorial = false
            (level as? FirstLevel)?.let {
               stage.addAction(it.showDialog(0, game.camera, stage, player,true))
            }
        }
    }

    private var transitionActor: Group? = null
    override fun getTransitionActor(): Group = if (transitionActor == null) stage.root else transitionActor!!

    private fun restartGame() {
        LGCGame.setFirstLevelPassed(true)
        blackScreenTime = 0f
        screenOut = true  // Чтобы рисовать черный экран
        val levelAndPlayer = LGCGame.getLevelAndPlayer(worldMap)
        val level = levelAndPlayer.second
        val player = levelAndPlayer.first
        game.showGameScreen(GameScreen(game, level, player, worldMap))
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

//    fun isGameOver() = gameOver || win

    companion object {
        @JvmField
        var tileSize = 0
    }

    internal fun handleFling() {
        removeSettings()
    }

    internal fun handleKeyDown(keycode: Int): Boolean {
        val handled = keycode == Input.Keys.BACK
        if (handled) removeSettings()
        return handled
    }

    private fun removeSettings() {
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.moveTo(Gdx.graphics.width.toFloat(), 0f, ANIMATION_DURATION))
        sequenceAction.addAction(Actions.run {
            settings?.remove()
            settings = null
        })

        settings?.addAction(sequenceAction)
    }

    internal fun handleTouchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return stage.touchDown(x, y, pointer, button)
    }

    internal fun handleTouchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        val actorIsTouched = stage.actors
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

        if (!gameOver) {
            level.movePlayerTo(x.toFloat(), y.mapYToLevel(), player)
        }

        return true
    }

    private fun Int.mapYToScreen(): Float = Gdx.graphics.height - 1f - this;
    private fun Int.mapYToLevel(): Float = game.camera.position.y - Gdx.graphics.height / 2f + mapYToScreen()

    private fun makeActorsVisible(isVisible: Boolean) {
        stage.actors.forEach {
            if (it is MovableActor) it.isVisible = isVisible
        }
    }

    init {
        Gdx.input.inputProcessor = gestureDetector
        Gdx.input.isCatchBackKey = true
        worldRenderer = WorldRenderer(game.camera, spriteBatch)


        resetStage()
    }
}