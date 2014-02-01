package de.htw.saarland.gamedev.nap.data;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;
import de.htw.saarland.gamedev.nap.data.shapes.MageShape;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;
import de.htw.saarland.gamedev.nap.data.skills.Nova;
import de.htw.saarland.gamedev.nap.data.skills.Pyroblast;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class Mage extends PlayableCharacter{

	private static final String META_FILE_PATH_SERVER = GameServer.FOLDER_DATA_SERVER + "meta/characters/mage/mage.txt";
	private static final String META_FILE_PATH_CLIENT = "data/meta/characters/mage/mage.txt";
	
	private static final float DENSITY = 1f;
	private static final float FRICTION = 0f;
	private static final float RESTITUTION = 0;
	private static final Vector2 BASEVELOCITY;
	private static final Vector2 MAXVELOCITY;
	private static final int MAXHEALTH;
	
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
	
	public Mage(World world, Vector2 position, int teamId, int id, float timeToCapturePoint) {
		super(world, new MageShape(), DENSITY, FRICTION, RESTITUTION, position, BASEVELOCITY,
				MAXVELOCITY, MAXHEALTH, PlayableCharacter.ID_MAGE, teamId, id, timeToCapturePoint);
		
		attack1 = new Fireball(this, 1);
		attack2 = new Pyroblast(this, 2);
		attack3 = new Nova(this, 3);
	}

}
