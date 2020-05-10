package kifio.leningrib.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import kifio.leningrib.LGCGame
import kifio.leningrib.Utils
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Mushroom
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.model.actors.tutorial.Grandma
import kifio.leningrib.screens.GameScreen


class WorldRenderer(private var camera: OrthographicCamera?,
                    private val batch: SpriteBatch) {

    private val renderer: ShapeRenderer = ShapeRenderer()
    private val playerDebugColor = Color(0f, 0f, 1f, 0.5f)
    private val playerPathDebugColor = Color(0f, 0f, 1f, 1f)
    private val foresterDebugColor = Color(1f, 0f, 0f, 0.5f)
    private val grass0 = ResourcesManager.getRegion(ResourcesManager.GRASS_0)
    private val grass2 = ResourcesManager.getRegion(ResourcesManager.GRASS_2)
    var isChessBoard: Boolean = false

    fun renderBlackScreen(currentTime: Float,
                          maximumTime: Float,
                          inverted: Boolean) {
        var alpha = (currentTime / maximumTime).coerceAtMost(1f)
        if (inverted) alpha = 1f - alpha
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera!!.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.setColor(0f, 0f, 0f, alpha)
        renderer.rect(0f, camera!!.position.y - Gdx.graphics.height / 2f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        renderer.end()
    }

    fun render(level: Level, stage: Stage) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        drawGrass()
        drawDebug(level)
        stage.draw()
    }

    private fun drawDebug(level: Level) {
        if (!LGCGame.isDebug) return

        // Включаем поддержку прозрачности
//        Gdx.gl.glEnable(GL20.GL_BLEND)
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//        renderer.projectionMatrix = camera!!.combined
//        renderer.begin(ShapeRenderer.ShapeType.Filled)
//
//        drawPlayerPath(level.player);
//        drawGrid();
//		  drawCharacterDebug();
//		  drawGrandmaDebug();
//        drawMushroomsBounds();
//        drawForesterDebug(level.foresters);
//        Gdx.gl.glDisable(GL20.GL_BLEND)
//        renderer.end()
    }

    private fun drawGrid() {
        run {

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
//        for (forester in foresters) {
//            drawForesterPath(forester)
//        }

//        for (Forester forester : foresters) {
//            drawForesterArea(forester);
//        }
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

    private val grassSize = GameScreen.tileSize * 2
    private val halfHeight = Gdx.graphics.height / 2

    private fun drawGrass() {
        camera?.let {

            val bottom = (it.position.y - halfHeight).toInt() / grassSize
            val top = (it.position.y + halfHeight).toInt() / grassSize

            batch.projectionMatrix = camera!!.combined
            batch.begin()

            for (x in 0 until Gdx.graphics.width step grassSize) {
                for (y in bottom .. top) {
                    batch.draw(
                            if (isChessBoard) getRegion(x, y, (grassSize* 2)) else grass2,
                            x.toFloat(),
                            y.toFloat() * grassSize,
                            grassSize.toFloat(),
                            grassSize.toFloat())
                }
            }
            batch.end()
        }
    }

    private fun getRegion(x: Int, y: Int, foo: Int): TextureRegion {
        if (y % foo == 0) {
            if (x % foo == 0) {
                return grass0
            } else {
                return grass2
            }
        } else {
            if (x % foo == 0) {
                return grass2
            } else {
                return grass0
            }
        }
    }

    fun dispose() {
        batch.dispose()
        renderer.dispose()
        camera = null
    }
}