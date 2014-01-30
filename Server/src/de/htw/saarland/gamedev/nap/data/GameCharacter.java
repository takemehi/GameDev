package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.shapes.EntityShape;
import de.htw.saarland.gamedev.nap.data.skills.Skill;

public class GameCharacter extends MoveableEntity{
	
	//Exceptions
	private final static String EXCEPTION_ILLEGAL_HEALTH_MAX = "MaxHealth value has to be greater than zero!";
	private final static String EXCEPTION_ILLEGAL_GROUNDTIME = "Groundtime value can't be smaller than zero!";
	private final static String EXCEPTION_ILLEGAL_ORIENTATION = "Orientation can only be 0 or 1!";
	
	public static final String USERDATA_FOOT_SENSOR_PREFIX = "footSensor.";
	
	public static final int ORIENTATION_LEFT = 0;
	public static final int ORIENTATION_RIGHT = 1;

	private int maxHealth;
	private int health;
	private boolean jumping;
	private float timeOnGround;
	private boolean attackEnabled;
	private volatile boolean movementEnabled;
	private boolean stunned;
	private boolean snared;
	private volatile boolean movingLeft;
	private volatile boolean movingRight;
	private volatile boolean movingDown;
	private volatile boolean movingUp;
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
	
	private volatile boolean attacking1;
	private volatile boolean attacking2;
	private volatile boolean attacking3;
	
	protected IStatusUpdateListener statusUpdateListener;
	
	protected int footContacts = 0;
	
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
	
	@Override
	public void setBody(Body b) {
		super.setBody(b);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.18f, 0.05f, ((EntityShape)getFixtureDef().shape).footSensorPos, 0);
		FixtureDef fSensor = new FixtureDef();
		fSensor.shape = shape;
		fSensor.density = 1;
		fSensor.isSensor = true;
		b.createFixture(fSensor).setUserData(USERDATA_FOOT_SENSOR_PREFIX + getId());
	}
	
	public void setStatusUpdateListener(IStatusUpdateListener statusUpdateListener) {
		this.statusUpdateListener = statusUpdateListener;
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
		
		setLeft(movingLeft);
		setRight(movingRight);
		
		if(jumping && footContacts >= 1){
			setUp(false);
			jumping=false;
		}
		
		if(footContacts == 0) {
			timeOnGround = 0;
		}
		else {
			timeOnGround += deltaTime;
		}
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
		this.movingUp=up;
		if(movementEnabled){
			if(movingUp && footContacts >= 1){
				getBody().setAwake(true);
				getBody().setLinearVelocity(getBody().getLinearVelocity().x, getBaseVelocity().y);
				jumping=true;
			}
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
				}
			}else{
				this.attacking1=false;
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
				}
			}else{
				this.attacking2=false;
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
				}
			}else{
				this.attacking3=false;
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
		boolean alreadyDead = this.health == 0 && health <= 0; //character is already dead
		
		if (health>maxHealth) {
			this.health=maxHealth;
		}
		else if (health < 0) {
			this.health = 0;
		}
		else {
			this.health=health;
		}
		
		if (statusUpdateListener != null && !alreadyDead) {
			statusUpdateListener.hpUpdated(this.health);
		}
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
	
	public void setMovementEnabled(boolean movementEnabled) {
		if (movementEnabled && (stunned || snared)) {
			return; //cannot set movement to enabled during stun or snare
		}
		
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
		if (statusUpdateListener != null) {
			statusUpdateListener.stunUpdated(stunned);
		}
		
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
		else {
			movementEnabled=true;
			attackEnabled=true;
		}
	}

	public boolean isSnared() {
		return snared;
	}

	public void setSnared(boolean snared, float snareDuration) {
		if (statusUpdateListener != null) {
			statusUpdateListener.snareUpdated(snared);
		}
		
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
	
	public boolean isMoving() {
		return movementEnabled && (movingLeft || movingRight || movingUp || movingDown);
	}
	
	public static void handleFootSensorContact(Fixture fA, Fixture fB, Array<IPlayer> players, int add) {
		if (((String)fA.getUserData()).startsWith(USERDATA_FOOT_SENSOR_PREFIX) || ((String)fB.getUserData()).startsWith(USERDATA_FOOT_SENSOR_PREFIX)) {
				String dat = null;
				if (((String)fA.getUserData()).startsWith(USERDATA_FOOT_SENSOR_PREFIX)) {
					if (!fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_WORLD) &&
						!fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) &&
						!fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)) {
						return; //no contact with ground
					}
					
					dat = ((String)fA.getUserData()).replace(USERDATA_FOOT_SENSOR_PREFIX, "");
				}
				else {
					if (!fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_WORLD) &&
							!fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) &&
							!fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)) {
							return; //no contact with ground
						}
					
					dat = ((String)fB.getUserData()).replace(USERDATA_FOOT_SENSOR_PREFIX, "");
				}
				
				int entId = Integer.parseInt(dat);
				
				for (IPlayer player: players) {
					if (player.getPlChar().getId() == entId) {
						player.getPlChar().footContacts += add;
						break;
					}
				}
		}
	}

}
