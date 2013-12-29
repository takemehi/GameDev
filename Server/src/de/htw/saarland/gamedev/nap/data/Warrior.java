package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.htw.saarland.gamedev.nap.data.shapes.WarriorShape;

public class Warrior extends PlayableCharacter{

	private static final PolygonShape SHAPE = new WarriorShape();
	private static final float DENSITY = 1;
	private static final float FRICTION = 0;
	private static final float RESTITUTION = 0;
	private static final Vector2 BASEVELOCITY = new Vector2(2,7);
	private static final Vector2 MAXVELOCITY = new Vector2(2,7);
	private static final int MAXHEALTH = 200;
	
	private FixtureDef meleeSensorDef;
	private Fixture meleeSensor;
	
	public Warrior(Vector2 position, int id) {
		super(SHAPE, DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, id);
		
		initMeleeSensorDef();
	}
	
	private void initMeleeSensorDef(){
		meleeSensorDef = new FixtureDef();
		meleeSensorDef.isSensor=true;
		
		PolygonShape sensorShape = new PolygonShape();
		sensorShape.setAsBox(0.2f, 0.35f, new Vector2(0.4f,0), 0);
		meleeSensorDef.shape=sensorShape;
	}
	
	public void setMeleeSensorFixture(Fixture fixture){
		this.meleeSensor=fixture;
	}
	
	public Fixture getMeleeSensorFixture(){
		return meleeSensor;
	}
	
	public FixtureDef getMeleeSensorFixtureDef(){
		return meleeSensorDef;
	}
}
