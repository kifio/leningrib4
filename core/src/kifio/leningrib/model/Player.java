package kifio.leningrib.model;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {

	private Animation playerAnimation;

	public int x;
	public int y;

	public Player() {
		TextureAtlas playerAtlas = new TextureAtlas("player.txt");
		this.playerAnimation = new Animation<>(1/15f, playerAtlas.getRegions());
	}

	public TextureRegion getTextureRegion(float elapsedTime) {
		return (TextureRegion) playerAnimation.getKeyFrame(elapsedTime, true);
	}

}