package de.htw.saarland.gamedev.nap.client.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class EntityAnimation {	
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
	
	public abstract TextureRegion getAnimationFrame(CharacterStates state, float stateTime);
}
