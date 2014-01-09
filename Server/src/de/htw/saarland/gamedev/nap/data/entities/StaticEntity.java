package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Static Entities represent things like walls or the ground
 * 
 * @author Stefan
 *
 */
public class StaticEntity extends Entity {

	public StaticEntity(World world, Shape shape, float friction, Vector2 position, int id) {
		super(world, shape, position, id);		
		getBodyDef().type=BodyDef.BodyType.StaticBody;
		getFixtureDef().friction=friction;
	}
	
	public StaticEntity(World world, Shape shape, float friction, float x, float y, int id){
		this(world, shape, friction, new Vector2(x,y), id);
	}
}
