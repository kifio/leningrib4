package kifio.leningrib.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import kifio.leningrib.levels.Level
import kifio.leningrib.model.HeadsUpDisplay
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Forester
import kifio.leningrib.model.actors.Grandma
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.Player
import kifio.leningrib.model.speech.SpeechManager
import kifio.leningrib.screens.GameScreen
import java.util.*

class WorldRenderer(private var camera: OrthographicCamera?,
                    private val levelWidth: Int,
                    cameraHeight: Int,
                    private val batch: SpriteBatch) {

    private val debug = false
    private val renderer: ShapeRenderer = ShapeRenderer()
    private val grassCount: Int = levelWidth * (cameraHeight + 2)
    private val playerDebugColor = Color(0f, 0f, 1f, 0.5f)
    private val playerPathDebugColor = Color(0f, 0f, 1f, 1f)
    private val foresterDebugColor = Color(1f, 0f, 0f, 0.5f)
    private val grass = ResourcesManager.getRegion(ResourcesManager.GRASS_0)

    fun renderBlackScreen(levelPassed: Boolean,
                          gameOverTime: Float,
                          gameOverAnimationTime: Float,
                          level: Level,
                          stage: Stage) {
        render(level, stage, null)
        val alpha = (gameOverTime / gameOverAnimationTime).coerceAtMost(1f)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera!!.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.setColor(0f, 0f, 0f, alpha)
        renderer.rect(0f,
                camera!!.position.y - Gdx.graphics.height / 2f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        renderer.end()
        if (!levelPassed) {
            drawGameOverText(level.player.mushroomsCount)
        }
    }

    private fun drawGameOverText(mushroomsCount: Int) {
        batch.begin()
        val text = String.format(Locale.getDefault(), GAME_OVER_TEXT, mushroomsCount)
        val speechManager = SpeechManager.getInstance()
        val x = Gdx.graphics.width / 2f - SpeechManager.getTextWidth(text) / 2
        val y = camera!!.position.y - SpeechManager.getTextHeight(text) / 2
        speechManager.bitmapFont.draw(batch, text, x, y)
        batch.end()
    }

    fun render(level: Level,
               stage: Stage,
               headsUpDisplay: HeadsUpDisplay?) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        drawGrass()
        drawDebug(level)
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
        headsUpDisplay?.let { drawHUD(it) }
    }

    private fun drawHUD(headsUpDisplay: HeadsUpDisplay) {
        batch.projectionMatrix = camera!!.combined

        val pauseRectangle = headsUpDisplay.getPauseButtonPosition()
        val bottleRectangle = headsUpDisplay.getItemsPositions()[0]

        batch.begin()
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

        batch.end()
    }

    private fun drawDebug(level: Level) {
        if (!debug) {
            return
        }
        // Включаем поддержку прозрачности
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera!!.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)

        // drawPlayerPath();
//        drawGrid();
//		  drawCharacterDebug();
//		  drawGrandmaDebug();
//        drawMushroomsBounds();
        // drawForesterDebug();
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
        //		for (Forester forester : foresters) { drawForesterArea(forester); }
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

    private fun drawForesterArea(forester: Forester) {
        var r = forester.pursueArea
        renderer.color = foresterDebugColor
        renderer.rect(r.x, r.y, r.width, r.height)
        r = forester.noticeArea
        renderer.color = playerDebugColor
        renderer.rect(r.x, r.y, r.width, r.height)
    }

    private fun drawGrass() {
        batch.projectionMatrix = camera!!.combined
        batch.begin()
        for (i in 0 until grassCount) {
            val x = GameScreen.tileSize * (i % levelWidth)
            val y = GameScreen.tileSize * (i / levelWidth) + (camera!!.position.y - (Gdx.graphics.height / 2 + GameScreen.tileSize)).toInt()
            batch.draw(grass, x.toFloat(), y.toFloat(), GameScreen.tileSize.toFloat(), GameScreen.tileSize.toFloat())
        }
        batch.end()
    }

    fun dispose() {
        batch.dispose()
        renderer.dispose()
        camera = null
    }

    companion object {
        private const val GAME_OVER_TEXT = "ЯДРЕНА КОЧЕРЫЖКА\nТЫ СОБРАЛ %s ГРИБОВ"
    }

}