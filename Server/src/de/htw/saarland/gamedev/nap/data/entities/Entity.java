package de.htw.saarland.gamedev.nap.data.entities;

import java.io.ObjectInputStream.GetField;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class Entity {
	
	//Exceptions
	private static final String EXCEPTION_ILLEGAL_DISTANCE_TRAVELED = "The distanceTraveled value can't be smaller than 0";
	private static final String EXCEPTION_ILLEGAL_TIME_LIVING = "The timeLiving value can't be smaller than 0";
	private static final String EXCEPTION_NULL_BODY = "Body object is null!";
	private static final String EXCEPTION_NULL_FIXTURE = "Fixture object is null!";
	private static final String EXCEPTION_NULL_SHAPE = "Shape object is null!";
	private static final String EXCEPTION_NULL_POSITION = "Position object is null!";

	private FixtureDef fixtureDef;
	private BodyDef bodyDef;
	private Body body;
	private Fixture fixture;
	private int id;
	private float timeLiving;
	private float distanceTraveled;
	private boolean flaggedForDelete;
	private Vector2 positionOriginal;
	
	public Entity(Shape shape, Vector2 position, int id){
		if(shape==null) throw new NullPointerException(EXCEPTION_NULL_SHAPE);
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_POSITION);
		initBodyDef(position);
		initFixtureDef(shape);
		timeLiving=0;
		distanceTraveled=0;
		positionOriginal=new Vector2(position);
	}
	
	public Entity(Shape shape, float x, float y, int id){
		this(shape, new Vector2(x,y), id);
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
	
	public float getDistanceTraveled(){
		return distanceTraveled;
	}
	
	public void setDistanceTraveled(float distanceTraveled){
		if(distanceTraveled<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_DISTANCE_TRAVELED);
		this.distanceTraveled=distanceTraveled;
	}
	
	public void setFixture(Fixture fixture){
		if(fixture==null) throw new NullPointerException(EXCEPTION_NULL_FIXTURE);
		this.fixture=fixture;
		dispose();
	}
	
	public Fixture getFixture(){
		return fixture;
	}
	
	public boolean isFlaggedForDelete(){
		return flaggedForDelete;
	}
	
	public void setFlaggedForDelete(boolean flaggedForDelete){
		this.flaggedForDelete=flaggedForDelete;
	}
	
	public Vector2 getPositionOriginal(){
		return positionOriginal;
	}
	
	public float getTimeLiving(){
		return timeLiving;
	}
	
	public void setTimeLiving(float timeLiving){
		if(timeLiving<0) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TIME_LIVING);
		this.timeLiving=timeLiving;
	}
}
