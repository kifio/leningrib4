package kifio.leningrib.model

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

// Дерево разделено на 4 части,
// чтобы иметь возможность располагать лесные массивы по углам экрана
class TreePart(private val treeTexture: TextureRegion,
               var alpha: Float = 1f,  // in percents
               x: Float,
               y: Float,
               size: Int) : Actor() {

    @JvmField
	var position: Vector2 = Vector2(x, y)
    val bounds: Rectangle

    init {
        setX(x)
        setY(y)
        width = size.toFloat()
        height = size.toFloat()
        bounds = Rectangle(x ,y, width, height)
    }

    override fun draw(batch: Batch, alpha: Float) {
        batch.setColor(color.r, color.g, color.b, color.a * this.alpha);
        batch.draw(treeTexture, x, y, width, height)
        batch.setColor(color.r, color.g, color.b, 1f);
    }
}