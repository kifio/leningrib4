package kifio.leningrib.model

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

// Дерево разделено на 4 части,
// чтобы иметь возможность располагать лесные массивы по углам экрана
class TreePart(private val treeTexture: TextureRegion, x: Float, y: Float, size: Int) : Actor() {

    @JvmField
	var position: Vector2 = Vector2(x, y)


    init {
        setX(x)
        setY(y)
        setWidth(size.toFloat())
        setHeight(size.toFloat())
    }

    override fun draw(batch: Batch, alpha: Float) {
        batch.draw(treeTexture, x, y, width, height)
    }

}