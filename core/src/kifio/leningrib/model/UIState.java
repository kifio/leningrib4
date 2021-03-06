package kifio.leningrib.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import kifio.leningrib.model.actors.game.MovableActor;

public class UIState {

	private static Set<UIState> pool = new HashSet<>();

	public static UIState obtainUIState(String packFile, MovableActor actor) {
		for (UIState state : pool) {
			if (state.packFile.equals(packFile)) { return state; }
		}

		UIState state = new UIState(packFile, actor.getFrameDuration());
		pool.add(state);
		return state;
	}

	private Animation<? extends TextureRegion> actorAnimation;
	private @NotNull String packFile;
	private float frameDuration;
	private int regionsCount;

	private UIState(@NotNull String packFile, float frameDuration) {
		this.frameDuration = frameDuration;
		setPackFile(packFile);
	}

	public void setPackFile(@NotNull String packFile) {
		this.packFile = packFile;
		TextureAtlas playerAtlas = new TextureAtlas(packFile.concat(".txt"));
		regionsCount = playerAtlas.getRegions().size;
		this.actorAnimation = new Animation<>(frameDuration, playerAtlas.getRegions());
	}

	public void setTextureRegions(TextureRegion[] regions) {
		actorAnimation = new Animation<>(frameDuration, regions);
	}

	public int getRegionsCount() {
		return regionsCount;
	}

	public Texture getTexture() {
		return ResourcesManager.getTexture(packFile);
	}

	@Override public boolean equals(Object o) {
		if (!(o instanceof UIState)) { return false; } else if (o == this) { return true; } else {
			return ((UIState) o).packFile.equals(this.packFile);
		}
	}

	@Override public int hashCode() {
		return packFile.hashCode();
	}

	public @NotNull String getPackFile() {
		return packFile;
	}

	public @NotNull Animation<? extends TextureRegion> getAnimation() {
		return actorAnimation;
	}
}
