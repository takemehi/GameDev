package de.htw.saarland.gamedev.nap.data.skills;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Fireball extends Skill{
	
	private static final Vector2 COMPENSATE_GRAVITY = new Vector2(0,20);
	
	public static final float COOLDOWN = 0.3f;
	public static final float CASTTIME = 0f;
	public static final float RADIUS = 0.1f;
	public static final float TRAVEL_DISTANCE = 2f;
	public static final float VELOCITY = 6;
	public static final int DAMAGE = 35;
	
	public static final String USERDATA_FIREBALL = "fireball";

	private Array<SensorEntity> fireBalls;
	
	public Fireball() {
		super(COOLDOWN, CASTTIME);
		
		fireBalls = new Array<SensorEntity>();
	}

	@Override
	protected void start(World world, PlayableCharacter character, int currentId, Vector2 mouseCoords) {
		
		SensorEntity ball;
		CircleShape shape = new CircleShape();
		shape.setRadius(RADIUS);
		ball = new SensorEntity(shape, character.getBody().getPosition().x, character.getBody().getPosition().y, currentId);
		ball.setBody(world.createBody(ball.getBodyDef()));
		ball.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
		ball.getFixture().setUserData(USERDATA_FIREBALL);
		ball.getBody().setType(BodyDef.BodyType.DynamicBody);
		Vector2 direction = mouseCoords.sub(character.getBody().getPosition());
		direction = direction.nor();
		Vector2 velocityBall=direction.mul(VELOCITY);
		ball.getBody().setLinearVelocity(velocityBall);
		fireBalls.add(ball);
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, int currentId, Vector2 mouseCoords) {		
		
		for(SensorEntity s: fireBalls){
			if(s.getDistanceTraveled()>TRAVEL_DISTANCE){
				s.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}else{
				s.setDistanceTraveled((new Vector2(s.getPositionOriginal().x-s.getBody().getPosition().x
						,s.getPositionOriginal().y-s.getBody().getPosition().y)).len());
				s.getBody().applyForceToCenter(COMPENSATE_GRAVITY, true);
			}
		}
	}
	
	@Override
	public void cleanUp(World world){
		for (Iterator<SensorEntity> it = fireBalls.iterator(); it.hasNext(); ) {
		    SensorEntity s = it.next();
		    if(s.getBody().getUserData()!=null && s.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) && !world.isLocked()){
				fireBalls.removeValue(s, true);
				world.destroyBody(s.getBody());
			}
		}
	}
	
}
