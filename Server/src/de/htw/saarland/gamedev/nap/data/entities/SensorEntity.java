package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Sensor Entities have no collision but they are able to check for other Entities that
 * touch / interfere with them. They can be used for detecting Entities in a radius for example.
 * 
 * @author Stefan
 *
 */

public class SensorEntity extends Entity {
		
	//TODO static or dynamic?
	public SensorEntity(World world, Shape shape, Vector2 position, int id) {
		super(world, shape, position, id);
		getFixtureDef().isSensor=true;
	}
	
	public SensorEntity(World world, Shape shape, float x, float y, int id){
		this(world, shape, new Vector2(x,y), id);
	}

}
