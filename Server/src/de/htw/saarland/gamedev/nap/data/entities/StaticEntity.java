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

	public StaticEntity(Shape shape, float friction, Vector2 position) {
		super(shape, position);		
		getBodyDef().type=BodyDef.BodyType.StaticBody;
		getFixtureDef().friction=friction;
	}
}
