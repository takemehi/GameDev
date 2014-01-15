package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.ISendPacket;

public abstract class Skill {

	//Exceptions
	private static final String EXCEPTION_ILLEGAL_COOLDOWN = "The cooldown value can't be smaller than 0";
	private static final String EXCEPTION_ILLEGAL_CASTTIME = "The castTime value can't be smaller than 0";
	
	private float cooldown;
	private float castTime;
	private float deltaTime;
	private boolean onCooldown;
	private boolean casted;
	private boolean attacking;
	private Vector2 direction;
	private boolean cast;
	private PlayableCharacter character;
	
	private ISendPacket sendPacketListener;
	private int skillNr;
	
	public Skill(PlayableCharacter character, float cooldown, float castTime, boolean cast, int skillNr){
		if(cooldown<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_COOLDOWN);
		if(castTime<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_CASTTIME);
		this.cooldown=cooldown;
		this.castTime=castTime;
		deltaTime=0;
		onCooldown=false;
		casted=true;
		this.cast=cast;
		this.character=character;
		direction = new Vector2(0,0);
		this.skillNr = skillNr;
	}
	
	public void setSendPacketListener(ISendPacket sendPacketListener) {
		this.sendPacketListener = sendPacketListener;
	}
	
	public void update(float _deltaTime){
		doUpdate(character.getWorld(), character, direction);
		if(!onCooldown && attacking){
			
			if (sendPacketListener != null && castTime > 0) {
				//we are the server send ok packet to client that started the request
				SFSObject params = new SFSObject();
				params.putInt(GameOpcodes.ENTITY_ID_PARAM, character.getId());
				
				switch (skillNr) {
					case 1:
						sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL1_CAST_START, params);
						break;
					case 2:
						sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL2_CAST_START, params);
						break;
					case 3:
						sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL3_CAST_START, params);
						break;
				}
			}
			casted=false;
			if (deltaTime>=castTime){
				if (sendPacketListener != null) {
					//we are the server send ok packet to client that started the request
					SFSObject params = new SFSObject();
					params.putInt(GameOpcodes.ENTITY_ID_PARAM, character.getId());
					params.putFloat(GameOpcodes.DIRECTION_X_PARAM, direction.x);
					params.putFloat(GameOpcodes.DIRECTION_Y_PARAM, direction.y);
					
					switch (skillNr) {
						case 1:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL1_START, params);
							break;
						case 2:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL2_START, params);
							break;
						case 3:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL3_START, params);
							break;
					}
				}
				if (sendPacketListener != null) {
					//only do this on the server side
					start(character.getWorld(), character, direction);
				}
				casted=true;
			}
			onCooldown=true;
		}	
		
		if(onCooldown){
			deltaTime+=_deltaTime;
			if(!casted && deltaTime>=castTime){
				if (sendPacketListener != null) {
					//only do this on the server side
					start(character.getWorld(), character, direction);
				}
				casted=true;
			}
		}
		
		if(onCooldown && casted && deltaTime>=cooldown){
			onCooldown=false;
			casted=true;
			deltaTime=0;
		}
	}
	
	public abstract void cleanUp();
	
	public abstract void start(World world, PlayableCharacter character, Vector2 mouseCoords);
	
	protected abstract void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords);
	
	public void reset(){
		casted=true;
		deltaTime=0;
	}
	
	//getter / setter
	
	public boolean isAttacking(){
		return attacking;
	}
	
	public void setAttacking(boolean attacking){
		this.attacking=attacking;
	}
	
	public boolean isCast(){
		return cast;
	}
	
	public boolean isCasted(){
		return casted;
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getCastTime() {
		return castTime;
	}
	
	public boolean isOnCooldown(){
		return onCooldown;
	}
	
	public void setOnCooldown(boolean onCooldown){
		this.onCooldown=onCooldown;
	}
	
	public Vector2 getDirection(){
		return direction;
	}
	
	public void setDirection(Vector2 direction){
		this.direction=direction;
	}
	
	public PlayableCharacter getPlayableCharacter(){
		return character;
	}
	
}
