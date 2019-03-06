package kifio.leningrib.model;

import java.util.*;
import java.lang.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class Player extends Actor {

	private Animation playerAnimation;
	
	private Pool<MoveToAction> actionPool = new Pool<MoveToAction>() {
	    protected MoveToAction newObject() {
	        return new MoveToAction();
	    }
	};
	
	private float elapsedTime = 0;
	private float velocity = 1000f;

	public Player() {
		TextureAtlas playerAtlas = new TextureAtlas("player.txt");
		this.playerAnimation = new Animation<>(1/15f, playerAtlas.getRegions());
	}

	@Override
    public void act(float delta){
        elapsedTime += delta;
        super.act(delta);
    }

	@Override
    public void draw(Batch batch, float alpha){
        TextureRegion playerTexture = (TextureRegion) playerAnimation.getKeyFrame(elapsedTime, true);
		batch.draw(playerTexture, getX(), getY(), playerTexture.getRegionWidth() * 4, playerTexture.getRegionHeight() * 4);
    }

    public void moveTo(float targetX, float targetY) {
		MoveToAction moveAction = actionPool.obtain();
		
		double dx = (double) (targetX - getX());
		double dy = (double) (targetY - getY());
		float length = (float) Math.sqrt(dx * dx + dy * dy);

		moveAction.reset();
    	moveAction.setDuration(length / velocity);
		moveAction.setPosition(targetX, targetY);
		
		addAction(moveAction);
    }
}