package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Nova extends Skill{
	
	public static final float COOLDOWN = 5f;
	public static final float CASTTIME = 0f;
	public static final float RADIUS = 1f;
	public static final float TRAVEL_DISTANCE = 2f;
	public static final float FORCE = 1;
	public static final int DAMAGE = 5;
	public static final float DURATION_STUN = 0.4f;
	
	public static final String USERDATA_NOVA = "nova";
	
	private SensorEntity nova;
	
	public Nova(){
		super(COOLDOWN, CASTTIME);
	}

	@Override
	public void start(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {		
		CircleShape circle = new CircleShape();
		circle.setRadius(RADIUS);
				
		nova = new SensorEntity(circle, character.getBody().getPosition(), currentId);
		nova.setBody(world.createBody(nova.getBodyDef()));
		nova.getBody().setType(BodyType.DynamicBody);
		nova.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		nova.setFixture(nova.getBody().createFixture(nova.getFixtureDef()));
		nova.getFixture().setUserData(USERDATA_NOVA);
		
	}

	@Override
	public void doUpdate(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {
		if(isOnCooldown()){
			try{
				nova.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}catch(Exception e){}
		}
	}

	@Override
	public void cleanUp(World world) {
		if(nova!= null && nova.getBody().getUserData()!=null && nova.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) && !world.isLocked()){
			world.destroyBody(nova.getBody());
			nova=null;
		}		
	}
}