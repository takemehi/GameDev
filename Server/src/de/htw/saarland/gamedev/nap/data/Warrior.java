package de.htw.saarland.gamedev.nap.data;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;
import de.htw.saarland.gamedev.nap.data.shapes.WarriorShape;
import de.htw.saarland.gamedev.nap.data.skills.Axe;
import de.htw.saarland.gamedev.nap.data.skills.Charge;
import de.htw.saarland.gamedev.nap.data.skills.Snare;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class Warrior extends PlayableCharacter{

	private static final String META_FILE_PATH_SERVER = GameServer.FOLDER_DATA_SERVER + "meta/characters/warrior/warrior.txt";
	private static final String META_FILE_PATH_CLIENT = "data/meta/characters/warrior/warrior.txt";
	
	public static final float DENSITY = 1;
	public static final float FRICTION = 0;
	public static final float RESTITUTION = 0;
	public static final Vector2 BASEVELOCITY;
	public static final Vector2 MAXVELOCITY;
	public static final int MAXHEALTH;
	
	public static final String USERDATA_AXE = "axe";
	
	static {
		try {
			KeyValueFile values = null;
			if ((new File(META_FILE_PATH_SERVER)).exists()) {
				values = new KeyValueFile(META_FILE_PATH_SERVER);
			}
			else {
				values = new KeyValueFile(META_FILE_PATH_CLIENT);
			}
			
			values.load();
			
			float velocityX = values.getValueFloat(KEY_VELOCITY_X);
			float velocityY = values.getValueFloat(KEY_VELOCITY_Y);
			
			BASEVELOCITY = new Vector2(velocityX, velocityY);
			MAXVELOCITY = BASEVELOCITY;
			MAXHEALTH = values.getValueInt(KEY_HEALTH);
			
		} catch (IOException e) {
			throw new RuntimeException(e); //let the program die on error
		}
	}
	
	public Warrior(World world, Vector2 position, int teamId,  int id, float timetoCapturePoint) {
		super(world, new WarriorShape(), DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, PlayableCharacter.ID_WARRIOR, teamId, id, timetoCapturePoint);
		
		attack1=new Axe(this, 1);
		attack2=new Charge(this, 2);
		attack3=new Snare(this, 3);
	}
}
