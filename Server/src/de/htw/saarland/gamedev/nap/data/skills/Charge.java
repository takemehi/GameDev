package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Charge extends Skill {
	
	private static final Vector2 COMPENSATE_GRAVITY = new Vector2(0,20);
	
	public static final float COOLDOWN = 3f;
	public static final float CASTTIME = 0f;
	public static final float VELOCITY = 10;
	public static final float TRAVEL_DISTANCE = 20f;
	public static final int DAMAGE = 30;
	
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
		}catch(Exception e){}
		traveling=false;
		distanceTraveled=0;
	}

}
