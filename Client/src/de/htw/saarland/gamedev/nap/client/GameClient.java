package de.htw.saarland.gamedev.nap.client;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.smartfoxserver.v2.exceptions.SFSException;

import de.htw.saarland.gamedev.nap.NetworkConstants;
import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.client.input.KeyboardMouseInputProcessor;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;

/**
 * Main Game loop
 * 
 * @author Pascal
 * 
 */
public class GameClient implements ApplicationListener, IEventListener {

	private SmartFox sfClient;
	private IBaseInput inputProcessor;
	
	private boolean isInitialized;
	private boolean gameStarted;
	
	public GameClient(SmartFox sfClient) {
		if (sfClient == null) {
			throw new NullPointerException();
		}
		
		this.sfClient = sfClient;
		this.isInitialized = false;
		this.gameStarted = false;
		
		sfClient.addEventListener(SFSEvent.PING_PONG, this);
		sfClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
	}
	
	@Override
	public void create() {
		// TODO load keys from own file
		this.inputProcessor = new KeyboardMouseInputProcessor(
				Buttons.LEFT,
				Keys.NUM_1,
				Keys.NUM_2,
				Keys.SPACE,
				Keys.A,
				Keys.D,
				Keys.S,
				Keys.F);
	}

	@Override
	public void resize(int width, int height) {
		// TODO what todo on resizing?
	}

	@Override
	public void render() {
		if (isInitialized && gameStarted) {
			inputProcessor.process();
			
			// TODO remove this just a networking test
			if (inputProcessor.isLeftDown() && !inputProcessor.wasLeftDown()) {
				sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_LEFT_REQUEST, null, sfClient.getLastJoinedRoom()));
			}
			else if (inputProcessor.isRightDown() && !inputProcessor.wasRightDown()) {
				sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_RIGHT_REQUEST, null, sfClient.getLastJoinedRoom()));
			}
			else if ((!inputProcessor.isLeftDown() && inputProcessor.wasLeftDown()) ||
					(!inputProcessor.isRightDown() && inputProcessor.wasRightDown())) {
				sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_STOP_REQUEST, null, sfClient.getLastJoinedRoom()));
			}
			
			if (inputProcessor.isJumpDown()) {
				sfClient.send(new ExtensionRequest(GameOpcodes.GAME_MOVE_JUMP_REQUEST, null, sfClient.getLastJoinedRoom()));
			}
		}
	}
	
	@Override
	public void dispose() {
		sfClient.removeAllEventListeners();
		sfClient.disconnect();
	}

	@Override
	public void dispatch(BaseEvent be) throws SFSException {
		switch (be.getType()) {
			case SFSEvent.PING_PONG:
				break;
			case SFSEvent.EXTENSION_RESPONSE:
				dispatchExtensionResponse(be);
				break;
		}
	}
	
	public void dispatchExtensionResponse(BaseEvent be) {
		String cmd = (String)be.getArguments().get(NetworkConstants.CMD_KEY);
		
		switch (cmd) {
			case GameOpcodes.GAME_START:
				gameStarted = true; //lets go!
				break;
		}
	}

	@Override
	public void pause() {
		// Ignore this method, only for android apps!
	}
	@Override
	public void resume() {
		// Ignore this method, only for android apps!
	}
	
}
