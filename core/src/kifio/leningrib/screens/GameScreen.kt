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
import kifio.leningrib.levels.Level
import kifio.leningrib.levels.LevelFabric
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
                 var worldMap: WorldMap,
                 levelMap: LevelMap    // Уровень конструируется с координатами 0,0. Карта уровня долго генерируется первый раз, передаем ее снаружи.
) : BaseScreen(game) {

    private val gestureListener: LInputListener? = LInputListener(this)
    private val gestureDetector: LGestureDetector? = LGestureDetector(gestureListener, this)
    private var worldRenderer: WorldRenderer?
    private val bottles = ArrayList<Bottle>()
    private var win = false
    private var nextLevelX = 0
    private var nextLevelY = 0
    private var level: Level
    private var gameOverTime = 0f
    private var animationTime = 0.3f

    private var paused = true

    private var active = false

    private var gameOver = false

    @JvmField
    var isFirstLevelPassed = true

    @JvmField
    var player: Player

    private var settings: Group? = null

    fun activate() {
        pauseGame(false)
        this.active = true
    }

    private fun updateCamera(player: Player) {
        game.camera.update()
        game.camera.position.y = if (player.y < bottomCameraThreshold) {
            bottomCameraThreshold
        } else {
            player.y.coerceAtMost(topCameraThreshold)
        }
    }

    private fun getNextLevel(x: Int, y: Int): Level {
        val levelMap = worldMap.addLevel(x, y, LGCGame.getConfig())
        return LevelFabric.getNextLevel(x, y, this, levelMap)
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    override fun render(delta: Float) {

        if (active) {
            update(delta)
            updateCamera(level.player)
            stage.act(delta)
            worldRenderer?.render(level, stage)
        }

        if (win && gameOverTime < GAME_OVER_ANIMATION_TIME) {
            gameOverTime += delta
            worldRenderer?.renderBlackScreen(gameOverTime, GAME_OVER_ANIMATION_TIME, level, stage)
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
        for (b in bottles) {
            if (b.isRemovable()) {
                b.remove()
                bottles.remove(b)
            }
        }
        if (level.grandma != null) {
            if (level.grandma.isReadyForDialog(level.player)) {
                level.grandma.startDialog()
                stage.addActor(level.grandma.grandmaLabel)
            }
        }
        addSpeechesToStage(level.mushroomsSpeeches)
        addSpeechesToStage(level.forestersSpeeches)
        updateWorld(delta)
    }

    private fun updateWorld(delta: Float) {
        if (!isGameOver() && level.player.y >= yLimit) {
            nextLevelY++
            win = true
            return
        } else if (!isGameOver() && level.player.x >= xLimit) {
            nextLevelX++
            win = true
            return
        }
        game.camera.position.y.let {
            level.update(delta, bottles, it, paused)
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
        if (level.tutorialLabels != null) {
            for (l in level.tutorialLabels) {
                stage.addActor(l)
            }
        }
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
        stage.addActor(level.player)
        for (tree in treesManager.getBottomBorderNonObstaclesTrees()) {
            stage.addActor(tree)
        }
        if (level.grandma != null) {
            stage.addActor(level.grandma)
        }
        for (i in 0 until level.foresters.size) {
            stage.addActor(level.foresters[i])
        }
//		if (level.getGrandma() != null) {
//			stage.addActor(level.getGrandma().getGrandmaLabel());
//		}
        for (s in level.spaces) {
            stage.addActor(s)
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
                game.showGameScreen(GameScreen(game, worldMap, worldMap.addLevel(0, 0,
                        Config(LGCGame.LEVEL_WIDTH, LGCGame.LEVEL_HEIGHT))))
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
                game.showGameScreen(GameScreen(game, worldMap, worldMap.addLevel(0, 0,
                        Config(LGCGame.LEVEL_WIDTH, LGCGame.LEVEL_HEIGHT))))
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

        stage.addActor(pauseButton)
    }

    private fun setupVodka() {
        val playerX = Utils.mapCoordinate(player.x)
        val playerY = Utils.mapCoordinate(player.y)
        val bottle = Bottle(playerX, playerY)
        stage.addActor(bottle)
        bottles.add(bottle)
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
            level.movePlayerTo(x.toFloat(), y.mapYToLevel())
        }

        return true
    }

    private fun Int.mapYToScreen(): Float = Gdx.graphics.height - 1f - this;
    private fun Int.mapYToLevel(): Float = game.camera.position.y - Gdx.graphics.height / 2f + mapYToScreen()

    init {
        Gdx.input.inputProcessor = gestureDetector
        Gdx.input.isCatchBackKey = true
        worldRenderer = WorldRenderer(game.camera, spriteBatch)
        player = if (isFirstLevelPassed) {
            Player(2f * tileSize, 2f * tileSize)
        } else {
            Player(2f * tileSize, 0f)
        }
        level = LevelFabric.getNextLevel(0, 0, this, levelMap)
        resetStage()
    }
}