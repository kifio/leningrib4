package kifio.leningrib.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashSet;
import java.util.Set;
import kifio.leningrib.model.actors.MovableActor;
import org.jetbrains.annotations.NotNull;

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
	private TextureAtlas playerAtlas;
	private @NotNull String packFile;
	private float frameDuration;

	private UIState(@NotNull String packFile, float frameDuration) {
		this.packFile = packFile;
		this.frameDuration = frameDuration;
		this.playerAtlas = new TextureAtlas(packFile.concat(".txt"));
		this.actorAnimation = new Animation<>(frameDuration, playerAtlas.getRegions());
	}

	public void setPlayerColorizedAnimation(int pixelColor, Color newColor) {
		int count = playerAtlas.getRegions().size;

		Pixmap pixmap;
		Texture texture = ResourcesManager.getTexture(packFile);
		TextureData textureData = texture.getTextureData();
		TextureRegion[] regions = new TextureRegion[count];

		textureData.prepare();
		pixmap = textureData.consumePixmap();
		pixmap.setColor(newColor);
		updatePixmap(pixmap, pixelColor);

		int x = 16;
		int y = 0;
		int w = 16;
		int h = 24;

		texture = new Texture(pixmap);

		for (int i = 0; i < count; i++) {
			regions[i] = new TextureRegion(texture, x * i, y, w, h);
		}

		pixmap.dispose();
		actorAnimation = new Animation<>(frameDuration, regions);
	}

	private void updatePixmap(Pixmap pixmap, int pixelColor) {
		for (int i = 0; i < pixmap.getWidth(); i++) {
			for (int j = 0; j < pixmap.getHeight(); j++) {
				if (pixmap.getPixel(i, j) == pixelColor) {
					pixmap.drawPixel(i, j);
				}
			}
		}
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
