package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Sensor Entities have no collision but they are able to check for other Entities that
 * touch / interfere with them. They can be used for detecting Entities in a radius for example.
 * 
 * @author Stefan
 *
 */

public class SensorEntity extends Entity {
		
	//TODO static or dynamic?
	public SensorEntity(Shape shape, Vector2 position, int id) {
		super(shape, position, id);
		getFixtureDef().isSensor=true;
	}
	
	public SensorEntity(Shape shape, float x, float y, int id){
		this(shape, new Vector2(x,y), id);
	}

}
