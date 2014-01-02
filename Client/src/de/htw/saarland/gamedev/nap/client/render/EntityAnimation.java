package de.htw.saarland.gamedev.nap.client.render;

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
	
	public abstract TextureRegion getAnimationFrame(CharacterStates state, float stateTime);
}
