package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Static Entities represent things like walls or the ground
 * 
 * @author Stefan
 *
 */
public class StaticEntity extends Entity {

	public StaticEntity(Shape shape, float friction, Vector2 position, int id) {
		super(shape, position, id);		
		getBodyDef().type=BodyDef.BodyType.StaticBody;
		getFixtureDef().friction=friction;
	}
	
	public StaticEntity(Shape shape, float friction, float x, float y, int id){
		this(shape, friction, new Vector2(x,y), id);
	}
}
