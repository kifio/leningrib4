package kifio.leningrib.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

import kifio.leningrib.LGCGame;
import kifio.leningrib.controller.WorldController;
import kifio.leningrib.levels.FirstLevel;
import kifio.leningrib.levels.Level;
import kifio.leningrib.view.WorldRenderer;

public class GameScreen extends InputAdapter implements Screen {
    
	private WorldRenderer worldRenderer;
	private WorldController worldController;
    private OrthographicCamera camera;

	public GameScreen(LGCGame game) {
		Gdx.input.setInputProcessor(this);
		initCamera();
		this.worldController = new WorldController(game);
		this.worldRenderer = new WorldRenderer(game, worldController, camera, getNextLevel(game));
	}

    private void initCamera() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        // create the camera and the game.batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
    }

	private Level getNextLevel(LGCGame game) {
		return new FirstLevel(game);
	}

	public void onLevelPassed(LGCGame game) {
		this.worldRenderer.resetStage(getNextLevel(game));
	}

	@Override
	public void render(float delta) {
		worldController.update();
		worldRenderer.render();
	}

	@Override
	public void dispose() {
		if (worldRenderer != null) {
			worldRenderer.dispose();
			worldRenderer = null;
		}
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
	    Gdx.app.log("kifio", camera.position.toString());
        worldController.movePlayerTo(
                camera.position.x - (Gdx.graphics.getWidth() / 2f) + x,
                camera.position.y - (Gdx.graphics.getHeight() / 2f) + (Gdx.graphics.getHeight() - y));
        return true;
    }
}
