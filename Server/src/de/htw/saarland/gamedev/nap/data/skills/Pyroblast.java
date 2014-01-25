package de.htw.saarland.gamedev.nap.data.skills;

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

public class Pyroblast extends Skill{
	
	public static final float COOLDOWN = 6f;
	public static final float CASTTIME = 1.5f;
	public static final float RADIUS = 0.2f;
	public static final float TRAVEL_DISTANCE = 3.5f;
	public static final float VELOCITY = 5;
	public static final int DAMAGE = 30;
	
	public static final String USERDATA_PYROBLAST = "pyroblast";
	
	private SensorEntity ball;
	private Vector2 velocityBall;
	
	
	public Pyroblast(PlayableCharacter character, int skillNr) {
		super(character, COOLDOWN, CASTTIME, true, skillNr);
		cast=true;
	}

	@Override
	public void start(World world, PlayableCharacter character, Vector2 direction){
		
		CircleShape shape = new CircleShape();
		shape.setRadius(RADIUS);
		Vector2 position = new Vector2();
		position.y=character.getBody().getPosition().y;
		if(character.getOrientation()==PlayableCharacter.ORIENTATION_LEFT)
			position.x=character.getBody().getPosition().x-0.2f;
		else
			position.x=character.getBody().getPosition().x+0.2f;
		ball = new SensorEntity(world, shape, position, -1);
		ball.setBody(world.createBody(ball.getBodyDef()));
		ball.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
		ball.getFixture().setUserData(USERDATA_PYROBLAST);
		ball.getBody().setType(BodyDef.BodyType.DynamicBody);							
		velocityBall=direction.mul(VELOCITY);
		ball.getBody().setGravityScale(0);
		ball.getBody().setLinearVelocity(velocityBall);
		character.setMovementEnabled(true);
		
		if (skillEventListener != null) {
			skillEventListener.skillStarted(this, ball);
		}
		
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords) {
		
		if(isOnCooldown() && !isCasted()) {
			character.setMovementEnabled(false);
		}
		
		if(isOnCooldown() && isCasted()){
			try{
				ball.setDistanceTraveled((new Vector2(ball.getPositionOriginal().x-ball.getBody().getPosition().x
						,ball.getPositionOriginal().y-ball.getBody().getPosition().y)).len());
				if(ball.getDistanceTraveled()>= TRAVEL_DISTANCE) ball.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}catch (Exception e){}
		}
		
	}

	@Override
	public void cleanUp() {
		if(ball!=null && ball.getBody().getUserData()!=null && ball.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) 
				&& !getPlayableCharacter().getWorld().isLocked()){
			getPlayableCharacter().getWorld().destroyBody(ball.getBody());
			
			if (skillEventListener != null) {
				skillEventListener.skillEnd(this, ball);
			}
			
			ball=null;
		}			
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<IPlayer> players, boolean isClient){
		// Pyroblast hitting the world
		if (fA.getUserData() == Pyroblast.USERDATA_PYROBLAST && fB.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
			fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		} else if (fB.getUserData() == Pyroblast.USERDATA_PYROBLAST && fA.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
			fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
		// Pyroblast hitting a player
		else if (fA.getUserData() == Pyroblast.USERDATA_PYROBLAST && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			if(!isClient){
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Pyroblast.DAMAGE);
						break;
					}
				}
			}
			fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		} else if (fB.getUserData() == Pyroblast.USERDATA_PYROBLAST && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			if(!isClient){
				for (IPlayer p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						p.getPlChar().setHealth(p.getPlChar().getHealth() - Pyroblast.DAMAGE);
						break;
					}
				}
			}
			fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
	}
}
