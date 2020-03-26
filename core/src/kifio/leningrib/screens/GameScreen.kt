package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ScreenViewport
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.levels.Level
import kifio.leningrib.levels.LevelFabric
import kifio.leningrib.model.GameOverDisplay
import kifio.leningrib.model.HeadsUpDisplay
import kifio.leningrib.model.actors.Player
import kifio.leningrib.view.WorldRenderer
import model.WorldMap

class GameScreen(game: LGCGame,
                 public var constantsConfig: Config) : InputAdapter(), Screen {

    private val game: LGCGame
    private var worldRenderer: WorldRenderer?
    private var camera: OrthographicCamera? = null
    private var cameraWidth = 0
    private var cameraHeight = 0
    private var win = false
    private var nextLevelX = 0
    private var nextLevelY = 0
    private var stage: Stage?
    private var level: Level?
    private var gameOverTime = 0f

    @JvmField
    var gameOver = false

    @JvmField
    var isFirstLevelPassed = true

    @JvmField
    var worldMap: WorldMap

    @JvmField
    var player: Player? = null

    // Если позиция игрока больше одной из этих двух координат, н переходит на следующую локацию
    private val xLimit: Float
    private val yLimit: Float

    // Если позиция камеры выходит за рамки этих значений, камера перестает двигаться
    private val bottomCameraThreshold: Float
    private val topCameraThreshold: Float

    private val headsUpDisplay = HeadsUpDisplay()
    private var gameOverDisplay: GameOverDisplay? = null

    // Инициализирует камеру ортгональную карте
    private fun initCamera() {
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height

        // create the camera and the batch
        camera = OrthographicCamera()
        camera!!.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    private fun updateCamera(player: Player) {
        camera!!.update()
        if (player.y < bottomCameraThreshold) {
            camera!!.position.y = bottomCameraThreshold
        } else camera!!.position.y = player.y.coerceAtMost(topCameraThreshold)
    }

    // Инициализирует размер экрана.
    // Экран разбит на квадарты, здесь задается количество квадратов по ширине,
    // в зависимости от этого рассчитывается количество кадратов по высоте
    private fun initScreenSize() {
        cameraWidth = constantsConfig.levelWidth
        tileSize = Gdx.graphics.width / cameraWidth
        cameraHeight = Gdx.graphics.height / tileSize + 1
    }

    private fun getNextLevel(x: Int, y: Int): Level {
        return LevelFabric.getNextLevel(x, y, this)
    }

    /*
        I/kifio: Delta: 0.01672223
        I/kifio: Delta: 0.015889948
        I/kifio: Delta: 0.015999753
    */
    override fun render(delta: Float) {
        if (isGameOver() && gameOverTime < GAME_OVER_ANIMATION_TIME) {
            gameOverTime += delta
            update(delta, camera!!.position.y, level, stage)
            if (!win && gameOverDisplay == null) {
                gameOverDisplay = GameOverDisplay(player?.mushroomsCount ?: 0, camera!!.position.y)
            }
            worldRenderer!!.renderBlackScreen(gameOverDisplay, gameOverTime, GAME_OVER_ANIMATION_TIME, level!!, stage!!)
        } else if (win && gameOverTime >= GAME_OVER_ANIMATION_TIME) {
//            isFirstLevelPassed = true;
            player!!.resetPosition(constantsConfig)
            level = getNextLevel(nextLevelX, nextLevelY)
            resetStage(level, stage)
            gameOverTime = 0f
            win = false
        } else if (!gameOver) {
            updateCamera(level!!.player)
            update(delta, camera!!.position.y, level, stage)
            val maxX = tileSize * constantsConfig.levelWidth.toFloat()
            val maxY = camera!!.position.y + Gdx.graphics.height / 2f
            headsUpDisplay.setPauseButtonPosition(maxX, maxY)
            headsUpDisplay.setItemsPosition(maxX, maxY, null)
            worldRenderer!!.render(level!!, stage!!, headsUpDisplay)
        }
    }

    private fun update(delta: Float, cameraPositionY: Float, level: Level?, stage: Stage?) {
        if (level!!.grandma != null) {
            if (level.grandma.isReadyForDialog(level.player)) {
                level.grandma.startDialog()
                stage!!.addActor(level.grandma.grandmaLabel)
            }
        }
        addSpeechesToStage(stage, level.mushroomsSpeeches)
        addSpeechesToStage(stage, level.forestersSpeeches)
        updateWorld(delta, cameraPositionY, level)
    }

    private fun updateWorld(delta: Float, cameraPositionY: Float, level: Level?) {
        if (!isGameOver() && level!!.player.y >= yLimit) {
            nextLevelY++
            win = true
            return
        } else if (!isGameOver() && level!!.player.x >= xLimit) {
            nextLevelX++
            win = true
            return
        }
        level!!.update(delta, cameraPositionY)
    }

    private fun addSpeechesToStage(stage: Stage?, speeches: Array<Label>) {
        for (speech in speeches) {
            if (speech != null && speech.stage == null) {
                stage!!.addActor(speech)
            }
        }
    }

    private fun resetStage(level: Level?, stage: Stage?) {
        stage!!.clear()
        if (level!!.tutorialLabels != null) {
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
            stage.addActor(level.forestersSpeeches[i])
        }
        //
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
        if (level != null) {
            level!!.dispose()
            level = null
        }
        if (stage != null) {
            stage!!.dispose()
            stage = null
        }
    }

    override fun resize(width: Int, height: Int) {}
    override fun show() {}
    override fun hide() {}
    override fun pause() {}
    override fun resume() {}

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        val touchX = x.mapX()
        val touchY = y.mapYToLevel()

        if (headsUpDisplay.getPauseButtonPosition().contains(touchX, touchY)) {
            headsUpDisplay.isPauseButtonPressed = true
        } else {
            headsUpDisplay.selectedItem = headsUpDisplay.getItemsPositions().indexOfFirst {
                it.contains(touchX, touchY)
            }
        }

        return super.touchDown(x, y, pointer, button)
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        if (!isGameOver()) {
            level!!.movePlayerTo(x.mapX(), y.mapYToLevel())
        } else if (gameOverTime > GAME_OVER_ANIMATION_TIME) {
            val touchX = x.mapX()
            val touchY = y.mapYToLevel()
            if (gameOverDisplay?.isRestartTouched(touchX, touchY) == true) {
                headsUpDisplay.isPauseButtonPressed = false
                headsUpDisplay.selectedItem = -1
                game.showGameScreen()
            } else if (gameOverDisplay?.isMenuTouched(touchX, touchY) == true) {
                game.showMenuScreen()
            }
            dispose()
        }
        return true
    }

    private fun Int.mapX(): Float = this.toFloat()
    private fun Int.mapYToScreen(): Float = Gdx.graphics.height - 1f - this;
    private fun Int.mapYToLevel(): Float = camera!!.position.y - Gdx.graphics.height / 2f + mapYToScreen()



    fun isGameOver(): Boolean {
        return gameOver || win
    }

    companion object {
        private const val GAME_OVER_ANIMATION_TIME = 0.5f
        const val SPEECH_SEED = 768

        @JvmField var tileSize = 0
    }

    init {
        Gdx.input.inputProcessor = this
        initScreenSize()
        initCamera()
        bottomCameraThreshold = Gdx.graphics.height / 2f
        topCameraThreshold = constantsConfig.levelHeight * tileSize - Gdx.graphics.height / 2f
        xLimit = Gdx.graphics.width - tileSize.toFloat()
        yLimit = (constantsConfig.levelHeight - 1) * tileSize.toFloat()
        this.game = game
        val batch = SpriteBatch()
        stage = Stage(ScreenViewport(camera), batch)
        worldRenderer = WorldRenderer(
                camera,
                constantsConfig.levelWidth,
                constantsConfig.levelWidth * (cameraHeight + 2),
                batch
        )
        worldMap = WorldMap()
        if (isFirstLevelPassed) {
            player = Player(2f * tileSize, 2f * tileSize)
        } else {
            player = Player(2f * tileSize, 0f)
        }
        level = getNextLevel(nextLevelX, nextLevelY)
        resetStage(level, stage)
    }
}