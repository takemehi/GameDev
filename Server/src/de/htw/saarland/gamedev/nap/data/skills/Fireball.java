package de.htw.saarland.gamedev.nap.data.skills;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class Fireball extends Skill{
	
	private static final String KEY_RADIUS = "radius";
	private static final String KEY_TRAVEL_DISTANCE = "travel_distance";
	private static final String KEY_VELOCITY = "velocity";	
	private static final String META_FILE_PATH_SERVER = GameServer.FOLDER_DATA_SERVER + "meta/characters/mage/fireball.txt";
	private static final String META_FILE_PATH_CLIENT = "data/meta/characters/mage/fireball.txt";
	
	public static final float COOLDOWN;
	public static final float CASTTIME;
	public static final float RADIUS;
	public static final float TRAVEL_DISTANCE;
	public static final float VELOCITY;
	public static final int DAMAGE;
	
	public static final String USERDATA_FIREBALL = "fireball";

	private Array<SensorEntity> fireBalls;
	
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
			RADIUS = values.getValueFloat(KEY_RADIUS);
			TRAVEL_DISTANCE = values.getValueFloat(KEY_TRAVEL_DISTANCE);
			VELOCITY = values.getValueFloat(KEY_VELOCITY);
			DAMAGE = values.getValueInt(KEY_DAMAGE);
			
		} catch (IOException e) {
			throw new RuntimeException(e); //let the program die on error
		}
	}
	
	public Fireball(PlayableCharacter character, int skillNr) {
		super(character, COOLDOWN, CASTTIME, false, skillNr);
		
		fireBalls = new Array<SensorEntity>();
		cast=false;
	}

	@Override
	public void start(World world, PlayableCharacter character, Vector2 direction) {
		
		SensorEntity ball;
		CircleShape shape = new CircleShape();
		shape.setRadius(RADIUS);
		ball = new SensorEntity(world, shape, character.getBody().getPosition().x, character.getBody().getPosition().y, -1);
		ball.setBody(world.createBody(ball.getBodyDef()));
		ball.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
		ball.getFixture().setUserData(USERDATA_FIREBALL);
		ball.getBody().setType(BodyDef.BodyType.DynamicBody);
		Vector2 velocityBall=direction.mul(VELOCITY);
		ball.getBody().setGravityScale(0);
		ball.getBody().setLinearVelocity(velocityBall);
		fireBalls.add(ball);
		
		if (skillEventListener != null) {
			skillEventListener.skillStarted(this, ball);
		}
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords, float deltaTime) {		
		
		for(SensorEntity s: fireBalls){
			if(s.getDistanceTraveled()>TRAVEL_DISTANCE){
				s.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}else{
				s.setDistanceTraveled((new Vector2(s.getPositionOriginal().x-s.getBody().getPosition().x
						,s.getPositionOriginal().y-s.getBody().getPosition().y)).len());
			}
		}
	}
	
	@Override
	public void cleanUp(){
		for (Iterator<SensorEntity> it = fireBalls.iterator(); it.hasNext(); ) {
		    SensorEntity s = it.next();
		    if(s.getBody().getUserData()!=null && s.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) 
		    		&& !getPlayableCharacter().getWorld().isLocked()){
		    	if (skillEventListener != null) {
					skillEventListener.skillEnd(this, s);
				}
				fireBalls.removeValue(s, true);
				getPlayableCharacter().getWorld().destroyBody(s.getBody());
			}
		}
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<IPlayer> players, boolean isClient){
		// Fireball hitting the world
		if (fA.getUserData() == Fireball.USERDATA_FIREBALL && fB.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
			fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		} else if (fB.getUserData() == Fireball.USERDATA_FIREBALL && fA.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
			fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
		// Fireball hitting a player
		else if (fA.getUserData() == Fireball.USERDATA_FIREBALL && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			if(!isClient){
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Fireball.DAMAGE);
						break;
					}
				}
			}
			fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		} else if (fB.getUserData() == Fireball.USERDATA_FIREBALL && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			if(!isClient){
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Fireball.DAMAGE);
						break;
					}
				}
			}
			fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
	}
	
}
