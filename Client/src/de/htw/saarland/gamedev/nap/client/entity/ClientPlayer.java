package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;
import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.client.render.character.MageAnimation;
import de.htw.saarland.gamedev.nap.client.render.character.WarriorAnimation;
import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class ClientPlayer implements IPlayer, IRender, IMoveable, Disposable {

	protected PlayableCharacter character;
	protected int team;
	
	private EntityAnimation animations;
	private float stateTime;
	protected PlayerCasting cast;
	
	public ClientPlayer(PlayableCharacter character, int team) {
		if (character == null) {
			throw new NullPointerException();
		}
		
		this.character = character;
		this.team = team;
		cast = null;
		
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
		TextureRegion region = animations.getAnimationFrame(getCharacterState(), stateTime);
		float width = (region.getRegionWidth() * GameServer.PIXELS_TO_METERS);
		float height = (region.getRegionHeight() * GameServer.PIXELS_TO_METERS);
		
		if (region.isFlipX() == (character.getOrientation() == PlayableCharacter.ORIENTATION_LEFT)) {
			region.flip(true, false);
		}
		
		float x = pos.x - (width / 2);
		x = !region.isFlipX() ? x + animations.getXOffset(GameServer.PIXELS_TO_METERS) : x - animations.getXOffset(GameServer.PIXELS_TO_METERS);
		
		batch.draw(region,
				x,
				pos.y - (height / 2) + animations.getYOffset(GameServer.PIXELS_TO_METERS),
				width,
				height);
		
		// TODO render healthbar & name
	}
	
	public void setCast(PlayerCasting cast) {
		this.cast = cast;
	}

	protected CharacterStates getCharacterState() {
		if (character.getHealth() <= 0) {
			return CharacterStates.DEAD;
		}
		else if (cast != null) {
			return cast.getCharacterState();
		}
		else if (character.getLeft() || character.getRight()) {
			return CharacterStates.WALKING;
		}
		
		return CharacterStates.IDLE;
	}
	
	public PlayableCharacter getPlayableCharacter() {
		return character;
	}
	
	public PlayableCharacter getPlChar(){
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
