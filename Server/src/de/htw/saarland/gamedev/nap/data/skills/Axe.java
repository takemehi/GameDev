package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.Warrior;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Axe extends Skill{
	
	public static final float COOLDOWN = 0.7f;
	public static final float CASTTIME = 0f;
	public static final int DAMAGE = 10;
	
	public static final String USERDATA_AXE = "axe";

	private SensorEntity axe;
	
	public Axe() {
		super(COOLDOWN, CASTTIME);
		
	}

	@Override
	protected void start(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 0.35f, new Vector2(0.4f,0), 0);
		Vector2 position = new Vector2();
		position.y=character.getBody().getPosition().y;
		if(character.getOrientation()==PlayableCharacter.ORIENTATION_LEFT)
			position.x=character.getBody().getPosition().x-0.8f;
		else
			position.x=character.getBody().getPosition().x+0.0f;
		axe = new SensorEntity(world, shape, position, currentId);
		axe.setBody(world.createBody(axe.getBodyDef()));
		axe.getBody().setType(BodyType.DynamicBody);
		axe.getFixtureDef().filter.groupIndex=character.getFixture().getFilterData().groupIndex;
		axe.setFixture(axe.getBody().createFixture(axe.getFixtureDef()));
		axe.getFixture().setUserData(USERDATA_AXE);
		
	}

	@Override
	protected void doUpdate(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {
		if(isOnCooldown()){
			try{
				axe.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}catch(Exception e){}
		}
	}

	@Override
	public void cleanUp(World world) {
		if(axe!= null && axe.getBody().getUserData()!=null && axe.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE) && !world.isLocked()){
			world.destroyBody(axe.getBody());
			axe=null;
		}
	}
	
	public static void handleContact(Fixture fA, Fixture fB, Array<Player> players){
		//Axe hitting a player
		if ((fA.getUserData() == Axe.USERDATA_AXE && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER)) {
			for (Player p : players) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setHealth(p.getPlChar().getHealth() - Axe.DAMAGE);
					break;
				}
			}
		} 
		else if (fB.getUserData() == Axe.USERDATA_AXE && fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			for (Player p : players) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setHealth(p.getPlChar().getHealth() - Axe.DAMAGE);
					break;
				}
			}
		}
	}

}
