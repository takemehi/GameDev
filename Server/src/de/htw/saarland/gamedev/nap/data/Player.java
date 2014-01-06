package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.User;

public class Player implements Disposable {
	
	//exceptions
	private static final String EXCEPTION_NULL_USER = "User object is missing!";	
	
	private PlayableCharacter plChar;
	private SFSUser user;
	
	public Player(SFSUser user, World world, Vector2 position,int characterId, int teamId, int id){
		//if(user==null) throw new NullPointerException(EXCEPTION_NULL_USER);
		
		this.user=user;
		initPlayableCharacter(world, position, characterId, teamId, id);
	}
	
	private void initPlayableCharacter(World world, Vector2 position, int characterId, int teamId, int id){
		switch(characterId){
		case PlayableCharacter.ID_MAGE:
			this.plChar=new Mage(position, teamId, id);
			plChar.setBody(world.createBody(plChar.getBodyDef()));
			plChar.setFixture(plChar.getBody().createFixture(plChar.getFixtureDef()));
			break;
		case PlayableCharacter.ID_WARRIOR:
			this.plChar=new Warrior(position, teamId, id);
			plChar.setBody(world.createBody(plChar.getBodyDef()));
			plChar.setFixture(plChar.getBody().createFixture(plChar.getFixtureDef()));
			break;
		}
	}
	
	
	
	//getter / setter
	
	public PlayableCharacter getPlChar() {
		return plChar;
	}

	public SFSUser getUser() {
		return user;
	}

	@Override
	public void dispose() {
		plChar.dispose();
	}

}
