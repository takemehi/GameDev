package de.htw.saarland.gamedev.nap.data.skills;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class Pyroblast extends Skill{

	public static final float COOLDOWN = 0.3f;
	public static final float CASTTIME = 1.5f;
	public static final float RADIUS = 1f;
	public static final float TRAVEL_DISTANCE = 2f;
	public static final float FORCE = 1;
	public static final int DAMAGE = 30;
	
	public static final String USERDATA_NOVA = "nova";
	
	private SensorEntity nova;
	private PlayableCharacter character;
	private boolean started;
	
	public Pyroblast() {
		super(COOLDOWN, CASTTIME);
		
	}

	@Override
	public void start(World world, PlayableCharacter character, int currentId,
			Vector2 mouseCoords) {
		
		started=true;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp(World world) {
		// TODO Auto-generated method stub
		
	}
}
