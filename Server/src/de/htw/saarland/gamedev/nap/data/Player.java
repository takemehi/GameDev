package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;

public class Player {
	
	//exceptions
	private static final String EXCEPTION_NULL_USER = "User object is missing!";
	
	private static final float MOVEMENT_UPDATE_TRESHOLD = 0.300f;
	
	private PlayableCharacter plChar;
	private SFSUser user;
	
	private float stateTime;
	private SFSExtension extension;
	
	public Player(SFSUser user, World world, Vector2 position,int characterId, int teamId, int id, SFSExtension extension){
		//if(user==null) throw new NullPointerException(EXCEPTION_NULL_USER);
		
		this.user=user;
		this.extension = extension;
		this.stateTime = 0.0f;
		initPlayableCharacter(world, position, characterId, teamId, id);
	}
	
	private void initPlayableCharacter(World world, Vector2 position, int characterId, int teamId, int id){
		switch(characterId){
		case PlayableCharacter.ID_MAGE:
			this.plChar=new Mage(world, position, teamId, id);
			plChar.setBody(world.createBody(plChar.getBodyDef()));
			plChar.setFixture(plChar.getBody().createFixture(plChar.getFixtureDef()));
			break;
		case PlayableCharacter.ID_WARRIOR:
			this.plChar=new Warrior(world, position, teamId, id);
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
	
	public void update(float deltaTime, Array<CapturePoint> capturePoints) {
//		if (plChar.isMoving()) {
//			stateTime += deltaTime;
//			
//			if (stateTime > MOVEMENT_UPDATE_TRESHOLD) {
//				SFSObject moveParams = new SFSObject();
//				moveParams.putInt(GameOpcodes.ENTITY_ID_PARAM, plChar.getId());
//				moveParams.putFloat(GameOpcodes.COORD_X_PARAM, plChar.getBody().getPosition().x);
//				moveParams.putFloat(GameOpcodes.COORD_Y_PARAM, plChar.getBody().getPosition().y);
//				extension.send(GameOpcodes.GAME_OBJECT_COORD_UPDATE, moveParams, extension.getParentRoom().getPlayersList());
//				stateTime = 0;
//			}
//		}
		
		plChar.update(deltaTime, capturePoints);
	}

}
