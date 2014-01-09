package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.skills.Skill;

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
	private boolean attackEnabled;
	private boolean movementEnabled;
	private boolean stunned;
	private boolean snared;
	private boolean movingLeft;
	private boolean movingRight;
	private boolean movingDown;
	private boolean movingUp;
	private int orientation;
	private float snareDuration;
	private float stunDuration;
	private float timeStunned;
	private float timeSnared;
	private int lastHealth;
	private boolean lostHealth;
	
	protected Skill attack1;
	protected Skill attack2;
	protected Skill attack3;
	
	private boolean attacking1;
	private boolean attacking2;
	private boolean attacking3;
	
	public GameCharacter(World world, Shape shape, float density,
			float friction, float restitution, Vector2 position, Vector2 baseVelocity, Vector2 maxVelocity, int maxHealth
			, int id){
		super(world, shape, density, friction, restitution, position, baseVelocity, maxVelocity, id);
		
		if(maxHealth <= 0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_HEALTH_MAX);
		this.maxHealth=maxHealth;
		this.health=maxHealth;
		lastHealth=health;
		jumping=false;
		timeOnGround=0;
		orientation=1;
		movementEnabled=true;
		attackEnabled=true;
		snareDuration=0;
		stunDuration=0;
		timeSnared=0;
		timeStunned=0;
		lostHealth=false;
	}
	
	public void update(float deltaTime, Array<CapturePoint> capturepoints){
		if (stunned){
			timeStunned+=deltaTime;
			if(timeStunned>=stunDuration){
				setStunned(false, 0);
				timeStunned=0;
				stunDuration=0;
			}
		}
		
		if (snared){
			timeSnared+=deltaTime;
			if(timeSnared>=snareDuration){
				setSnared(false, 0);
				timeSnared=0;
				snareDuration=0;
			}
		}
		
		if(lastHealth>health) lostHealth=true;
		else lostHealth=false;
		lastHealth=health;
		
		setUp(movingUp);
		setDown(movingDown);
		setLeft(movingLeft);
		setRight(movingRight);
		setAttacking1(attacking1);
		setAttacking2(attacking2);
		setAttacking3(attacking3);
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
			}else if (!left && !movingRight){
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
			}else if (!right && !movingLeft){
				getBody().setLinearVelocity(0, getBody().getLinearVelocity().y);
			}
		}
	}
	
	public void setUp(boolean up){
		if(up || (!up && isGrounded()))
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
	
	public boolean isAttacking1() {
		return attacking1;
	}


	public void setAttacking1(boolean attacking1) {
		if(attackEnabled){
			if(attacking1){
				if(attack2.isCasted() && attack3.isCasted()){
					this.attacking1 = attacking1;
					attack1.setAttacking(attacking1);
				}
			}else{
				this.attacking1=false;
				attack1.setAttacking(false);
			}
		}else
			this.attacking1=false;
	}


	public boolean isAttacking2() {
		return attacking2;
	}


	public void setAttacking2(boolean attacking2) {
		if(attackEnabled){
			if(attacking2){
				if(attack1.isCasted() && attack3.isCasted()){
					this.attacking2 = attacking2;
					attack2.setAttacking(attacking2);
				}
			}else{
				this.attacking2=false;
				attack2.setAttacking(false);
			}
		}else
			this.attacking2=false;
	}


	public boolean isAttacking3() {
		return attacking3;
	}


	public void setAttacking3(boolean attacking3) {
		if(attackEnabled){
			if(attacking3){
				if(attack1.isCasted() && attack2.isCasted()){
					this.attacking3 = attacking3;
					attack3.setAttacking(attacking3);
				}
			}else{
				this.attacking3=false;
				attack3.setAttacking(false);
			}
		}else
			this.attacking3=false;
	}


	public Skill getAttack1() {
		return attack1;
	}


	public Skill getAttack2() {
		return attack2;
	}


	public Skill getAttack3() {
		return attack3;
	}
	
	public int getHealth(){
		return health;
	}
	
	public boolean isAttackEnabled() {
		return attackEnabled;
	}

	public void setAttackEnabled(boolean attackEnabled) {
		this.attackEnabled = attackEnabled;
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
	
	public boolean hasLostHealth(){
		return lostHealth;
	}
	
	public boolean isMovementEnabled(){
		return movementEnabled;
	}
	
	public void setMovementEnabled(boolean movementEnabled){
		this.movementEnabled=movementEnabled;
		if(!movementEnabled) getBody().setLinearVelocity(0, getBody().getLinearVelocity().y);
	}
	
	public int getOrientation(){
		return orientation;
	}
	
	public void setOrientation(int orientation){
		if(orientation!=ORIENTATION_LEFT && orientation!=ORIENTATION_RIGHT) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ORIENTATION);
		this.orientation=orientation;
	}
	
	public boolean isStunned() {
		return stunned;
	}

	public void setStunned(boolean stunned, float stunDuration) {
		this.stunned = stunned;
		if(stunned){
			setMovementEnabled(false);
			attackEnabled=false;
			this.stunDuration=stunDuration;
			timeStunned=0;
			attack1.reset();
			attack2.reset();
			attack3.reset();
		}
		if(!stunned){
			movementEnabled=true;
			attackEnabled=true;
		}
	}

	public boolean isSnared() {
		return snared;
	}

	public void setSnared(boolean snared, float snareDuration) {
		this.snared = snared;
		if(snared){
			setMovementEnabled(false);
			this.snareDuration=snareDuration;
			timeSnared=0;
		}
		else movementEnabled=true;
	}

	public float getTimeOnGround(){
		return timeOnGround;
	}
	
	public void setTimeonGround(float timeOnGround){
		if(timeOnGround<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_GROUNDTIME);
		this.timeOnGround=timeOnGround;
	}

}
