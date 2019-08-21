package kifio.leningrib.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import kifio.leningrib.model.actors.MovableActor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class UIState {

	private static Set<UIState> pool = new HashSet<>();

	public static UIState retainUIState(String packFile, MovableActor actor) {
		for (UIState state : pool) {
			if (state.packFile.equals(packFile)) { return state; }
		}

		UIState state = new UIState(packFile, actor.getFrameDuration());
		pool.add(state);
		return state;
	}

	private Animation actorAnimation;
	private TextureAtlas playerAtlas;
	private @NotNull String packFile;

	private UIState(@NotNull String packFile, float frameDuration) {
		this.packFile = packFile;
		this.playerAtlas = new TextureAtlas(packFile);
		this.actorAnimation = new Animation<>(frameDuration, playerAtlas.getRegions());
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

	public @NotNull Animation getAnimation() {
		return actorAnimation;
	}
}
