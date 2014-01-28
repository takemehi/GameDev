package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

public class ClientPlayer implements IPlayer, IRender, IMoveable, ISkillStart, Disposable {

	public static final Color FRIENDLY_COLOR = new Color(0, 1.0f, 0, 1.0f);
	public static final Color ENEMY_COLOR = new Color(1.0f, 0, 0, 1.0f);
	
	protected PlayableCharacter character;
	protected int team;
	private CharacterStates charState;
	private CharacterStates charStateBefore;
	
	private EntityAnimation animations;
	private float stateTime;
	
	private ShapeRenderer shapeRenderer;
	
	public ClientPlayer(PlayableCharacter character, int team, int friendlyTemId) {
		if (character == null) {
			throw new NullPointerException();
		}
		
		this.character = character;
		this.team = team;
		this.charState = CharacterStates.IDLE;
		this.charStateBefore = CharacterStates.IDLE;
		this.shapeRenderer = new ShapeRenderer();
		this.shapeRenderer.setColor(friendlyTemId == character.getTeamId() ? FRIENDLY_COLOR : ENEMY_COLOR);
		
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
		
		if (region.isFlipX() == (character.getOrientation() == PlayableCharacter.ORIENTATION_RIGHT)) {
			region.flip(true, false);
		}
		
		float x = pos.x - (width / 2);
		x = !region.isFlipX() ? x + animations.getXOffset(GameServer.PIXELS_TO_METERS) : x - animations.getXOffset(GameServer.PIXELS_TO_METERS);
		
		batch.draw(region,
				x,
				pos.y - (height / 2) + animations.getYOffset(GameServer.PIXELS_TO_METERS),
				width,
				height);
		batch.end();
		
		// TODO render name
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.rect(
				pos.x - animations.getHealthBarXOffset(GameServer.PIXELS_TO_METERS),
				pos.y + animations.getHealthBarYOffset(GameServer.PIXELS_TO_METERS),
				1f * ((float)character.getHealth() / (float)character.getMaxHealth()),
				10f * GameServer.PIXELS_TO_METERS);
		shapeRenderer.end();
		
		if (stateTime > animations.getAnimationTime(charState)) {
			charState = charStateBefore;
		}
		
		batch.begin();
	}

	protected CharacterStates getCharacterState() {
		if (character.getHealth() <= 0) {
			return CharacterStates.DEAD;
		}
		
		return charState;
	}
	
	private void setCharacterState(CharacterStates newCharState) {
		switch (newCharState) {
			case SKILL1:
			case SKILL2:
			case SKILL3:
				if (charState != CharacterStates.SKILL1 && charState != CharacterStates.SKILL2 && charState != CharacterStates.SKILL3) {
					//new state == SKILL & current state != SKILL
					charStateBefore = charState;
					charState = newCharState;
					stateTime = 0;
				}
				break;
			case IDLE:
			case WALKING:
				if (charState == CharacterStates.SKILL1 || charState == CharacterStates.SKILL2 || charState == CharacterStates.SKILL3) {
					//new state == IDLE | WALKING & current state == SKILL
					charStateBefore = newCharState;
				}
				else {
					//new state == IDLE | WALKING & current state != SKILL
					charStateBefore = newCharState;
					charState = newCharState;
					stateTime = 0;
				}
				break;
				
		}
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
		setCharacterState(CharacterStates.WALKING);
	}

	@Override
	public void moveRight() {
		setCharacterState(CharacterStates.WALKING);
	}

	@Override
	public void stopMoveLeft() {
		setCharacterState(CharacterStates.IDLE);
	}
	
	@Override
	public void stopMoveRight() {
		setCharacterState(CharacterStates.IDLE);
	}
	
	@Override
	public void startSkill1() {
		setCharacterState(CharacterStates.SKILL1);
	}

	@Override
	public void startSkill2() {
		setCharacterState(CharacterStates.SKILL2);
	}

	@Override
	public void startSkill3() {
		setCharacterState(CharacterStates.SKILL3);
	}

	@Override
	public void dispose() {
		animations.dispose();
	}
}
