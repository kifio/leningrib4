package kifio.leningrib.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import kifio.leningrib.LGCGame
import kifio.leningrib.Utils
import kifio.leningrib.levels.Level
import kifio.leningrib.levels.LevelFabric
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.ResourcesManager.*
import kifio.leningrib.model.actors.Overlay
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.model.actors.game.SquareButton
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.game.StartGameButton
import kifio.leningrib.model.items.Bottle
import kifio.leningrib.view.WorldRenderer
import model.LevelMap
import model.WorldMap
import java.util.concurrent.ThreadLocalRandom

class GameScreen(game: LGCGame,
                 var worldMap: WorldMap,
                 levelMap: LevelMap    // Уровень конструируется с координатами 0,0. Карта уровня долго генерируется первый раз, передаем ее снаружи.
) : BaseScreen(game) {

    private var worldRenderer: WorldRenderer?
    private val bottles = ArrayList<Bottle>()
    private var win = false
    private var nextLevelX = 0
    private var nextLevelY = 0
    private var level: Level
    private var gameOverTime = 0f

    private var paused = true

    private var active = false

    @JvmField
    var gameOver = false

    @JvmField
    var isFirstLevelPassed = true

    @JvmField
    var player: Player


    fun activate() {
        pauseGame()
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
        if (isGameOver() && gameOverTime < GAME_OVER_ANIMATION_TIME) {
            gameOverTime += delta
            update(delta)
            if (!win) {
//                gameOverDisplay = GameOverDisplay(player.mushroomsCount, getCameraPositionY())
            }
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
        } else if (!gameOver) {
            updateCamera(level.player)
            update(delta)
            stage.act(Gdx.graphics.deltaTime)
            if (active) {
                worldRenderer?.render(level, stage)
            }
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
    }

    private fun pauseGame() {
        paused = true
        val overlay = Overlay(game.camera)
        val startGameButton = StartGameButton(
                game.camera,
                getRegion(ResourcesManager.START_GAME_PRESSED),
                getRegion(ResourcesManager.START_GAME)
        )

        val settingsButton = SquareButton(
                getRegion(SETTINGS_PRESSED),
                getRegion(SETTINGS),
                game.camera
        )

        settingsButton.onTouchHandler = {

        }

        startGameButton.onTouchHandler = {
            settingsButton.remove()
            overlay.remove()
            startGameButton.remove()
            resumeGame()
        }

        stage.addActor(overlay)
        stage.addActor(settingsButton)
        stage.addActor(startGameButton)
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
            pauseGame()
        }

        stage.addActor(pauseButton)
    }

    override fun resize(width: Int, height: Int) {}
    override fun show() {}
    override fun hide() {}



    override fun pause() {

    }

    override fun resume() {

    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        stage.touchDown(x, y, pointer, button)
        return super.touchDown(x, y, pointer, button)
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        val actorIsTouched = stage.actors
                .filter { it is StaticActor }
                .find { (it as StaticActor).touched } != null
        stage.touchUp(x, y, pointer, button)
        if (actorIsTouched) return true
        if (!isGameOver()) {
            level.movePlayerTo(x.mapX(), y.mapYToLevel())
        } else if (gameOverTime > GAME_OVER_ANIMATION_TIME) {
//            game?.showGameScreen(worldMap, levelMap)
//            dispose()
        }
        return true
    }

    private fun Int.mapX(): Float = this.toFloat()
    private fun Int.mapYToScreen(): Float = Gdx.graphics.height - 1f - this;
    private fun Int.mapYToLevel(): Float = getCameraPositionY() - Gdx.graphics.height / 2f + mapYToScreen()

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

    init {
        Gdx.input.inputProcessor = this
        worldRenderer = WorldRenderer(game.camera, spriteBatch)
        if (isFirstLevelPassed) {
            player = Player(2f * tileSize, 2f * tileSize)
        } else {
            player = Player(2f * tileSize, 0f)
        }
        level = LevelFabric.getNextLevel(0, 0, this, levelMap)
        resetStage()
    }
}