package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;

public class NPC extends MoveableEntity{

	public NPC(Shape shape, float weight, float density, float friction,
			float restitution, Vector2 position, Vector2 baseVelocity, Vector2 maxVelocity, int id) {
		super(shape, density, friction, restitution, position, baseVelocity, maxVelocity, id);
		// TODO Auto-generated constructor stub
	}
}
