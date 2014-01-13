package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Charge extends Skill {
	
	private static final Vector2 COMPENSATE_GRAVITY = new Vector2(0,20);
	
	public static final float COOLDOWN = 3f;
	public static final float CASTTIME = 0f;
	public static final float VELOCITY = 10;
	public static final float TRAVEL_DISTANCE = 20f;
	public static final int DAMAGE = 35;
	public static final float DURATION_STUN = 1f;
	
	public static final String USERDATA_CHARGE = "charge";
	
	private PlayableCharacter character;
	private Vector2 positionStart;
	private float distanceTraveled;
	private boolean traveling;

	public Charge() {
		super(COOLDOWN, CASTTIME);
		
		distanceTraveled=0;
		traveling=false;
	}

	@Override
	public void cleanUp(World world) {	
	}

	@Override
	protected void start(World world, PlayableCharacter character,
			int currentId, Vector2 mouseCoords) {

		Vector2 direction = mouseCoords.sub(character.getBody().getPosition());
		direction = direction.nor();
		Vector2 velocity=direction.mul(VELOCITY);
		character.setMovementEnabled(false);
		character.getBody().setLinearVelocity(velocity);
		character.getBody().setGravityScale(0);
		character.getFixture().setUserData(USERDATA_CHARGE);
		positionStart= new Vector2(character.getBody().getPosition().x, character.getBody().getPosition().y);
		traveling=true;
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character,
			int currentId, Vector2 mouseCoords) {
		this.character=character;
		
		if(isOnCooldown() && isCasted()){
			if(traveling){
				try{
					if(distanceTraveled>=TRAVEL_DISTANCE){
						character.getBody().setLinearVelocity(0,0);
						character.setMovementEnabled(true);
						character.getBody().setGravityScale(1);
						traveling=false;
						distanceTraveled=0;
						character.getFixture().setUserData(PlayableCharacter.USERDATA_PLAYER);
					}else{
						distanceTraveled+=((new Vector2(positionStart.x-character.getBody().getPosition().x
								,positionStart.y-character.getBody().getPosition().y)).len());
					}
				}catch(Exception e){}
			}
		}
			
	}
	
	@Override
	public void reset(){
		super.reset();
		try{
			character.getBody().setLinearVelocity(0,0);
			character.setMovementEnabled(true);
			character.getBody().setGravityScale(1);
			character.getFixture().setUserData(PlayableCharacter.USERDATA_PLAYER);
		}catch(Exception e){}
		traveling=false;
		distanceTraveled=0;
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<Player> players, boolean isClient){
		//Charge hitting the world
		if(fA.getUserData()==Charge.USERDATA_CHARGE && fB.getUserData()==GameWorld.USERDATA_FIXTURE_WORLD){
			for(Player p: players){
				if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().getAttack2().reset();
			}
		}
		else if(fB.getUserData()==Charge.USERDATA_CHARGE && fA.getUserData()==GameWorld.USERDATA_FIXTURE_WORLD){
			for(Player p: players){
				if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().getAttack2().reset();
			}
		}
		//Charge hitting a player
		else if(fA.getUserData()==Charge.USERDATA_CHARGE && fB.getUserData()==PlayableCharacter.USERDATA_PLAYER){	
			for(Player p: players){
				if(p.getPlChar().getFixture().equals(fB)){
					if(!isClient){
						p.getPlChar().setHealth(p.getPlChar().getHealth()-Charge.DAMAGE);
						p.getPlChar().setStunned(true, Charge.DURATION_STUN);
					}
					for(Player pl: players){
						if(pl.getPlChar().getFixture().equals(fA)) pl.getPlChar().getAttack2().reset();
					}
					break;
				}
			}
		}
		else if(fB.getUserData()==Charge.USERDATA_CHARGE && fA.getUserData()==PlayableCharacter.USERDATA_PLAYER){
			for(Player p: players){
				if(p.getPlChar().getFixture().equals(fA)){
					if(!isClient){
						p.getPlChar().setHealth(p.getPlChar().getHealth()-Charge.DAMAGE);
						p.getPlChar().setStunned(true, Charge.DURATION_STUN);
					}
					for(Player pl: players){
						if(pl.getPlChar().getFixture().equals(fB)) pl.getPlChar().getAttack2().reset();
					}
					break;
				}
			}
		}
	}

}
