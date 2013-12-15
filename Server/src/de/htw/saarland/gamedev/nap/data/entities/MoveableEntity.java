package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Moveable entities represent things like players or npcs.
 * 
 * @author Stefan
 *
 */
public class MoveableEntity extends Entity{
	
	private static final String EXCEPTION_NULL_VELOCITY = "MaxVelocity object is null!";
	
	private Vector2 maxVelocity;
	
	public MoveableEntity(Shape shape, float density,
			float friction, float restitution, Vector2 position, Vector2 maxVelocity) {
		super(shape, position);
		if(maxVelocity==null) throw new NullPointerException(EXCEPTION_NULL_VELOCITY);
		this.maxVelocity=maxVelocity;
		
		getBodyDef().type=BodyDef.BodyType.DynamicBody;
		getBodyDef().bullet=true;
		getBodyDef().fixedRotation=true;
		getFixtureDef().density=density;
		getFixtureDef().friction=friction;
		getFixtureDef().restitution=restitution;
	}

	public MoveableEntity(Shape shape, float density,
			float friction, float restitution, float xPos, float yPos, float xVel, float yVel){
		this(shape, density, friction, restitution, new Vector2(xPos,yPos), new Vector2(xVel,yVel));
	}
	
	public Vector2 getMaxVelocity(){
		return maxVelocity;
	}
	
}
