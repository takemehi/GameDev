package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;
import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.client.render.character.MageAnimation;
import de.htw.saarland.gamedev.nap.client.render.character.WarriorAnimation;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;

public class ClientPlayer implements IRender, IMoveable, Disposable {

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
		
		switch (character.getCharacterClass()) {
			case PlayableCharacter.ID_MAGE:
				animations = new MageAnimation();
				break;
			case PlayableCharacter.ID_WARRIOR:
				animations = new WarriorAnimation();
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		Vector2 pos = character.getBody().getPosition();
		batch.draw(animations.getAnimationFrame(getCharacterState(), stateTime), pos.x * 96, pos.y * 96);
		
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
	
	public PlayableCharacter getPlayableCharacter() {
		return character;
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
		character.setUp(true);
	}
	
	@Override
	public void stopJump() {
		character.setUp(false);
	}

	@Override
	public void stopMoveLeft() {
		character.setLeft(false);
	}
	
	@Override
	public void stopMoveRight() {
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

	@Override
	public void dispose() {
		animations.dispose();
	}

}
