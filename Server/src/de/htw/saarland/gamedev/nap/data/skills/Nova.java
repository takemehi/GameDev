package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Nova extends Skill{
	
	public static final float COOLDOWN = 0.3f;
	public static final float CASTTIME = 0f;
	public static final float RADIUS = 1f;
	public static final float TRAVEL_DISTANCE = 2f;
	public static final float FORCE = 1;
	public static final int DAMAGE = 5;
	
	public static final String USERDATA_NOVA = "nova";
	
	private SensorEntity nova;
	private PlayableCharacter character;
	private boolean started;
	private int swingCount;
	
	public Nova(){
		super(COOLDOWN, CASTTIME);
		
	}

	@Override
	public void start(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {
		this.character=character;
		
		CircleShape circle = new CircleShape();
		circle.setRadius(RADIUS);
				
		nova = new SensorEntity(circle, character.getBody().getPosition(), currentId);
		nova.setBody(world.createBody(nova.getBodyDef()));
		nova.getBody().setType(BodyType.DynamicBody);
		nova.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		nova.setFixture(nova.getBody().createFixture(nova.getFixtureDef()));
		nova.getFixture().setUserData(USERDATA_NOVA);
		
		started=true;
	}

	@Override
	public void update() {
		if(started){
			if(swingCount==1){
				swingCount=0;
				started=false;
				nova.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			else swingCount++;
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
