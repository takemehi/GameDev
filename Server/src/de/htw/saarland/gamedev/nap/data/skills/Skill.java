package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.GameCharacter;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.ISendPacket;
import de.htw.saarland.gamedev.nap.server.DeltaTime;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public abstract class Skill {

	public static final String KEY_COOLDOWN = "cooldown";
	public static final String KEY_CASTTIME = "casttime";
	public static final String KEY_DAMAGE = "damage";
	
	//Exceptions
	private static final String EXCEPTION_ILLEGAL_COOLDOWN = "The cooldown value can't be smaller than 0";
	private static final String EXCEPTION_ILLEGAL_CASTTIME = "The castTime value can't be smaller than 0";
	
	private float cooldown;
	private float castTime;
	private float deltaTime;
	private float deltaTimeClient;
	private boolean onCooldown;
	private boolean onCooldownClient;
	private boolean casted;
	private volatile boolean directionUpdated;
	private Vector2 direction;
	protected boolean cast;
	protected boolean client;
	private PlayableCharacter character;
	private ISendPacket sendPacketListener;
	private int skillNr;
	private volatile boolean packetStartAttack;
	private volatile boolean packetStartCast;
	private volatile boolean directionRequestSend;
	
	protected ISkillEvent skillEventListener;
	
	public Skill(PlayableCharacter character, float cooldown, float castTime, boolean cast, int skillNr){
		if(cooldown<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_COOLDOWN);
		if(castTime<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_CASTTIME);
		this.cooldown=cooldown;
		this.castTime=castTime;
		deltaTimeClient=0;
		deltaTime=0;
		onCooldownClient=false;
		onCooldown=false;
		casted=true;
		this.cast=cast;
		this.character=character;
		direction = new Vector2(0,0);
		directionUpdated = false;
		this.skillNr = skillNr;
		client=true;
		packetStartAttack = false;
		packetStartCast = false;
		directionRequestSend = false;
	}
	
	public void setSkillStartListener(ISkillEvent skillEventListener) {
		this.skillEventListener = skillEventListener;
	}
	
	public void setSendPacketListener(ISendPacket sendPacketListener) {
		this.sendPacketListener = sendPacketListener;
		client=false;
	}
	
	public void startAttackByPacket() {
		packetStartAttack = true;
	}
	
	public void startCastByPacket() {
		packetStartCast = true;
	}
	
	public void update(float _deltaTime){
		doUpdate(character.getWorld(), character, direction, _deltaTime);		
		if(client){
			if (packetStartCast) {
				onCooldown = true;
				onCooldownClient = true;
				casted = false;
				packetStartCast = false;
			}
			else if (packetStartAttack){ 
				//add info about received packet
				start(character.getWorld(), character, direction);
				
				casted=true;
				onCooldown=true;
				onCooldownClient=true;
				
				packetStartAttack = false;
			}
			if(onCooldownClient){
				deltaTimeClient+=_deltaTime;
				if(deltaTimeClient>=cooldown){
					onCooldownClient=false;
					deltaTimeClient=0;
				}
			}
		}
		
		if(!client){
			if(!onCooldown &&
				((skillNr==1 && character.isAttacking1())||(skillNr==2 && character.isAttacking2())||(skillNr==3 && character.isAttacking3()))){
				//accept request here and send packet to confirm the accepted request
				//if the skill is no cast the client can start the attack simulation immediately
				//without receiving further packets
				//
				//if the skill is no cast the client can immediately start the cast simulation
				casted=false;
				if (!isCast()){
					SFSObject params = new SFSObject();
					params.putInt(GameOpcodes.ENTITY_ID_PARAM, character.getId());
					params.putFloat(GameOpcodes.DIRECTION_X_PARAM, direction.x);
					params.putFloat(GameOpcodes.DIRECTION_Y_PARAM, direction.y);
					
					directionUpdated=false;
					casted=true;
					switch(skillNr){
						case 1:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL1_START, params);
							character.setAttacking1(false);
							break;
						case 2:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL2_START, params);
							character.setAttacking2(false);
							break;
						case 3:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL3_START, params);
							character.setAttacking3(false);
							break;
					}
					
					start(character.getWorld(), character, direction);
				}
				else {
					SFSObject params = new SFSObject();
					params.putInt(GameOpcodes.ENTITY_ID_PARAM, character.getId());
					
					switch(skillNr){
						case 1:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL1_CAST_START, params);
							//character.setAttacking1(false);
							break;
						case 2:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL2_CAST_START, params);
							//character.setAttacking2(false);
							break;
						case 3:
							sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL3_CAST_START, params);
							//character.setAttacking3(false);
							break;
					}
				}
				onCooldown=true;
				directionUpdated = false;
			}
			
			if(onCooldown && !directionRequestSend){
				deltaTime+=_deltaTime;
				if(!casted && deltaTime>=castTime){
					sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL_DIRECTION_REQUEST, null, character.getId());
					directionRequestSend = true;
				}
			}
			
			//direction has to be updated via setDirection()
			//once the direction has been Updated the skill starts
			if(cast && !casted && directionUpdated && deltaTime >= castTime){
				
				SFSObject params = new SFSObject();
				params.putInt(GameOpcodes.ENTITY_ID_PARAM, character.getId());
				params.putFloat(GameOpcodes.DIRECTION_X_PARAM, direction.x);
				params.putFloat(GameOpcodes.DIRECTION_Y_PARAM, direction.y);
				
				casted=true;
				directionUpdated=false;
				switch(skillNr){
					case 1:
						sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL1_START, params);
						character.setAttacking1(false);
						break;
					case 2:
						sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL2_START, params);
						character.setAttacking2(false);
						break;
					case 3:
						sendPacketListener.sendServerPacket(GameOpcodes.GAME_SKILL3_START, params);
						character.setAttacking3(false);
						break;
				}
				
				start(character.getWorld(), character, direction);
			}
		}
		if(onCooldown){
			switch(skillNr){
			case 1:
				character.setAttacking1(false);
				break;
			case 2:
				character.setAttacking2(false);
				break;
			case 3:
				character.setAttacking3(false);
				break;
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
	
	protected abstract void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords, float deltaTime);
	
	public void reset(){
		casted=true;
		deltaTime=0;
	}
	
	//getter / setter
	
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
	
	public float getDeltaTime(){
		if(!client)
			return deltaTime;
		else
			return deltaTimeClient;
	}
	
	public Vector2 getDirection(){
		return direction;
	}
	
	public void setDirection(Vector2 direction){		
		this.direction=direction;
		directionUpdated=true;
		directionRequestSend = false;
		
		if(direction.x > 0) {
			character.setOrientation(GameCharacter.ORIENTATION_RIGHT);
		}
		else {
			character.setOrientation(GameCharacter.ORIENTATION_LEFT);
		}
	}
	
	public PlayableCharacter getPlayableCharacter(){
		return character;
	}
	
}
