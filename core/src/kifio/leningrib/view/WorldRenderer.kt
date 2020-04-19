package kifio.leningrib.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import generator.Config
import kifio.leningrib.LGCGame
import kifio.leningrib.levels.Level
import kifio.leningrib.model.GameOverDisplay
import kifio.leningrib.model.HeadsUpDisplay
import kifio.leningrib.model.PauseDisplay
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Grandma
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.speech.LabelManager
import kifio.leningrib.screens.GameScreen

class WorldRenderer(private var camera: OrthographicCamera?,
                    private val config: Config,
                    private val batch: SpriteBatch) {

    private val renderer: ShapeRenderer = ShapeRenderer()
    private val playerDebugColor = Color(0f, 0f, 1f, 0.5f)
    private val playerPathDebugColor = Color(0f, 0f, 1f, 1f)
    private val foresterDebugColor = Color(1f, 0f, 0f, 0.5f)
    private val grass = ResourcesManager.getRegion(ResourcesManager.GRASS_0)

    fun renderBlackScreen(gameOverDisplay: GameOverDisplay?,
                          gameOverTime: Float,
                          gameOverAnimationTime: Float,
                          level: Level,
                          stage: Stage) {
        render(level, stage, null, null)
        val alpha = (gameOverTime / gameOverAnimationTime).coerceAtMost(1f)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera!!.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.setColor(0f, 0f, 0f, alpha)
        renderer.rect(0f, camera!!.position.y - Gdx.graphics.height / 2f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        renderer.end()
        gameOverDisplay?.let { drawGameOverText(it) }
    }

    private fun drawGameOverText(it: GameOverDisplay) {
        batch.begin()
        LabelManager.getInstance().bitmapFont.draw(batch, it.label, it.labelX, it.labelY)
        batch.draw(it.menu, it.menuX, it.menuY, it.menuSize, it.menuSize)
        batch.draw(it.restart, it.restartX, it.restartY, it.restartSize, it.restartSize)
        batch.end()
    }

    fun render(level: Level,
               stage: Stage,
               headsUpDisplay: HeadsUpDisplay?,
               pauseDisplay: PauseDisplay?) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        drawGrass()
        drawDebug(level)
        stage.draw()

        batch.projectionMatrix = camera!!.combined
        batch.begin()

        headsUpDisplay?.let { drawHUD(it) }
        pauseDisplay?.let { drawPauseInterface(it) }

        batch.end()
    }

    private fun drawPauseInterface(pauseDisplay: PauseDisplay) {
        batch.draw(pauseDisplay.resume,
                pauseDisplay.resumeX, pauseDisplay.resumeY,
                pauseDisplay.resumeWidth, pauseDisplay.resumeHeight)
        batch.draw(pauseDisplay.menu,
                pauseDisplay.menuX, pauseDisplay.menuY,
                pauseDisplay.menuWidth, pauseDisplay.menuHeight)
    }

    private fun drawHUD(headsUpDisplay: HeadsUpDisplay) {
        val pauseRectangle = headsUpDisplay.getPauseButtonPosition()
        val bottleRectangle = headsUpDisplay.getItemsPositions()[0]

        batch.draw(headsUpDisplay.getBackgroundTexture(),
                pauseRectangle.x, pauseRectangle.y,
                pauseRectangle.width, pauseRectangle.height)

        batch.draw(headsUpDisplay.getBackgroundTexture(),
                bottleRectangle.x, bottleRectangle.y,
                bottleRectangle.width, bottleRectangle.height)

        batch.draw(
                headsUpDisplay.getPauseButtonTexture(),
                pauseRectangle.x, pauseRectangle.y,
                pauseRectangle.width, pauseRectangle.height)

        batch.draw(
                headsUpDisplay.getBottleTexture(),
                bottleRectangle.x, bottleRectangle.y,
                bottleRectangle.width, bottleRectangle.height)
    }

    private fun drawDebug(level: Level) {
        if (!LGCGame.isDebug) return

        // Включаем поддержку прозрачности
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera!!.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)

//        drawPlayerPath(level.player);
//        drawGrid();
//		  drawCharacterDebug();
//		  drawGrandmaDebug();
//        drawMushroomsBounds();
//        drawForesterDebug(level.foresters);
        Gdx.gl.glDisable(GL20.GL_BLEND)
        renderer.end()
    }

    private fun drawGrid() {
        run {
            var i = 0
            while (i < Gdx.graphics.width) {
                renderer.line(i.toFloat(), 0f, i.toFloat(), Gdx.graphics.height.toFloat())
                i += GameScreen.tileSize
            }
        }
        var i = 0
        while (i < Gdx.graphics.height) {
            renderer.line(0f, i.toFloat(), Gdx.graphics.width.toFloat(), i.toFloat())
            i += GameScreen.tileSize
        }
    }

    private fun drawPlayerPath(player: Player) {
        renderer.color = playerPathDebugColor
        for (vec in player.path) {
            renderer.rect(vec.x, vec.y, GameScreen.tileSize.toFloat(), GameScreen.tileSize.toFloat())
        }
    }

    private fun drawMushroomsBounds(mushrooms: Array<Mushroom>) {
        renderer.color = playerPathDebugColor
        for (m in mushrooms) {
            if (m != null) {
                renderer.rect(m.bounds.x, m.bounds.y, m.bounds.width, m.bounds.height)
            }
        }
    }

    private fun drawCharacterDebug(player: Player) {
        renderer.color = playerDebugColor
        val bounds = player.bounds
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    private fun drawGrandmaDebug(grandma: Grandma?) {
        if (grandma == null) {
            return
        }
        renderer.color = playerDebugColor
        val bounds = grandma.bounds
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    private fun drawForesterDebug(foresters: Array<Forester>) {
        for (forester in foresters) {
            drawForesterPath(forester)
        }
//        		for (Forester forester : foresters) { drawForesterArea(forester); }
    }

    private fun drawExitRect() {
        renderer.color = playerDebugColor
        renderer.rect(GameScreen.tileSize * 3f, GameScreen.tileSize * 24f, GameScreen.tileSize.toFloat(), GameScreen.tileSize.toFloat())
        renderer.rect(GameScreen.tileSize * 4f, GameScreen.tileSize * 24f, GameScreen.tileSize.toFloat(), GameScreen.tileSize.toFloat())
    }

    private fun drawForesterPath(forester: Forester) {
        renderer.color = foresterDebugColor
        for (vec in forester.path) {
            renderer.rect(vec.x, vec.y, GameScreen.tileSize.toFloat(), GameScreen.tileSize.toFloat())
        }
    }

//    private fun drawForesterArea(forester: Forester) {
//        var r = forester.pursueArea
//        renderer.color = foresterDebugColor
//        renderer.rect(r.x, r.y, r.width, r.height)
//        r = forester.noticeArea
//        renderer.color = playerDebugColor
//        renderer.rect(r.x, r.y, r.width, r.height)
//    }

    private fun drawGrass() {
        camera?.let {
            val grassSize = GameScreen.tileSize * 2
            val height = Gdx.graphics.height
            val bottom = it.position.y - (height / 2) - grassSize
            val top = it.position.y + (height / 2)

            batch.projectionMatrix = camera!!.combined
            batch.begin()

            for (x in 0 .. (config.levelWidth * GameScreen.tileSize) step grassSize) {
                for (y in 0 .. (config.levelHeight * GameScreen.tileSize) step grassSize) {
                    if (y in bottom..top) {
                        batch.draw(grass, x.toFloat(), y.toFloat(), grassSize.toFloat(), grassSize.toFloat())
                    }
                }
            }
            batch.end()
        }
    }

    fun dispose() {
        batch.dispose()
        renderer.dispose()
        camera = null
    }
}