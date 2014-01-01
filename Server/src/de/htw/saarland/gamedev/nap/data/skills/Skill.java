package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;

public abstract class Skill {

	//Exceptions
	private static final String EXCEPTION_ILLEGAL_COOLDOWN = "The cooldown value can't be smaller than 0";
	private static final String EXCEPTION_ILLEGAL_CASTTIME = "The castTime value can't be smaller than 0";
	
	private float cooldown;
	private float castTime;
	
	public Skill(float cooldown, float castTime){
		if(cooldown<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_COOLDOWN);
		if(castTime<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_CASTTIME);
		this.cooldown=cooldown;
		this.castTime=castTime;
	}
	
	public abstract void start(World world, PlayableCharacter character, int currentId, Vector2 mouseCoords);
	
	public abstract void update();
	
	public abstract void cleanUp(World world);
	
	//getter / setter

	public float getCooldown() {
		return cooldown;
	}

	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
	}

	public float getCastTime() {
		return castTime;
	}

	public void setCastTime(float castTime) {
		this.castTime = castTime;
	}
	
}
