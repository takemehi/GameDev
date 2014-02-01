package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public abstract class PlayableCharacter extends GameCharacter{
	
	public static final String KEY_HEALTH = "health";
	public static final String KEY_VELOCITY_X = "velocity_x";
	public static final String KEY_VELOCITY_Y = "velocity_y";
	
	//Exceptions
	private static final String EXCEPTION_ILLEGAL_ID_CHARACTERCLASS = "This character class doesn't exist!";
	private static final String EXCEPTION_ILLEGAL_ID_TEAM = "This teamId doesn't exist!";
	//Userdata
	public final static String USERDATA_PLAYER = "player";
	//Class ids
	public final static int ID_WARRIOR = 0;
	public final static int ID_MAGE = 1;
	//Team constants
	public static final int ID_TEAM_BLUE = 0;
	public static final int ID_TEAM_RED = 1;
	public static final int GROUP_TEAM_BLUE = -1;
	public static final int GROUP_TEAM_RED = -2;
	//other
	//TODO change that value
	public final static float MIN_TIME_ON_GROUND = 0.1f;
	
	private int characterClass;
	private boolean atSpawn;
	public boolean doCapture;
	private boolean capturing;
	private int pointEligibleToCapture;
	private int teamId;
	private float timeCapturing;
	private float respawnTime;
	private float respawnTimer;
	
	private float timeToCapturePoint;
	
	public PlayableCharacter(World world, Shape shape, float density, float friction,
			float restitution, Vector2 position, Vector2 baseVelocity,
			Vector2 maxVelocity, int maxHealth, int characterClass, int teamId, int id, float timeToCapturePoint) {
		super(world, shape, density, friction, restitution, position, baseVelocity,
				maxVelocity, maxHealth, id);
		
		if(teamId!=ID_TEAM_BLUE && teamId!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ID_TEAM);
		if(characterClass!=ID_MAGE && characterClass!=ID_WARRIOR) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ID_CHARACTERCLASS);
		
		this.characterClass=characterClass;
		this.timeToCapturePoint = timeToCapturePoint;
		this.teamId=teamId;
		if(teamId==ID_TEAM_BLUE)
			this.getFixtureDef().filter.groupIndex=PlayableCharacter.GROUP_TEAM_BLUE;
		else
			this.getFixtureDef().filter.groupIndex=PlayableCharacter.GROUP_TEAM_RED;
		pointEligibleToCapture=-1;
		capturing=false;
		timeCapturing=0;
		doCapture=false;
		respawnTime = 0;
		respawnTimer = 0;
	}
	

	@Override
	public void setFixture(Fixture fixture) {
		super.setFixture(fixture);
		getFixture().setUserData(USERDATA_PLAYER);
	}
	
	@Override
	public void setBody(Body body){
		super.setBody(body);

	}
	
	@Override
	public void update(float deltaTime, Array<CapturePoint> capturePoints){
		super.update(deltaTime, capturePoints);
		
		CapturePoint point = null;
		for(CapturePoint cp: capturePoints){
			if(cp.getCapturePoint().getId()==getPointEligibleToCapture()){
				point=cp;
				break;
			}
		}
		
		if(getPointEligibleToCapture()!=-1 && doCapture && !capturing){
			if(point!= null && !point.isBeingCaptured() && point.getTeamId() != teamId){
				capturing=true;
				timeCapturing = 0;
				point.setBeingCaptured(true);
			}
			else {
				doCapture = false;
			}
		}
		if(capturing){
			timeCapturing+=deltaTime;
			if(timeCapturing>=timeToCapturePoint){
				point.setBeingCaptured(false);
				doCapture = false;
				capturing = false;
				timeCapturing = 0;
				point.setTeamId(getTeamId(), getId());
			}
			if(getBody().getLinearVelocity().x!=0 || getBody().getLinearVelocity().y!=0 || hasLostHealth()
					|| isAttacking1() || isAttacking2() || isAttacking3()){
				capturing=false;
				doCapture = false;
				point.setBeingCaptured(false);
			}
		}
		
		if (getHealth() <= 0 && respawnTime > 0) {
			respawnTimer += deltaTime;
			
			if (respawnTimer >= respawnTime) {
				statusUpdateListener.respawn(); //end the respawn timer
				
				setHealth(getMaxHealth());
				setMovementEnabled(true);
				setAttackEnabled(true);
				getBody().setTransform(getPositionOriginal(), 0); // respawn at spawn
				
				//send position update
				statusUpdateListener.positionChanged(getPositionOriginal());
			}
		}
		
		attack1.update(deltaTime);
		attack2.update(deltaTime);
		attack3.update(deltaTime);
	}
	
	public void startRespawning(float respawnTime) {
		this.respawnTime = respawnTime;
		respawnTimer = 0;
	}
	
	//getter / setter
	
	public boolean isAtSpawn(){
		return atSpawn;
	}

	public void setAtSpawn(boolean atSpawn){
		this.atSpawn=atSpawn;
	}
	
	public boolean isCapturing(){
		return capturing;
	}
	
	public void setCapturing(boolean doCapture){
		this.doCapture=doCapture;
	}
	
	public int getCharacterClass(){
		return characterClass;
	}
	
	public int getPointEligibleToCapture(){
		return pointEligibleToCapture;
	}
	
	public void setPointEligibleToCapture(int pointEligibleToCapture){
		this.pointEligibleToCapture=pointEligibleToCapture;
	}
	
	public int getTeamId(){
		return teamId;
	}
}
