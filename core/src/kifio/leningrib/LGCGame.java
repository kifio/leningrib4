package kifio.leningrib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import kifio.leningrib.model.TextureManager;
import kifio.leningrib.screens.GameScreen;

public class LGCGame extends Game {

	private Screen currentScreen;
	public SpriteBatch batch;
	public ShapeRenderer renderer;
    public int cameraWidth;
    public int cameraHeight;
    public int tileSize;
    public int width;
    public int height;

    @Override
	public void create () {
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		TextureManager.init();
		initScreenSize();
		currentScreen = new GameScreen(this);
		setScreen(currentScreen);
	}

    private void initScreenSize() {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        cameraWidth = 5;
        tileSize = (width / cameraWidth) + 1;
        cameraHeight = (height / tileSize) + 1;
    }

	public void onLevelPassed() {
		if (currentScreen instanceof GameScreen) {
			((GameScreen) currentScreen).onLevelPassed(this);
		}
	}

    @Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
