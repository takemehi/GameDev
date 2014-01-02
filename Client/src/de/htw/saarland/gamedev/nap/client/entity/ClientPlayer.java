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
		// TODO return correct state
		
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
		// TODO call method to start moving left (needs to get implemented at MoveableEntity class)
		
	}

	@Override
	public void moveRight() {
		// TODO call method to start moving right (needs to get implemented at MoveableEntity class)
		
	}

	@Override
	public void startJump() {
		// TODO call method to start to jump (needs to get implemented at MoveableEntity class)
		
	}

	@Override
	public void stopMove() {
		// TODO call method to stop moving (needs to get implemented at MoveableEntity class)
		
	}

}
