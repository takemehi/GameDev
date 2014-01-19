package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.ISendPacket;

public class Player implements IPlayer{
	
	//exceptions
	private static final String EXCEPTION_NULL_USER = "User object is missing!";
	
	private static final float MOVEMENT_UPDATE_TRESHOLD = 0.5f;
	
	private PlayableCharacter plChar;
	private SFSUser user;
	
	private boolean sendUpdate;
	private float stateTime;
	private ISendPacket sendPacketListener;
	
	public Player(SFSUser user, World world, Vector2 position,int characterId, int teamId, int id, ISendPacket sendPacketListener){
		//if(user==null) throw new NullPointerException(EXCEPTION_NULL_USER);
		
		this.user=user;
		this.sendPacketListener = sendPacketListener;
		this.stateTime = 0.0f;
		this.sendUpdate = false;
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
		
		plChar.getAttack1().setSendPacketListener(sendPacketListener);
		plChar.getAttack2().setSendPacketListener(sendPacketListener);
		plChar.getAttack3().setSendPacketListener(sendPacketListener);
	}
	
	
	
	//getter / setter
	
	public PlayableCharacter getPlChar() {
		return plChar;
	}

	public SFSUser getUser() {
		return user;
	}
	
	public void update(float deltaTime, Array<CapturePoint> capturePoints) {
		if (plChar.isMoving()) {
			stateTime += deltaTime;
			
			if (!sendUpdate || stateTime > MOVEMENT_UPDATE_TRESHOLD) {
				SFSObject moveParams = new SFSObject();
				moveParams.putInt(GameOpcodes.ENTITY_ID_PARAM, plChar.getId());
				moveParams.putFloat(GameOpcodes.COORD_X_PARAM, plChar.getBody().getPosition().x);
				moveParams.putFloat(GameOpcodes.COORD_Y_PARAM, plChar.getBody().getPosition().y);
				sendPacketListener.sendServerPacketUDP(GameOpcodes.GAME_OBJECT_COORD_UPDATE, moveParams);
				sendUpdate = true;
				stateTime = 0;
			}
		}
		else {
			sendUpdate = false;
		}
		
		plChar.update(deltaTime, capturePoints);
	}

}
