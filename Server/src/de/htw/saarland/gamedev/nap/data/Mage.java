package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.htw.saarland.gamedev.nap.data.shapes.MageShape;

public class Mage extends PlayableCharacter{

	private static final PolygonShape SHAPE = new MageShape();
	private static final float DENSITY = 0.8f;
	private static final float FRICTION = 0;
	private static final float RESTITUTION = 0;
	private static final Vector2 BASEVELOCITY = new Vector2(3,8);
	private static final Vector2 MAXVELOCITY = new Vector2(3,8);
	private static final int MAXHEALTH = 100;
	public static final float TIME_SWING = 0.4f;
	
	public Mage(Vector2 position, int id) {
		super(SHAPE, DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, TIME_SWING, PlayableCharacter.ID_MAGE, id);
	}

}
