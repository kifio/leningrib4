package kifio.leningrib.model;

import java.util.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Дерево разделено на 4 части,
// чтобы иметь возможность располагать лесные массивы по углам экрана
public class TreePart extends Actor {

	private TextureRegion treeTexture;

	public TreePart(TextureRegion textureRegion, int x, int y, int width, int heigh) {
		treeTexture = textureRegion;
		setX(x); setY(y);
		setWidth(width); setHeight(heigh);
	}

	@Override
    public void draw(Batch batch, float alpha){
		batch.draw(treeTexture, getX(), getY(), getWidth(), getHeight());
    }
}