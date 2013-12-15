package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;

public class PlayableCharacter extends MoveableEntity{
	
	//exceptions
	private final static String EXCEPTION_NO_CHARCLASS = "CharacterClass object is missing!";
	
	private CharacterClass charClass;

	public PlayableCharacter(Shape shape, float weight, float density,
			float friction, float restitution, Vector2 position, Vector2 maxVelocity, CharacterClass charClass) {
		super(shape, density, friction, restitution, position, maxVelocity);
		if(charClass == null) throw new NullPointerException(EXCEPTION_NO_CHARCLASS);
		this.charClass=charClass;
	}
}
