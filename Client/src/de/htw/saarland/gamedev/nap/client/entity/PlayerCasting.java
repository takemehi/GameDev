package de.htw.saarland.gamedev.nap.client.entity;

import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;

public class PlayerCasting {
	private static final float CAST_DIRECTION_UPDATE_VALUE = 0.1f;
	
	private float stateTime;
	private float castTime;
	private CharacterStates charState;
	private boolean returned;
	
	public PlayerCasting(float castTime, CharacterStates charState) {
		this.castTime = castTime;
		this.charState = charState;
		stateTime = 0.0f;
		returned = false;
	}
	
	public boolean update(float deltaTime) {
		stateTime += deltaTime;
		
		return isReadyToSendDirection();
	}
	
	public boolean isReadyToSendDirection() {
		if (!returned) {
			if (stateTime > castTime - CAST_DIRECTION_UPDATE_VALUE) {
				returned = true;
				return true;
			}
		}
		return false;
	}
	
	public CharacterStates getCharacterState() {
		return charState;
	}
}
