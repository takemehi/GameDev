package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.physics.box2d.Body;
import com.smartfoxserver.v2.entities.User;

public class Player {
	
	//exceptions
	private static final String EXCEPTION_NO_USER = "User object is missing!";
	private static final String EXCEPTION_NO_CHARACTER = "PlayableCharacter object is missing!";
	private static final String EXCEPTION_NO_BODY = "Body object is missing!";
	
	private PlayableCharacter plChar;
	private User user;
	private Body body;
	
	public Player(User user, PlayableCharacter plChar){
		if(user == null) throw new NullPointerException(EXCEPTION_NO_USER);
		if(plChar == null) throw new NullPointerException(EXCEPTION_NO_CHARACTER);
		
		this.user=user;
		this.plChar=plChar;
	}
	
	//getter / setter
	public PlayableCharacter getPlChar() {
		return plChar;
	}

	public User getUser() {
		return user;
	}
	
	public void setBody(Body body){
		if(body == null) throw new NullPointerException(EXCEPTION_NO_BODY);
		this.body=body;
	}
	
	public Body getBody(){
		return body;
	}
}
