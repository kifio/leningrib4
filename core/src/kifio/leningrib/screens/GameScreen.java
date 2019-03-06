package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import kifio.leningrib.LGCGame;
import com.badlogic.gdx.graphics.*;
import kifio.leningrib.controller.*;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import kifio.leningrib.view.WorldRenderer;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends InputAdapter implements Screen {
    
	private LGCGame game;
	private WorldRenderer worldRenderer;
	private WorldController worldController;

	public GameScreen(LGCGame game) {
		Gdx.input.setInputProcessor(this);
		this.game = game;
		this.worldController = new WorldController();
		this.worldRenderer = new WorldRenderer(game, worldController);
	}

	@Override
	public void render(float delta) {
		worldRenderer.render();
	}

	@Override
	public void dispose() {
		if (worldRenderer != null) {
			worldRenderer.dispose();
			worldRenderer = null;
		}
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

	@Override 
	public boolean touchDown(int x, int y, int pointer, int button) {
        return super.touchDown(x, y, pointer, button);
    }

    @Override 
    public boolean touchUp(int x, int y, int pointer, int button) {
        worldController.movePlayerTo((float) x, (float) (Gdx.graphics.getHeight() - y));
        return true;
    }

}
