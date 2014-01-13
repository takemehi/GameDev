package de.htw.saarland.gamedev.nap.data.skills;

import java.util.Iterator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Fireball extends Skill{
	
	public static final float COOLDOWN = 0.3f;
	public static final float CASTTIME = 0f;
	public static final float RADIUS = 0.1f;
	public static final float TRAVEL_DISTANCE = 2f;
	public static final float VELOCITY = 6;
	public static final int DAMAGE = 5;
	
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
		ball = new SensorEntity(world, shape, character.getBody().getPosition().x, character.getBody().getPosition().y, currentId);
		ball.setBody(world.createBody(ball.getBodyDef()));
		ball.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
		ball.getFixture().setUserData(USERDATA_FIREBALL);
		ball.getBody().setType(BodyDef.BodyType.DynamicBody);
		Vector2 direction = mouseCoords.sub(character.getBody().getPosition());
		direction = direction.nor();
		Vector2 velocityBall=direction.mul(VELOCITY);
		ball.getBody().setGravityScale(0);
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
	
	public static void handleContact(Fixture fA, Fixture fB, Array<Player> players){
		// Fireball hitting the world
		if (fA.getUserData() == Fireball.USERDATA_FIREBALL && fB.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
			fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		} else if (fB.getUserData() == Fireball.USERDATA_FIREBALL && fA.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
			fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
		// Fireball hitting a player
		else if (fA.getUserData() == Fireball.USERDATA_FIREBALL && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			for (Player p : players) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setHealth(p.getPlChar().getHealth() - Fireball.DAMAGE);
					break;
				}
			}
			fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		} else if (fB.getUserData() == Fireball.USERDATA_FIREBALL && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			for (Player p : players) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setHealth(p.getPlChar().getHealth() - Fireball.DAMAGE);
					break;
				}
			}
			fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
	}
	
}
