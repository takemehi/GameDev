package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.GameCharacter;
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
	private boolean directionUpdated;
	private Vector2 direction;
	protected boolean cast;
	private boolean client;
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
		directionUpdated = false;
		this.skillNr = skillNr;
		client=true;
	}
	
	public void setSendPacketListener(ISendPacket sendPacketListener) {
		this.sendPacketListener = sendPacketListener;
		client=false;
	}
	
	public void update(float _deltaTime){
		doUpdate(character.getWorld(), character, direction);		
		if(client){
			if (!cast &&
					((skillNr==1 && character.isAttacking1())||(skillNr==2 && character.isAttacking2())||(skillNr==1 && character.isAttacking1()))){ 
				//add info about received packet
				start(character.getWorld(), character, direction);
				casted=true;
				onCooldown=true;
			}
			if(){ //direction request
				//send direction
				start(character.getWorld(), character, direction);
				casted=true;
			}
		}
		
		if(!client){
			if(!onCooldown &&
				((skillNr==1 && character.isAttacking1())||(skillNr==2 && character.isAttacking2())||(skillNr==1 && character.isAttacking1()))){
				//accept request here and send packet to confirm the accepted request
				//if the skill is no cast the client can start the attack simulation immediately
				//without receiving further packets
				//
				//if the skill is no cast the client can immediately start the cast simulation
				casted=false;
				if (!isCast()){				
					start(character.getWorld(), character, direction);
					directionUpdated=false;
					casted=true;
					switch(skillNr){
					case 1:
						character.setAttacking1(false);
					case 2:
						character.setAttacking2(false);
					case 3:
						character.setAttacking3(false);
					}
				}
				onCooldown=true;
			}
			
			if(onCooldown){
				deltaTime+=_deltaTime;
				if(!casted && deltaTime>=castTime){
					//send direction request
				}
			}
			
			//direction has to be updatedt via setDirection()
			//once the direction has been Updated the skill starts
			if(casted==false && directionUpdated==true){
				start(character.getWorld(), character, direction);
				casted=true;
				directionUpdated=false;
				switch(skillNr){
				case 1:
					character.setAttacking1(false);
				case 2:
					character.setAttacking2(false);
				case 3:
					character.setAttacking3(false);
				}
			}
		}
		
		if(onCooldown && casted && deltaTime>=cooldown){
			onCooldown=false;
			casted=true;
			deltaTime=0;
		}
		
		if(direction.x>0) character.setOrientation(GameCharacter.ORIENTATION_RIGHT);
		else character.setOrientation(GameCharacter.ORIENTATION_LEFT);
	}
	
	public abstract void cleanUp();
	
	public abstract void start(World world, PlayableCharacter character, Vector2 mouseCoords);
	
	protected abstract void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords);
	
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
	
	public Vector2 getDirection(){
		return direction;
	}
	
	public void setDirection(Vector2 direction){
		this.direction=direction;
		directionUpdated=true;
	}
	
	public PlayableCharacter getPlayableCharacter(){
		return character;
	}
	
}
