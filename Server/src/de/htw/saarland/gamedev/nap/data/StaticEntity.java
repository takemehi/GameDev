package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Static Entities represent things like walls or the ground
 * 
 * @author Stefan
 *
 */
public class StaticEntity extends Entity {

	
	//base values
	private Shape shape;
	private float density;
	private Vector2 position;
	
	//output values
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public StaticEntity(Shape shape, float density, Vector2 position) {
		super();
		this.shape = shape;
		this.density = density;
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
		bodyDef.type=BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position);
	}
	
	private void initFixtureDef(){
		fixtureDef=new FixtureDef();
		fixtureDef.density=density;
		fixtureDef.shape=shape;
		fixtureDef.friction=1;
	}
	
	//////////////////////
	//	public methods	//
	//////////////////////
	
	public void dispose(){
		shape.dispose();
	}
	
	//	getter / setter	

	public BodyDef getBodyDef() {
		return bodyDef;
	}

	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}
	
	public Shape getShape() {
		return shape;
	}

}
