package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
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
	
	public Nova(PlayableCharacter character){
		super(character, COOLDOWN, CASTTIME, false);
	}

	@Override
	public void start(World world, PlayableCharacter character, Vector2 direction) {		
		CircleShape circle = new CircleShape();
		circle.setRadius(RADIUS);
				
		nova = new SensorEntity(world, circle, character.getBody().getPosition(), -1);
		nova.setBody(world.createBody(nova.getBodyDef()));
		nova.getBody().setType(BodyType.DynamicBody);
		nova.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		nova.setFixture(nova.getBody().createFixture(nova.getFixtureDef()));
		nova.getFixture().setUserData(USERDATA_NOVA);
		
	}

	@Override
	public void doUpdate(World world, PlayableCharacter character, Vector2 mouseCoords) {
		if(isOnCooldown()){
			try{
				nova.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}catch(Exception e){}
		}
	}

	@Override
	public void cleanUp() {
		if(nova!= null && nova.getBody().getUserData()!=null && nova.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) 
				&& !getPlayableCharacter().getWorld().isLocked()){
			getPlayableCharacter().getWorld().destroyBody(nova.getBody());
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
