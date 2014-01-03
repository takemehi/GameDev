package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;

public class ClientPlayer implements IRender, IMoveable {

	protected PlayableCharacter character;
	protected int team;
	
	private EntityAnimation animations;
	private float stateTime;
	
	public ClientPlayer(PlayableCharacter character, int team) {
		if (character == null) {
			throw new NullPointerException();
		}
		
		this.character = character;
		this.team = team;
		
		// TODO init Player Animation based on character
	}

	@Override
	public void render(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		Vector2 pos = character.getBody().getPosition();
		batch.draw(animations.getAnimationFrame(getCharacterState(), stateTime), pos.x, pos.y);
		
		// TODO render healthbar & name
	}

	protected CharacterStates getCharacterState() {
		if (character.isJumping()) {
			return CharacterStates.JUMPING;
		}
		else if (character.getLeft() || character.getRight()) {
			return CharacterStates.WALKING;
		}
		else if (character.getHealth() <= 0) {
			return CharacterStates.DEAD;
		}
		
		// TODO skills?, capturing
		
		return CharacterStates.IDLE;
	}
	
	public int getEntityId() {
		return character.getId();
	}
	
	@Override
	public void setPosition(Vector2 pos) {
		character.getBody().setTransform(pos, character.getBody().getAngle());
	}

	@Override
	public void moveLeft() {
		character.setLeft(true);
	}

	@Override
	public void moveRight() {
		character.setRight(true);
	}

	@Override
	public void startJump() {
		character.setJumping(true);
	}

	@Override
	public void stopMove() {
		character.setLeft(false);
		character.setRight(false);
	}

	@Override
	public void moveDown() {
		character.setDown(true);
	}

	@Override
	public void stopDown() {
		character.setDown(false);
	}

}
