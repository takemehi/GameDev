package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.ISendPacket;

public class Player implements IPlayer, IStatusUpdateListener {
	
	//exceptions
	private static final String EXCEPTION_NULL_USER = "User object is missing!";
	
	private static final float MOVEMENT_UPDATE_TRESHOLD = 1/20f;
	
	private PlayableCharacter plChar;
	private SFSUser user;
	
	private ISendPacket sendPacketListener;
	
	public Player(SFSUser user, World world, Vector2 position,int characterId, int teamId, int id, ISendPacket sendPacketListener){
		//if(user==null) throw new NullPointerException(EXCEPTION_NULL_USER);
		
		this.user=user;
		this.sendPacketListener = sendPacketListener;
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
		
		plChar.setStatusUpdateListener(this);
	}
	
	
	
	//getter / setter
	
	public PlayableCharacter getPlChar() {
		return plChar;
	}

	public SFSUser getUser() {
		return user;
	}
	
	public void update(float deltaTime, Array<CapturePoint> capturePoints) {
		if (plChar.isMoving() || !plChar.isGrounded()) {
			SFSObject moveParams = new SFSObject();
			moveParams.putInt(GameOpcodes.ENTITY_ID_PARAM, plChar.getId());
			moveParams.putFloat(GameOpcodes.COORD_X_PARAM, plChar.getBody().getPosition().x);
			moveParams.putFloat(GameOpcodes.COORD_Y_PARAM, plChar.getBody().getPosition().y);
			sendPacketListener.sendServerPacketUDP(GameOpcodes.GAME_OBJECT_COORD_UPDATE, moveParams);
		}
		
		plChar.update(deltaTime, capturePoints);
	}

	@Override
	public void hpUpdated(int newHp) {
		SFSObject params = new SFSObject();
		params.putInt(GameOpcodes.ENTITY_ID_PARAM, plChar.getId());
		params.putInt(GameOpcodes.HEALTH_PARAM, newHp);
		
		sendPacketListener.sendServerPacket(GameOpcodes.GAME_UPDATE_HEALTH, params);
	}

	@Override
	public void stunUpdated(boolean stunned) {
		SFSObject params = new SFSObject();
		params.putInt(GameOpcodes.ENTITY_ID_PARAM, plChar.getId());
		params.putBool(GameOpcodes.STUN_STATUS_PARAM, stunned);
		
		sendPacketListener.sendServerPacket(GameOpcodes.GAME_UPDATE_STATUS_STUN, params);
	}

	@Override
	public void snareUpdated(boolean snared) {
		SFSObject params = new SFSObject();
		params.putInt(GameOpcodes.ENTITY_ID_PARAM, plChar.getId());
		params.putBool(GameOpcodes.SNARE_STATUS_PARAM, snared);
		
		sendPacketListener.sendServerPacket(GameOpcodes.GAME_UPDATE_STATUS_SNARE, params);
	}

}
