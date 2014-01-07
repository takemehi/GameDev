package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;

import de.htw.saarland.gamedev.nap.data.skills.Skill;

public abstract class PlayableCharacter extends GameCharacter{
	
	//Exceptions
	private static final String EXCEPTION_ILLEGAL_ID_CHARACTERCLASS = "This character class doesn't exist!";
	private static final String EXCEPTION_ILLEGAL_ID_TEAM = "This teamId doesn't exist!";
	//Userdata
	public final static String USERDATA_PLAYER = "player";
	//Class ids
	public final static int ID_WARRIOR = 0;
	public final static int ID_MAGE = 1;
	//Team constants
	public static final int ID_TEAM_BLUE = 0;
	public static final int ID_TEAM_RED = 1;
	public static final int GROUP_TEAM_BLUE = -1;
	public static final int GROUP_TEAM_RED = -2;
	//other
	//TODO change that value
	public final static float MIN_TIME_ON_GROUND = 0.1f;
	
	private int characterClass;
	private boolean atSpawn;
	private int teamId;
	
	public PlayableCharacter(Shape shape, float density, float friction,
			float restitution, Vector2 position, Vector2 baseVelocity,
			Vector2 maxVelocity, int maxHealth, int characterClass, int teamId, int id) {
		super(shape, density, friction, restitution, position, baseVelocity,
				maxVelocity, maxHealth, id);
		
		if(teamId!=ID_TEAM_BLUE && teamId!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ID_TEAM);
		if(characterClass!=ID_MAGE && characterClass!=ID_WARRIOR) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ID_CHARACTERCLASS);
		
		this.characterClass=characterClass;
		this.teamId=teamId;
		if(teamId==ID_TEAM_BLUE)
			this.getFixtureDef().filter.groupIndex=PlayableCharacter.GROUP_TEAM_BLUE;
		else
			this.getFixtureDef().filter.groupIndex=PlayableCharacter.GROUP_TEAM_RED;
	}
	

	@Override
	public void setFixture(Fixture fixture) {
		super.setFixture(fixture);
		getFixture().setUserData(USERDATA_PLAYER);
	}
	
	//getter / setter
	
	public boolean isAtSpawn(){
		return atSpawn;
	}

	public void setAtSpawn(boolean atSpawn){
		this.atSpawn=atSpawn;
	}
	
	public int getCharacterClass(){
		return characterClass;
	}
	
	public int getTeamId(){
		return teamId;
	}
}
