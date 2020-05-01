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
import kifio.leningrib.Utils
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.levels.FirstLevel
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager.*
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

class GameScreen(game: LGCGame,
                 levelMap: LevelMap,
                 private var worldMap: WorldMap    // Уровень конструируется с координатами 0,0. Карта уровня долго генерируется первый раз, передаем ее снаружи.
) : BaseScreen(game) {

    private val gestureListener: LInputListener? = LInputListener(this)
    private val gestureDetector: LGestureDetector? = LGestureDetector(gestureListener, this)
    private var worldRenderer: WorldRenderer?
    private var win = false
    private var nextLevelX = 0
    private var nextLevelY = 0
    private var level: Level
    private var gameOverTime = 0f
    private var animationTime = 0.3f

    private var shouldShowTutorial = true

    private var paused = true

    private var active = false

    private var gameOver = false

    @JvmField
    var player: Player

    private var settings: Group? = null

    private var vodkaButton: SquareButton? = null

    fun activate() {
        pauseGame(false)
        this.active = true
    }

    fun isPaused() = paused

    fun getCameraPostion() = game.camera.position

    private fun updateCamera() {
        game.camera.update()
        game.camera.position.y = if (player.y < bottomCameraThreshold) {
            bottomCameraThreshold
        } else {
            player.y.coerceAtMost(topCameraThreshold)
        }
    }

    private fun getNextLevel(x: Int, y: Int): Level {
        val levelMap = worldMap.addLevel(x, y, LGCGame.getConfig())
        return CommonLevel(player, levelMap)
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    override fun render(delta: Float) {

        if (active) {
            update(delta)
            updateCamera()
            stage.act(delta)
            worldRenderer?.render(level, stage)
        }

        if (win && gameOverTime < GAME_OVER_ANIMATION_TIME) {
            gameOverTime += delta
            worldRenderer?.renderBlackScreen(gameOverTime, GAME_OVER_ANIMATION_TIME)
        } else if (win && gameOverTime >= GAME_OVER_ANIMATION_TIME) {
//            isFirstLevelPassed = true;
            player.resetPosition()
            level = getNextLevel(nextLevelX, nextLevelY)
            resetStage()
            resumeGame()
            worldRenderer?.isChessBoard = player.mushroomsCount > 5 && ThreadLocalRandom.current().nextBoolean()
            gameOverTime = 0f
            win = false
        }
    }

    private fun update(delta: Float) {
        if (player.bottlesCount > 0) {
            vodkaButton?.isVisible = true
        }

        addSpeechesToStage(level.mushroomsSpeeches)
        addSpeechesToStage(level.forestersSpeeches)
        updateWorld(delta)
    }

    private fun updateWorld(delta: Float) {
        if (!isGameOver() && player.y >= yLimit) {
            nextLevelY++
            win = true
            return
        } else if (!isGameOver() && player.x >= xLimit) {
            nextLevelX++
            win = true
            return
        }
        game.camera.position.y.let {
            level.update(delta, this)
        }
    }

    private fun addSpeechesToStage(speeches: Array<Label?>) {
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
        for (tree in treesManager.getTopBorderNonObstaclesTrees()) {
            stage.addActor(tree)
        }
        stage.addActor(player)
        for (tree in treesManager.getBottomBorderNonObstaclesTrees()) {
            stage.addActor(tree)
        }
        for (i in 0 until level.foresters.size) {
            stage.addActor(level.foresters[i])
        }

        (level as? FirstLevel)?.let {
            stage.addActor(it.grandma)
            stage.addActor(it.grandmaLabel)
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
        val overlay = Overlay(game.camera)

        val gameOverLogo = GameOverLogo(game.camera)
        val settingsButton = SquareButton(
                getRegion(SETTINGS_PRESSED),
                getRegion(SETTINGS),
                game.camera
        )

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
                    addAction(Actions.moveTo(0F, 0F, animationTime))
                }

                stage.addActor(settings)
            }
        }

        restartGameButton.onTouchHandler = {
            if (settings == null) {
                val worldMap = WorldMap()
                game.showGameScreen(GameScreen(game, worldMap.addLevel(0, 0,
                        Config(LGCGame.LEVEL_WIDTH, LGCGame.LEVEL_HEIGHT)), worldMap))
            }
        }

        stage.addActor(overlay)
        stage.addActor(settingsButton)
        stage.addActor(gameOverLogo)
        stage.addActor(MushroomsCountView(game.camera, player.mushroomsCount))
        stage.addActor(restartGameButton)
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
                val worldMap = WorldMap()
                game.showGameScreen(GameScreen(game, worldMap.addLevel(0, 0,
                        Config(LGCGame.LEVEL_WIDTH, LGCGame.LEVEL_HEIGHT)), worldMap))
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
                    addAction(Actions.moveTo(0F, 0F, animationTime))
                }

                stage.addActor(settings)
            }
        }

        resumeGameButton.onTouchHandler = {
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
        )

        pauseButton.onTouchHandler = {
            pauseButton.remove()
            pauseGame(true)
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
            }
        }
        stage.addActor(pauseButton)
        stage.addActor(vodkaButton)

        if (shouldShowTutorial && !LGCGame.firstLevelPassed()) {
            shouldShowTutorial = false

            val overlay = Overlay(game.camera)
            val dialog = Dialog(game.camera)

            stage.addActor(overlay)
            stage.addActor(dialog)

            dialog.disposeHandler = {
                dialog.addAction(Actions.run {
                    dialog.remove()
                    overlay.remove()
                })
            }
        }
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

fun isGameOver() = gameOver || win

companion object {
    private const val GAME_OVER_ANIMATION_TIME = 0.5f
    private const val MIN_FRAME_LENGTH = 1f / 60f
    const val SPEECH_SEED = 768

    @JvmField
    var tileSize = 0

    // Если позиция игрока больше одной из этих двух координат, н переходит на следующую локацию
    var xLimit: Float = 0f
    var yLimit: Float = 0f

    // Если позиция камеры выходит за рамки этих значений, камера перестает двигаться
    var bottomCameraThreshold: Float = 0f
    var topCameraThreshold: Float = 0f
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
    sequenceAction.addAction(Actions.moveTo(Gdx.graphics.width.toFloat(), 0f, animationTime))
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

    stage.touchUp(x, y, pointer, button)
    if (actorIsTouched) return true


    if (x < 40 * Gdx.graphics.density && settings != null && settings?.actions?.isEmpty == true) {
        removeSettings()
        return true
    }

    if (!isGameOver()) {
        level.movePlayerTo(x.toFloat(), y.mapYToLevel(), player)
    }

    return true
}

private fun Int.mapYToScreen(): Float = Gdx.graphics.height - 1f - this;
private fun Int.mapYToLevel(): Float = game.camera.position.y - Gdx.graphics.height / 2f + mapYToScreen()

init {
    Gdx.input.inputProcessor = gestureDetector
    Gdx.input.isCatchBackKey = true
    worldRenderer = WorldRenderer(game.camera, spriteBatch)

    if (LGCGame.firstLevelPassed()) {
        player = Player(2f * tileSize, 2f * tileSize)
        level = CommonLevel(player, levelMap)
    } else {
        player = Player(tileSize * 5f, tileSize * 9f)
        level = FirstLevel(levelMap)
    }
    resetStage()
}
}