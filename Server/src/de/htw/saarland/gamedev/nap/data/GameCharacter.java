package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;

public class GameCharacter extends MoveableEntity{
	
	//Exceptions
	private final static String EXCEPTION_ILLEGAL_HEALTH_MAX = "MaxHealth value has to be greater than zero!";
	private final static String EXCEPTION_ILLEGAL_GROUNDTIME = "Groundtime value can't be smaller than zero!";
	private final static String EXCEPTION_ILLEGAL_SWINGTIME = "Swingtime value can't be smaller than zero!";

	private int maxHealth;
	private int health;
	private float swingTime;
	private float maxSwingTime;
	private boolean jumping;
	private boolean swinging;
	private float timeOnGround;
	private boolean movingLeft;
	private boolean movingRight;
	private boolean movingDown;
	private boolean movingUp;
	private boolean attacking;
	
	public GameCharacter(Shape shape, float density,
			float friction, float restitution, Vector2 position, Vector2 baseVelocity, Vector2 maxVelocity, int maxHealth
			, float maxSwingTime, int id){
		super(shape, density, friction, restitution, position, baseVelocity, maxVelocity, id);
		
		if(maxHealth <= 0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_HEALTH_MAX);
		if(maxSwingTime<=0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_SWINGTIME);
		this.maxHealth=maxHealth;
		this.health=maxHealth;
		this.maxSwingTime=maxSwingTime;
		jumping=false;
		timeOnGround=0;
		swingTime=0;
	}
	
	//methods that get used by the network
	
	public boolean getAttacking(){
		return attacking;
	}
	
	public boolean getLeft(){
		return movingLeft;
	}
	
	public boolean getRight(){
		return movingRight;
	}
	
	public boolean getUp(){
		return movingUp;
	}
	
	public boolean getDown(){
		return movingDown;
	}
	
	public void setAttacking(boolean attacking){
		this.attacking=attacking;
		if(attacking) swinging=true;
	}
	
	public void setLeft(boolean left){
		this.movingLeft=left;
	}
	
	public void setRight(boolean right){
		this.movingRight=right;
	}
	
	public void setUp(boolean up){
		this.movingUp=up;
	}
	
	public void setDown(boolean down){
		this.movingDown=down;
	}
	
	//getter / setter
	
	public int getHealth(){
		return health;
	}
	
	public void setHealth(int health){
		if(health>maxHealth) this.health=maxHealth;
		else this.health=health;
	}
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
	public float getMaxSwingTime(){
		return maxSwingTime;
	}
	
	public boolean isJumping(){
		return jumping;
	}
	
	public void setJumping(boolean jumping){
		this.jumping=jumping;
	}
	
	public void setSwinging(boolean swinging){
		this.swinging=swinging;
	}
	
	public boolean isSwinging(){
		return swinging;
	}
	
	public void setSwingTime(float swingTime){
		this.swingTime=swingTime;
	}
	
	public float getSwingTime(){
		return swingTime;
	}
	
	public float getTimeOnGround(){
		return timeOnGround;
	}
	
	public void setTimeonGround(float timeOnGround){
		if(timeOnGround<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_GROUNDTIME);
		this.timeOnGround=timeOnGround;
	}

}
