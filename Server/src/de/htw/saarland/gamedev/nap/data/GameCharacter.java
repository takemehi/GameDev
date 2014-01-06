package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;

public class GameCharacter extends MoveableEntity{
	
	//Exceptions
	private final static String EXCEPTION_ILLEGAL_HEALTH_MAX = "MaxHealth value has to be greater than zero!";
	private final static String EXCEPTION_ILLEGAL_GROUNDTIME = "Groundtime value can't be smaller than zero!";
	private final static String EXCEPTION_ILLEGAL_ORIENTATION = "Orientation can only be 0 or 1!";
	
	public static final int ORIENTATION_LEFT = 0;
	public static final int ORIENTATION_RIGHT = 1;

	private int maxHealth;
	private int health;
	private boolean jumping;
	private float timeOnGround;
	private boolean movementEnabled;
	private boolean movingLeft;
	private boolean movingRight;
	private boolean movingDown;
	private boolean movingUp;
	private int orientation;
	
	public GameCharacter(Shape shape, float density,
			float friction, float restitution, Vector2 position, Vector2 baseVelocity, Vector2 maxVelocity, int maxHealth
			, int id){
		super(shape, density, friction, restitution, position, baseVelocity, maxVelocity, id);
		
		if(maxHealth <= 0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_HEALTH_MAX);
		this.maxHealth=maxHealth;
		this.health=maxHealth;
		jumping=false;
		timeOnGround=0;
		orientation=1;
		movementEnabled=true;
	}
	
	//methods that get used by the network
	
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
	
	public void setLeft(boolean left){
		this.movingLeft=left;
		if(movementEnabled){
			if(left){
				movingRight=false;
				getBody().setLinearVelocity(-getBaseVelocity().x, getBody().getLinearVelocity().y);
			}else{
				getBody().setLinearVelocity(0, getBody().getLinearVelocity().y);
			}
		}
	}
	
	public void setRight(boolean right){
		this.movingRight=right;
		if(movementEnabled){
			if(right){
				movingLeft=false;
				getBody().setLinearVelocity(getBaseVelocity().x, getBody().getLinearVelocity().y);
			}else{
				getBody().setLinearVelocity(0, getBody().getLinearVelocity().y);
			}
		}
	}
	
	public void setUp(boolean up){
		this.movingUp=up;
		if(movementEnabled){
			if(movingUp && !jumping){
				getBody().setAwake(true);
				getBody().setLinearVelocity(getBody().getLinearVelocity().x, getBaseVelocity().y);
				jumping=true;
			}
			if(!movingUp) jumping=false;
		}
	}
	
	public void setDown(boolean down){
		this.movingDown=down;
		if(movementEnabled){
			if(down && timeOnGround>0.2f)
				getBody().setAwake(true);
		}
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
	
	public boolean isJumping(){
		return jumping;
	}
	
	public void setJumping(boolean jumping){
		this.jumping=jumping;
	}
	
	public boolean isMovementEnabled(){
		return movementEnabled;
	}
	
	public void setMovementEnabled(boolean movementEnabled){
		this.movementEnabled=movementEnabled;
	}
	
	public int getOrientation(){
		return orientation;
	}
	
	public void setOrientation(int orientation){
		if(orientation!=ORIENTATION_LEFT && orientation!=ORIENTATION_RIGHT) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ORIENTATION);
		this.orientation=orientation;
	}
	
	public float getTimeOnGround(){
		return timeOnGround;
	}
	
	public void setTimeonGround(float timeOnGround){
		if(timeOnGround<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_GROUNDTIME);
		this.timeOnGround=timeOnGround;
	}

}
