package de.htw.saarland.gamedev.nap.client.entity;

import sfs2x.client.SmartFox;
import sfs2x.client.entities.Room;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;

public class MeClientPlayer extends ClientPlayer {

	private IBaseInput inputProcessor;
	private SmartFox sfClient;
	private Room gameRoom;
	
	public MeClientPlayer(PlayableCharacter character, int team,
			IBaseInput inputProcessor, SmartFox sfClient, Room gameRoom) {
		super(character, team);
		
		if (inputProcessor == null || sfClient == null || gameRoom == null) {
			throw new NullPointerException();
		}
		
		this.inputProcessor = inputProcessor;
		this.sfClient = sfClient;
		this.gameRoom = gameRoom;
	}
	
	public void render(SpriteBatch batch, Vector2 direction) {
		
		super.render(batch);
		
		if (inputProcessor.isLeftDown() && !inputProcessor.wasLeftDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_LEFT_REQUEST, null, gameRoom));
		}
		else if (inputProcessor.isRightDown() && !inputProcessor.wasRightDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_RIGHT_REQUEST, null, gameRoom));
		}
		
		if ((!inputProcessor.isLeftDown() && inputProcessor.wasLeftDown())) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_STOP_LEFT_REQUEST, null, gameRoom));
		}
		else if (!inputProcessor.isRightDown() && inputProcessor.wasRightDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_STOP_RIGHT_REQUEST, null, gameRoom));
		}
		
		if (inputProcessor.isJumpDown() && !inputProcessor.wasJumpDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_JUMP_REQUEST, null, gameRoom));
		}
		else if (!inputProcessor.isJumpDown() && inputProcessor.wasJumpDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_JUMP_STOP_REQUEST, null, gameRoom));
		}
		
		if (inputProcessor.isDownDown() && !inputProcessor.wasDownDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_DOWN_REQUEST, null, gameRoom));
		}
		
		if (!inputProcessor.isDownDown() && inputProcessor.wasDownDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_DOWN_STOP_REQUEST, null, gameRoom));
		}
		
		if (inputProcessor.isCaptureDown() && !inputProcessor.wasCaptureDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_CAPTURE_START_REQUEST, null, gameRoom));
		}
		else if (!inputProcessor.isCaptureDown() && inputProcessor.wasCaptureDown()) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_CAPTURE_STOP_REQUEST, null, gameRoom));
		}
		
		if (inputProcessor.isSkill1Down() && !inputProcessor.wasSkill1Down()) {
			SFSObject params = new SFSObject();
			params.putFloat(GameOpcodes.DIRECTION_X_PARAM, direction.x);
			params.putFloat(GameOpcodes.DIRECTION_Y_PARAM, direction.y);
			
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_SKILL1_START_REQUEST, params, gameRoom));
		}		
		else if (inputProcessor.isSkill2Down() && !inputProcessor.wasSkill2Down()) {
			SFSObject params = new SFSObject();
			params.putFloat(GameOpcodes.DIRECTION_X_PARAM, direction.x);
			params.putFloat(GameOpcodes.DIRECTION_Y_PARAM, direction.y);
			
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_SKILL2_START_REQUEST, params, gameRoom));
		}		
		else if (inputProcessor.isSkill3Down() && !inputProcessor.wasSkill3Down()) {
			SFSObject params = new SFSObject();
			params.putFloat(GameOpcodes.DIRECTION_X_PARAM, direction.x);
			params.putFloat(GameOpcodes.DIRECTION_Y_PARAM, direction.y);
			
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_SKILL3_START_REQUEST, params, gameRoom));
		}
	}

}
