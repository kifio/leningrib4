package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import kifio.leningrib.LGCGame;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen {
    
	private LGCGame game;

	public GameScreen(LGCGame game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// game.batch.begin();
		// game.batch.draw(img, 0, 0);
		// game.batch.end();
	}

	@Override
	public void dispose() {
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
