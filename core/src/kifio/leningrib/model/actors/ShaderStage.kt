package kifio.leningrib.model.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import kifio.leningrib.LUTController

import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.screens.GameScreen

class ShaderStage(
        viewport: Viewport,
        batch: Batch,
        var lutController: LUTController) : Stage(viewport, batch) {

    private var isChessBoard: Boolean = false
    private val grass0 = ResourcesManager.getRegion(ResourcesManager.GRASS_0)
    private val grass2 = ResourcesManager.getRegion(ResourcesManager.GRASS_2)

    override fun draw() {
        val batch = super.getBatch();
        val lutController = lutController

        batch.projectionMatrix = camera.combined
        batch.begin();
        batch.shader = null

        if (lutController.lutTexture != null) {
            batch.shader = lutController.shader
            drawGrass()
            root.draw(batch, 1f);
            lutController.onDraw()
        } else {
            drawGrass()
            root.draw(batch, 1f);
        }

        batch.end();
    }

    private val grassSize = GameScreen.tileSize * 2
    private val halfHeight = Gdx.graphics.height / 2

    private fun drawGrass() {
        camera?.let {

            val bottom = (it.position.y - halfHeight).toInt() / grassSize
            val top = (it.position.y + halfHeight).toInt() / grassSize

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
}