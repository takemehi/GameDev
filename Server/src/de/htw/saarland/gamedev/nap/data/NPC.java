package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;

public class NPC extends GameCharacter{
	
	//Exceptions
	private static final String EXCEPTION_ILLEGAL_ID_NPC = "This creep id doesn't exist!";
	
	public final static int ID_CREEP_DMG = 0;
	public final static int ID_CREEP_RES = 1;

	private int npcType;
	
	public NPC(Shape shape, float density,
			float friction, float restitution, Vector2 position, Vector2 baseVelocity, Vector2 maxVelocity, int maxHealth
			, int npcType, int id) {
		super(shape, density, friction, restitution, position, baseVelocity,
				maxVelocity, maxHealth, id);
		
		if(npcType!=ID_CREEP_DMG && npcType!=ID_CREEP_RES) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ID_NPC);
		this.npcType=npcType;
	}
	
	//getter / setter

	public int getNpcType(){
		return npcType;
	}
}
