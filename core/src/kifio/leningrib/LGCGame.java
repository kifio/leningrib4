package kifio.leningrib;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kifio.leningrib.screens.GameScreen;

public class LGCGame extends Game {

	public SpriteBatch batch;
	public AssetManager am;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		am = new AssetManager();
		am.load("overworld.png", Texture.class);
		am.finishLoading();
		setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public Texture getTexture(String name) {
		return am.get("overworld.png");
	}
}
