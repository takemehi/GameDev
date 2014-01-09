package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Snare extends Skill {

	public static final float COOLDOWN = 5f;
	public static final float CASTTIME = 0f;
	public static final int DAMAGE = 15;
	public static final float DURATION_SNARE = 1.5f;
	
	public static final String USERDATA_SNARE = "snare";
	
	private SensorEntity snare;
	
	public Snare() {
		super(COOLDOWN, CASTTIME);
	}

	@Override
	public void cleanUp(World world) {
		if(snare!= null && snare.getBody().getUserData()!=null && snare.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) && !world.isLocked()){
			world.destroyBody(snare.getBody());
			snare=null;
		}
		
	}

	@Override
	protected void start(World world, PlayableCharacter character,
			int currentId, Vector2 mouseCoords) {

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 0.35f, new Vector2(0.4f,0), 0);
		Vector2 position = new Vector2();
		position.y=character.getBody().getPosition().y;
		if(character.getOrientation()==PlayableCharacter.ORIENTATION_LEFT)
			position.x=character.getBody().getPosition().x-0.8f;
		else
			position.x=character.getBody().getPosition().x+0.0f;
		snare = new SensorEntity(world, shape, position, currentId);
		snare.setBody(world.createBody(snare.getBodyDef()));
		snare.getBody().setType(BodyType.DynamicBody);
		snare.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		snare.setFixture(snare.getBody().createFixture(snare.getFixtureDef()));
		snare.getFixture().setUserData(USERDATA_SNARE);
		
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character,
			int currentId, Vector2 mouseCoords) {
		if(isOnCooldown()){
			try{
				snare.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}catch(Exception e){}
		}		
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<Player> players){
		//Snare hitting a player
		if (fA.getUserData() == Snare.USERDATA_SNARE && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			for (Player p : players) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setHealth(p.getPlChar().getHealth() - Snare.DAMAGE);
					p.getPlChar().setSnared(true, Snare.DURATION_SNARE);
					break;
				}
			}
		}
		else if (fB.getUserData() == Snare.USERDATA_SNARE && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			for (Player p : players) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setHealth(p.getPlChar().getHealth() - Snare.DAMAGE);
					p.getPlChar().setSnared(true, Snare.DURATION_SNARE);
					break;
				}
			}
		}
	}

}
