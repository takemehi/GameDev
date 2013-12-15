package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;

public class NPC extends MoveableEntity{

	public NPC(Shape shape, float weight, float density, float friction,
			float restitution, Vector2 position, Vector2 maxVelocity) {
		super(shape, density, friction, restitution, position, maxVelocity);
		// TODO Auto-generated constructor stub
	}
}
