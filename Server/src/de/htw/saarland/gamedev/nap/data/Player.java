package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.physics.box2d.Body;
import com.smartfoxserver.v2.entities.User;

public class Player {
	
	//exceptions
	private final static String EXCEPTION_ILLEGAL_TEAM_ID = "Team is not existing!";
	private static final String EXCEPTION_NULL_CHARACTER = "PlayableCharacter object is missing!";
	private static final String EXCEPTION_NULL_USER = "User object is missing!";
	//team constants
	private static final int ID_TEAM_BLUE = 0;
	private static final int ID_TEAM_RED = 1;
	
	private PlayableCharacter plChar;
	private User user;
	private int team;
	
	public Player(User user, PlayableCharacter plChar, int team){
		//if(user==null) throw new NullPointerException(EXCEPTION_NULL_USER);
		if(plChar==null) throw new NullPointerException(EXCEPTION_NULL_CHARACTER);
		if(team!=ID_TEAM_BLUE && team!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAM_ID);
		
		this.user=user;
		this.plChar=plChar;
		this.team=team;
	}
	
	//getter / setter
	public PlayableCharacter getPlChar() {
		return plChar;
	}

	public User getUser() {
		return user;
	}
	
	public int getTeam(){
		return team;
	}
}
