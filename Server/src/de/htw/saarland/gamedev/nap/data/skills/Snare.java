package de.htw.saarland.gamedev.nap.data.skills;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class Snare extends Skill {

	private static final String KEY_SNARE_DURATION = "snare_duration";
	private static final String META_FILE_PATH_SERVER = GameServer.FOLDER_DATA_SERVER + "meta/characters/warrior/snare.txt";
	private static final String META_FILE_PATH_CLIENT = "data/meta/characters/warrior/snare.txt";
	
	public static final float COOLDOWN;
	public static final float CASTTIME;
	public static final int DAMAGE;
	public static final float DURATION_SNARE;
	
	public static final String USERDATA_SNARE = "snare";
	
	private SensorEntity snare;
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
			DAMAGE = values.getValueInt(KEY_DAMAGE);
			DURATION_SNARE = values.getValueFloat(KEY_SNARE_DURATION);
			
		} catch (IOException e) {
			throw new RuntimeException(e); //let the program die on error
		}
	}
	
	public Snare(PlayableCharacter character, int skillNr) {
		super(character, COOLDOWN, CASTTIME, false, skillNr);
		cast=false;
		timeLiving=0;
	}

	@Override
	public void cleanUp() {
		if(snare!= null && snare.getBody().getUserData()!=null && snare.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) 
				&& !getPlayableCharacter().getWorld().isLocked()){
			getPlayableCharacter().getWorld().destroyBody(snare.getBody());
			snare=null;
		}
		
	}

	@Override
	public void start(World world, PlayableCharacter character, Vector2 direction) {

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 0.35f, new Vector2(0.4f,0), 0);
		Vector2 position = new Vector2();
		position.y=character.getBody().getPosition().y;
		if(character.getOrientation()==PlayableCharacter.ORIENTATION_LEFT)
			position.x=character.getBody().getPosition().x-0.8f;
		else
			position.x=character.getBody().getPosition().x+0.0f;
		snare = new SensorEntity(world, shape, position, -1);
		snare.setBody(world.createBody(snare.getBodyDef()));
		snare.getBody().setType(BodyType.DynamicBody);
		snare.getBody().setGravityScale(0);
		snare.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		snare.setFixture(snare.getBody().createFixture(snare.getFixtureDef()));
		snare.getFixture().setUserData(USERDATA_SNARE);
		
		timeLiving=0;
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords, float deltaTime) {
		if(isOnCooldown()){
			timeLiving+=deltaTime;
			if(timeLiving>=0.1f){
				try{
					snare.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
				}catch(Exception e){}
			}
		}		
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<IPlayer> players, boolean isClient){
		//Snare hitting a player
		if(!isClient){
			if (fA.getUserData() == Snare.USERDATA_SNARE && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Snare.DAMAGE);
						p.getPlChar().setSnared(true, Snare.DURATION_SNARE);
						break;
					}
				}
			}
			else if (fB.getUserData() == Snare.USERDATA_SNARE && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Snare.DAMAGE);
						p.getPlChar().setSnared(true, Snare.DURATION_SNARE);
						break;
					}
				}
			}
		}
	}

}
