package de.htw.saarland.gamedev.nap.data.skills;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class Charge extends Skill {
	
	private static final String KEY_TRAVEL_DISTANCE = "travel_distance";
	private static final String KEY_VELOCITY = "velocity";
	private static final String KEY_STUN_DURATION = "stun_duration";	
	private static final String META_FILE_PATH_SERVER = GameServer.FOLDER_DATA_SERVER + "meta/characters/warrior/charge.txt";
	private static final String META_FILE_PATH_CLIENT = "data/meta/characters/warrior/charge.txt";
	
	public static final float COOLDOWN;
	public static final float CASTTIME;
	public static final float VELOCITY;
	public static final float TRAVEL_DISTANCE;
	public static final int DAMAGE;
	public static final float DURATION_STUN;
	
	public static final String USERDATA_CHARGE = "charge";
	
	private PlayableCharacter character;
	private Vector2 positionStart;
	private float distanceTraveled;
	private boolean traveling;
	private Vector2 posBefore;
	
	static {
		try {
			KeyValueFile values = null;
			if ((new File(META_FILE_PATH_SERVER)).exists()) {
				values = new KeyValueFile(META_FILE_PATH_SERVER);
			}
			else {
				values = new KeyValueFile(META_FILE_PATH_CLIENT);
			}
			
			values.load();
			
			COOLDOWN = values.getValueFloat(KEY_COOLDOWN);
			CASTTIME = values.getValueFloat(KEY_CASTTIME);
			VELOCITY = values.getValueFloat(KEY_VELOCITY);
			TRAVEL_DISTANCE = values.getValueFloat(KEY_TRAVEL_DISTANCE);
			DURATION_STUN = values.getValueFloat(KEY_STUN_DURATION);
			DAMAGE = values.getValueInt(KEY_DAMAGE);
			
		} catch (IOException e) {
			throw new RuntimeException(e); //let the program die on error
		}
	}

	public Charge(PlayableCharacter character, int skillNr) {
		super(character, COOLDOWN, CASTTIME, false, skillNr);
		
		cast=false;
		distanceTraveled=0;
		traveling=false;
	}

	@Override
	public void cleanUp() {	
	}

	@Override
	public void start(World world, PlayableCharacter character, Vector2 direction) {
		if (client) {
			return;
		}
		
		Vector2 velocity=direction.mul(VELOCITY);
		character.setMovementEnabled(false);
		character.getBody().setLinearVelocity(velocity);
		character.getBody().setGravityScale(0);
		character.getFixture().setUserData(USERDATA_CHARGE);
		positionStart= new Vector2(character.getBody().getPosition().x, character.getBody().getPosition().y);
		traveling=true;
		posBefore = new Vector2(positionStart);
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords, float deltaTime) {
		if (client) {
			return;
		}
		
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
						if (!posBefore.equals(character.getBody().getPosition())) {
							//only compute distance if position changed
							distanceTraveled+=((new Vector2(positionStart.x-character.getBody().getPosition().x
									,positionStart.y-character.getBody().getPosition().y)).len());
							posBefore.x = character.getBody().getPosition().x;
							posBefore.y = character.getBody().getPosition().y;
						}
					}
				}catch(Exception e){
					e.getLocalizedMessage(); //for debugging (can set bp here)
				}
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
	
	public static void handleContact(Fixture fA, Fixture fB, Array<IPlayer> players, boolean isClient){
		//Charge hitting the world
		if(fA.getUserData()==Charge.USERDATA_CHARGE && fB.getUserData()==GameWorld.USERDATA_FIXTURE_WORLD){
			for(IPlayer p: players){
				if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().getAttack2().reset();
			}
		}
		else if(fB.getUserData()==Charge.USERDATA_CHARGE && fA.getUserData()==GameWorld.USERDATA_FIXTURE_WORLD){
			for(IPlayer p: players){
				if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().getAttack2().reset();
			}
		}
		//Charge hitting a player
		else if(fA.getUserData()==Charge.USERDATA_CHARGE && fB.getUserData()==PlayableCharacter.USERDATA_PLAYER){	
			for(IPlayer p: players){
				if(p.getPlChar().getFixture().equals(fB)){
					if(!isClient){
						p.getPlChar().setHealth(p.getPlChar().getHealth()-Charge.DAMAGE);
						p.getPlChar().setStunned(true, Charge.DURATION_STUN);
					}
					for(IPlayer pl: players){
						if(pl.getPlChar().getFixture().equals(fA)) pl.getPlChar().getAttack2().reset();
					}
					break;
				}
			}
		}
		else if(fB.getUserData()==Charge.USERDATA_CHARGE && fA.getUserData()==PlayableCharacter.USERDATA_PLAYER){
			for(IPlayer p: players){
				if(p.getPlChar().getFixture().equals(fA)){
					if(!isClient){
						p.getPlChar().setHealth(p.getPlChar().getHealth()-Charge.DAMAGE);
						p.getPlChar().setStunned(true, Charge.DURATION_STUN);
					}
					for(IPlayer pl: players){
						if(pl.getPlChar().getFixture().equals(fB)) pl.getPlChar().getAttack2().reset();
					}
					break;
				}
			}
		}
	}

}
