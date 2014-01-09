package de.htw.saarland.gamedev.nap.client.entity;

import sfs2x.client.SmartFox;
import sfs2x.client.entities.Room;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
	
	@Override
	public void render(SpriteBatch batch) {
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
		
		// TODO skills
	}

}
