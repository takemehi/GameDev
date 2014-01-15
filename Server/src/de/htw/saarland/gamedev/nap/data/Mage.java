package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.shapes.MageShape;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;
import de.htw.saarland.gamedev.nap.data.skills.Nova;
import de.htw.saarland.gamedev.nap.data.skills.Pyroblast;

public class Mage extends PlayableCharacter{

	private static final float DENSITY = 1f;
	private static final float FRICTION = 0f;
	private static final float RESTITUTION = 0;
	private static final Vector2 BASEVELOCITY = new Vector2(3,8);
	private static final Vector2 MAXVELOCITY = new Vector2(3,8);
	private static final int MAXHEALTH = 100;
	public static final float VELOCITY_FIREBALL = 1f;
	
	public Mage(World world, Vector2 position, int teamId, int id) {
		super(world, new MageShape(), DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, PlayableCharacter.ID_MAGE, teamId, id);
		
		attack1 = new Fireball(this, 1);
		attack2 = new Pyroblast(this, 2);
		attack3 = new Nova(this, 3);
	}

}
