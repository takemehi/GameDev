package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

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
	protected float maxSwingTime;
	
	public Skill(float cooldown, float castTime){
		if(cooldown<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_COOLDOWN);
		if(castTime<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_CASTTIME);
		this.cooldown=cooldown;
		this.castTime=castTime;
		deltaTime=0;
		onCooldown=false;
		casted=true;
	}
	
	public void update(World world, PlayableCharacter character, int currentId, Vector2 mouseCoords){
		doUpdate(world, character, currentId, mouseCoords);
		if(!onCooldown && attacking){
			casted=false;
			if (deltaTime>=castTime){
				start(world, character, currentId, mouseCoords);
				casted=true;
			}
			onCooldown=true;
		}	
		
		if(onCooldown){
			deltaTime+=Gdx.graphics.getDeltaTime();
			if(!casted && deltaTime>=castTime){
				start(world, character, currentId, mouseCoords);
				casted=true;
			}
		}
		
		if(onCooldown && casted && deltaTime>=cooldown){
			onCooldown=false;
			casted=true;
			deltaTime=0;
		}
	}
	
	public abstract void cleanUp(World world);
	
	protected abstract void start(World world, PlayableCharacter character, int currentId, Vector2 mouseCoords);
	
	protected abstract void doUpdate(World world, PlayableCharacter character, int currentId, Vector2 mouseCoords);
	
	//getter / setter
	
	public boolean isAttacking(){
		return attacking;
	}
	
	public void setAttacking(boolean attacking){
		this.attacking=attacking;
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
	
	public float getMaxSwingTime(){
		return maxSwingTime;
	}
	
	public boolean isOnCooldown(){
		return onCooldown;
	}
	
	public void setOnCooldown(boolean onCooldown){
		this.onCooldown=onCooldown;
	}
	
}
