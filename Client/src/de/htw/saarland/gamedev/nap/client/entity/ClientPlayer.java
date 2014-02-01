package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	private boolean wasDead;
	
	private ShapeRenderer shapeRenderer;
	private Color hpBoxCol;
	
	private boolean isStunned;
	private boolean isSnared;
	private float timeStunned;
	private float timeSnared;
	private float stunDuration;
	private float snareDuration;
	
	private BitmapFont font;
	
	public ClientPlayer(PlayableCharacter character, int team, int friendlyTeamId) {
		if (character == null) {
			throw new NullPointerException();
		}
		
		font = new BitmapFont(new FileHandle("data/font/calibri.fnt"),
				new FileHandle("data/font/calibri.png"), false);
		
		this.character = character;
		this.team = team;
		this.charState = CharacterStates.IDLE;
		this.charStateBefore = CharacterStates.IDLE;
		this.shapeRenderer = new ShapeRenderer();
		this.hpBoxCol = friendlyTeamId == character.getTeamId() ? FRIENDLY_COLOR : ENEMY_COLOR;
		this.wasDead = false;
		this.isStunned = false;
		this.isSnared = false;
		
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
		
		if (isStunned) {
			timeStunned += Gdx.graphics.getDeltaTime();
		}
		
		if (isSnared) {
			timeSnared += Gdx.graphics.getDeltaTime();
		}
		
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
		
//		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
//		font.draw(batch, str, pos.x, pos.y);
		
		batch.end();
		
		// TODO render name
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setColor(hpBoxCol);
		shapeRenderer.rect(
				pos.x - animations.getHealthBarXOffset(GameServer.PIXELS_TO_METERS),
				pos.y + animations.getHealthBarYOffset(GameServer.PIXELS_TO_METERS),
				1f * ((float)character.getHealth() / (float)character.getMaxHealth()),
				10f * GameServer.PIXELS_TO_METERS
			);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0f, 0f, 0f, 1f);
		shapeRenderer.rect(
				pos.x - animations.getHealthBarXOffset(GameServer.PIXELS_TO_METERS),
				pos.y + animations.getHealthBarYOffset(GameServer.PIXELS_TO_METERS),
				1f,
				10f * GameServer.PIXELS_TO_METERS
			);
		shapeRenderer.end();
		
		//cast bar
		if(character.getAttack1().isCast() && character.getAttack1().isOnCooldown() && !character.getAttack1().isCasted()
				|| character.getAttack2().isCast() && character.getAttack2().isOnCooldown() && !character.getAttack2().isCasted()
				|| character.getAttack3().isCast() && character.getAttack3().isOnCooldown() && !character.getAttack3().isCasted()){
			
			float barWidth = 0;
			if(character.getAttack1().isCast() && character.getAttack1().isOnCooldown() && !character.getAttack1().isCasted()){
				barWidth = 1f * ((float)character.getAttack1().getDeltaTime() / (float)character.getAttack1().getCastTime());
			}else if(character.getAttack2().isCast() && character.getAttack2().isOnCooldown() && !character.getAttack2().isCasted()){
				barWidth = 1f * ((float)character.getAttack2().getDeltaTime() / (float)character.getAttack2().getCastTime());
			}else if(character.getAttack3().isCast() && character.getAttack3().isOnCooldown() && !character.getAttack3().isCasted()){
				barWidth = 1f * ((float)character.getAttack3().getDeltaTime() / (float)character.getAttack3().getCastTime());
			}
			
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setColor(new Color(1,1,1,1));
			shapeRenderer.rect(
					pos.x - animations.getHealthBarXOffset(GameServer.PIXELS_TO_METERS),
					pos.y + animations.getHealthBarYOffset(GameServer.PIXELS_TO_METERS)+11f * GameServer.PIXELS_TO_METERS,
					barWidth,
					10f * GameServer.PIXELS_TO_METERS
				);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(0f, 0f, 0f, 1f);
			shapeRenderer.rect(
					pos.x - animations.getHealthBarXOffset(GameServer.PIXELS_TO_METERS),
					pos.y + animations.getHealthBarYOffset(GameServer.PIXELS_TO_METERS)+11f * GameServer.PIXELS_TO_METERS,
					1f,
					10f * GameServer.PIXELS_TO_METERS
				);
			shapeRenderer.end();
		}
		
		if (stateTime > animations.getAnimationTime(charState)) {
			charState = charStateBefore;
		}
		
		batch.begin();
	}

	protected CharacterStates getCharacterState() {
		if (character.getHealth() <= 0) {
			if (!wasDead) {
				stateTime = 0;
				wasDead = true;
			}
			return CharacterStates.DEAD;
		}
		
		return charState;
	}
	
	private void setCharacterState(CharacterStates newCharState) {
		wasDead = false;
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
	
	public boolean isStunned() {
		return isStunned;
	}

	public void setStunned(boolean isStunned, float stunDuration) {
		this.isStunned = isStunned;
		this.stunDuration = stunDuration;
		this.timeStunned = 0;
	}

	public boolean isSnared() {
		return isSnared;
	}

	public void setSnared(boolean isSnared, float snareDuration) {
		this.isSnared = isSnared;
		this.snareDuration = snareDuration;
		this.timeSnared = 0;
	}

	public float getTimeStunned() {
		return timeStunned;
	}

	public float getTimeSnared() {
		return timeSnared;
	}

	public float getStunDuration() {
		return stunDuration;
	}

	public float getSnareDuration() {
		return snareDuration;
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
