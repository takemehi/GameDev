package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.htw.saarland.gamedev.nap.data.shapes.WarriorShape;
import de.htw.saarland.gamedev.nap.data.skills.Axe;

public class Warrior extends PlayableCharacter{

	public static final float DENSITY = 1;
	public static final float FRICTION = 0;
	public static final float RESTITUTION = 0;
	public static final Vector2 BASEVELOCITY = new Vector2(2,7);
	public static final Vector2 MAXVELOCITY = new Vector2(2,7);
	public static final int MAXHEALTH = 200;
	public static final float TIME_SWING = 0.7f;
	
	public static final String USERDATA_AXE = "axe";
	
	public Warrior(Vector2 position, int teamId,  int id) {
		super(new WarriorShape(), DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, TIME_SWING, PlayableCharacter.ID_WARRIOR, teamId, id);
		
		attack1=new Axe();
	}
}
