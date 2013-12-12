package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public class MoveableEntity extends Entity{

	//base values
	private Shape shape;
	private float weight;
	private float density;
	private float friction;
	private float restitution;
	
	//momentary values
	private Vector2 acceleration;
	private Vector2 velocity;
	private Vector2 position;
	
	//internal values
	FixtureDef fixtureDef;
	BodyDef bodyDef;
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public MoveableEntity(Shape shape, float weight, float density,
			float friction, float restitution, Vector2 position) {
		super();
		this.shape = shape;
		this.weight = weight;
		this.density = density;
		this.friction = friction;
		this.restitution = restitution;
		this.position = position;
		
		initBodyDef();
		initFixtureDef();
	}

	//////////////////////
	//	intern methods	//
	//////////////////////
	
	//initialization methods

	private void initBodyDef(){
		bodyDef=new BodyDef();
		bodyDef.type=BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position);
	}
	
	private void initFixtureDef(){
		fixtureDef = new FixtureDef();
		fixtureDef.density=density;
		fixtureDef.friction=friction;
		fixtureDef.restitution=restitution;
	}
	
	//////////////////////
	//	public methods	//
	//////////////////////
	
	public void dispose(){
		shape.dispose();
	}
	
	//////////////////////
	//	getter / setter	//
	//////////////////////
	
	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}

	public BodyDef getBodyDef() {
		return bodyDef;
	}
}
