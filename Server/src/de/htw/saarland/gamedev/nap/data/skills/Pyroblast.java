package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Pyroblast extends Skill{

	private static final Vector2 COMPENSATE_GRAVITY = new Vector2(0,20);
	
	public static final float COOLDOWN = 6f;
	public static final float CASTTIME = 1.5f;
	public static final float RADIUS = 0.2f;
	public static final float TRAVEL_DISTANCE = 3.5f;
	public static final float VELOCITY = 5;
	public static final int DAMAGE = 30;
	
	public static final String USERDATA_PYROBLAST = "pyroblast";
	
	private SensorEntity ball;
	private Vector2 velocityBall;
	
	
	public Pyroblast() {
		super(COOLDOWN, CASTTIME);
	}

	@Override
	protected void start(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords){
		
		CircleShape shape = new CircleShape();
		shape.setRadius(RADIUS);
		Vector2 position = new Vector2();
		position.y=character.getBody().getPosition().y;
		if(character.getOrientation()==PlayableCharacter.ORIENTATION_LEFT)
			position.x=character.getBody().getPosition().x-0.2f;
		else
			position.x=character.getBody().getPosition().x+0.2f;
		ball = new SensorEntity(shape, position, currentId);
		ball.setBody(world.createBody(ball.getBodyDef()));
		ball.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
		ball.getFixture().setUserData(USERDATA_PYROBLAST);
		ball.getBody().setType(BodyDef.BodyType.DynamicBody);							
		
		Vector2 direction = mouseCoords.sub(character.getBody().getPosition());
		direction = direction.nor();
		velocityBall=direction.mul(VELOCITY);
		ball.getBody().setLinearVelocity(velocityBall);
		character.setMovementEnabled(true);
		
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {
		
		if(isOnCooldown() && !isCasted()) {
			character.setMovementEnabled(false);
		}
		
		if(isOnCooldown() && isCasted()){
			try{
				ball.setDistanceTraveled((new Vector2(ball.getPositionOriginal().x-ball.getBody().getPosition().x
						,ball.getPositionOriginal().y-ball.getBody().getPosition().y)).len());
				ball.getBody().applyForceToCenter(COMPENSATE_GRAVITY, true);
				if(ball.getDistanceTraveled()>= TRAVEL_DISTANCE) ball.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}catch (Exception e){}
		}
		
	}

	@Override
	public void cleanUp(World world) {
		if(ball!=null && ball.getBody().getUserData()!=null && ball.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) && !world.isLocked()){
			world.destroyBody(ball.getBody());
			ball=null;
		}			
	}
}