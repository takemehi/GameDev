package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;

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
	
	public Skill(PlayableCharacter character, float cooldown, float castTime, boolean cast){
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
	}
	
	public void update(){
		doUpdate(character.getWorld(), character, direction);
		if(!onCooldown && attacking){
			//TODO cast startet hier
			casted=false;
			if (deltaTime>=castTime){
				//TODO cast ist hier beendet und skill wird gestartet
				start(character.getWorld(), character, direction);
				casted=true;
			}
			onCooldown=true;
		}	
		
		if(onCooldown){
			deltaTime+=Gdx.graphics.getDeltaTime();
			if(!casted && deltaTime>=castTime){
				start(character.getWorld(), character, direction);
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
	
	protected abstract void start(World world, PlayableCharacter character, Vector2 mouseCoords);
	
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
