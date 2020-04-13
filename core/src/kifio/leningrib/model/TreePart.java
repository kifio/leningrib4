package kifio.leningrib.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

// Дерево разделено на 4 части,
// чтобы иметь возможность располагать лесные массивы по углам экрана
public class TreePart extends Actor {

	private TextureRegion treeTexture;
	public Vector2 position;

	public TreePart(TextureRegion textureRegion, float x, float y, int width, int height) {
		treeTexture = textureRegion;
		position = new Vector2(x, y);
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
	}

	@Override
    public void draw(Batch batch, float alpha){
		batch.draw(treeTexture, getX(), getY(), getWidth(), getHeight());
    }

    public boolean is(Rectangle bounds) {

		float xmin = bounds.x;
		float xmax = xmin + bounds.width;

		float ymin = bounds.y;
		float ymax = ymin + bounds.height;

		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();

		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
				&& ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
	}
}