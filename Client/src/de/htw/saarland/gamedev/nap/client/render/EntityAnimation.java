package de.htw.saarland.gamedev.nap.client.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class EntityAnimation implements Disposable {	
	public enum CharacterStates {
		IDLE,
		WALKING,
		JUMPING,
		CAPTURING,
		SKILL1,
		SKILL2,
		SKILL3,
		DEAD
	}
	
	protected Animation createAnimation(Texture texture, int row, int framesCount, int spriteWidth, int spriteHeight, float frameDuration) {
		int y = row * spriteHeight;
		TextureRegion[] reg = new TextureRegion[framesCount];
		for (int i = 0; i < framesCount; i++) {
			reg[i] = new TextureRegion(texture,
					i * spriteWidth,
					y,
					spriteWidth,
					spriteHeight);
		}
		
		return new Animation(frameDuration, reg);
	}
	
	public abstract float getXOffset(float unitScale);
	public abstract float getYOffset(float unitScale);
	public abstract float getHealthBarXOffset(float unitScale);
	public abstract float getHealthBarYOffset(float unitScale);
	
	public abstract TextureRegion getAnimationFrame(CharacterStates state, float stateTime);
}
