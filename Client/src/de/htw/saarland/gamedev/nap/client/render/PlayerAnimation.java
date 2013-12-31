package de.htw.saarland.gamedev.nap.client.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class PlayerAnimation {
	public enum CharacterStates {
		WALKING,
		JUMPING,
		SKILL1,
		SKILL2,
		SKILL3
	}
	
	public abstract TextureRegion getAnimationFrame(CharacterStates state, float stateTime);
}
