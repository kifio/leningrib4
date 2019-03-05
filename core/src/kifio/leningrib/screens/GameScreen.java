package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import kifio.leningrib.LGCGame;
import kifio.leningrib.model.World;
import kifio.leningrib.view.WorldRenderer;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen {
    
	private LGCGame game;
	private World world;
	private WorldRenderer worldRenderer;

	public GameScreen(LGCGame game) {
		this.game = game;
		this.world = new World();
		this.worldRenderer = new WorldRenderer(game, world);
	}

	@Override
	public void render(float delta) {
		worldRenderer.render();
	}

	@Override
	public void dispose() {
		worldRenderer = null;
		game = null;
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
