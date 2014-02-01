package de.htw.saarland.gamedev.nap.data.skills;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class Nova extends Skill{
	
	private static final String KEY_RADIUS = "radius";
	private static final String KEY_FORCE = "force";
	private static final String KEY_STUN_DURATION = "stun_duration";	
	private static final String META_FILE_PATH_SERVER = GameServer.FOLDER_DATA_SERVER + "meta/characters/mage/nova.txt";
	private static final String META_FILE_PATH_CLIENT = "data/meta/characters/mage/nova.txt";
	
	public static final float COOLDOWN;
	public static final float CASTTIME;
	public static final float RADIUS;
	public static final float FORCE;
	public static final int DAMAGE;
	public static final float DURATION_STUN;
	
	public static final String USERDATA_NOVA = "nova";
	
	private SensorEntity nova;
	private float timeLiving;
	
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
			FORCE = values.getValueFloat(KEY_FORCE);
			DAMAGE = values.getValueInt(KEY_DAMAGE);
			DURATION_STUN = values.getValueFloat(KEY_STUN_DURATION);
			
		} catch (IOException e) {
			throw new RuntimeException(e); //let the program die on error
		}
	}
	
	public Nova(PlayableCharacter character, int skillNr){
		super(character, COOLDOWN, CASTTIME, false, skillNr);
		cast=false;
		timeLiving=0;
	}

	@Override
	public void start(World world, PlayableCharacter character, Vector2 direction) {		
		CircleShape circle = new CircleShape();
		circle.setRadius(RADIUS);
				
		nova = new SensorEntity(world, circle, character.getBody().getPosition(), -1);
		nova.setBody(world.createBody(nova.getBodyDef()));
		nova.getBody().setType(BodyType.DynamicBody);
		nova.getBody().setGravityScale(0);
		nova.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		nova.setFixture(nova.getBody().createFixture(nova.getFixtureDef()));
		nova.getFixture().setUserData(USERDATA_NOVA);
		
		timeLiving=0;
		
		if (skillEventListener != null) {
			skillEventListener.skillStarted(this, nova);
		}
	}

	@Override
	public void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords, float deltaTime) {
		if(isOnCooldown()){
			timeLiving+=deltaTime;
			if(timeLiving>=0.1f){
				try{
					nova.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
				}catch(Exception e){}
			}
		}
	}

	@Override
	public void cleanUp() {
		if(nova!= null && nova.getBody().getUserData()!=null && nova.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) 
				&& !getPlayableCharacter().getWorld().isLocked()){
			getPlayableCharacter().getWorld().destroyBody(nova.getBody());
			
			if (skillEventListener != null) {
				skillEventListener.skillEnd(this, nova);
			}
			
			nova=null;
		}		
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<IPlayer> players, boolean isClient){
		// Nova hitting a player
		if(!isClient){
			if (fA.getUserData() == Nova.USERDATA_NOVA && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				Vector2 direction;
	
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						direction = new Vector2(p.getPlChar().getBody().getPosition().x - fA.getBody().getPosition().x, p.getPlChar().getBody().getPosition().y - fA.getBody().getPosition().y);
						direction = direction.nor();
						direction.mul(Nova.FORCE);
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Nova.DAMAGE);
						p.getPlChar().setStunned(true, Nova.DURATION_STUN);
						p.getPlChar().getBody().applyLinearImpulse(direction, p.getPlChar().getBody().getPosition(), true);
						break;
					}
				}
			} 
			else if (fB.getUserData() == Nova.USERDATA_NOVA && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				Vector2 direction;
	
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						direction = new Vector2(p.getPlChar().getBody().getPosition().x - fB.getBody().getPosition().x, p.getPlChar().getBody().getPosition().y - fB.getBody().getPosition().y);
						direction = direction.nor();
						direction.mul(Nova.FORCE);
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Nova.DAMAGE);
						p.getPlChar().setStunned(true, Nova.DURATION_STUN);
						p.getPlChar().getBody().applyLinearImpulse(direction, p.getPlChar().getBody().getPosition(), true);
						break;
					}
				}
			}
		}
	}
}
