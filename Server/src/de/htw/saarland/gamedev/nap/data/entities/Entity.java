package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class Entity {
	
	private static final String EXCEPTION_NULL_BODY = "Body object is null!";
	private static final String EXCEPTION_NULL_FIXTURE = "Fixture object is null!";
	private static final String EXCEPTION_NULL_SHAPE = "Shape object is null!";
	private static final String EXCEPTION_NULL_POSITION = "Position object is null!";

	private FixtureDef fixtureDef;
	private BodyDef bodyDef;
	private Body body;
	private Fixture fixture;
	
	public Entity(Shape shape, Vector2 position){
		if(shape==null) throw new NullPointerException(EXCEPTION_NULL_SHAPE);
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_POSITION);
		initBodyDef(position);
		initFixtureDef(shape);
	}
	
	public Entity(Shape shape, float x, float y){
		this(shape, new Vector2(x,y));
	}
	
	private void initBodyDef(Vector2 position){
		bodyDef=new BodyDef();
		bodyDef.position.set(position);
	}
	
	private void initFixtureDef(Shape shape){
		fixtureDef=new FixtureDef();
		fixtureDef.shape=shape;
	}
	
	public void dispose(){
		fixtureDef.shape.dispose();
	}
	
	//getter / setter
	
	public BodyDef getBodyDef() {
		return bodyDef;
	}

	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}
	
	public void setBody(Body body){
		if(body==null) throw new NullPointerException(EXCEPTION_NULL_BODY);
		this.body=body;
	}
	
	public Body getBody(){
		return body;
	}
	
	public void setFixture(Fixture fixture){
		if(fixture==null) throw new NullPointerException(EXCEPTION_NULL_FIXTURE);
		this.fixture=fixture;
	}
	
	public Fixture getFixture(){
		return fixture;
	}
}
