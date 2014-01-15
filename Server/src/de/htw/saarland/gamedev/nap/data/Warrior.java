package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.shapes.WarriorShape;
import de.htw.saarland.gamedev.nap.data.skills.Axe;
import de.htw.saarland.gamedev.nap.data.skills.Charge;
import de.htw.saarland.gamedev.nap.data.skills.Snare;

public class Warrior extends PlayableCharacter{

	public static final float DENSITY = 1;
	public static final float FRICTION = 0;
	public static final float RESTITUTION = 0;
	public static final Vector2 BASEVELOCITY = new Vector2(2,7);
	public static final Vector2 MAXVELOCITY = new Vector2(2,7);
	public static final int MAXHEALTH = 200;
	public static final float TIME_SWING = 0.7f;
	
	public static final String USERDATA_AXE = "axe";
	
	public Warrior(World world, Vector2 position, int teamId,  int id) {
		super(world, new WarriorShape(), DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, PlayableCharacter.ID_WARRIOR, teamId, id);
		
		attack1=new Axe(this, 1);
		attack2=new Charge(this, 2);
		attack3=new Snare(this, 3);
	}
}
