package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class PlayableCharacter extends GameCharacter{
	
	//Exceptions
	private static final String EXCEPTION_ILLEGAL_ID_CHARACTERCLASS = "This character class doesn't exist!";
	//Userdata
	public final static String USERDATA_PLAYER = "player";
	//Class ids
	public final static int ID_WARRIOR = 0;
	public final static int ID_MAGE = 1;
	
	private int characterClass;
	
	public PlayableCharacter(Shape shape, float density, float friction,
			float restitution, Vector2 position, Vector2 baseVelocity,
			Vector2 maxVelocity, int maxHealth, float maxSwingTime, int characterClass, int id) {
		super(shape, density, friction, restitution, position, baseVelocity,
				maxVelocity, maxHealth, maxSwingTime, id);

		if(characterClass!=ID_MAGE && characterClass!=ID_WARRIOR) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ID_CHARACTERCLASS);
		this.characterClass=characterClass;
	}

	@Override
	public void setFixture(Fixture fixture) {
		super.setFixture(fixture);
		getFixture().setUserData(USERDATA_PLAYER);
	}
	
	//getter / setter
	
	public int getCharacterClass(){
		return characterClass;
	}
}
