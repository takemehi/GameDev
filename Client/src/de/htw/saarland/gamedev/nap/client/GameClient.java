package de.htw.saarland.gamedev.nap.client;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.smartfoxserver.v2.exceptions.SFSException;

import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.client.input.KeyboardMouseInputProcessor;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;

public class GameClient implements ApplicationListener, IEventListener {

	private SmartFox sfClient;
	private IBaseInput inputProcessor;
	
	public GameClient(SmartFox sfClient) {
		if (sfClient == null) {
			throw new NullPointerException();
		}
		
		this.sfClient = sfClient;
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

		//send the server a packet to indicate that we are done loading stuff
		sfClient.send(new ExtensionRequest(GameOpcodes.GAME_INITIALIZED, null, sfClient.getLastJoinedRoom()));
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
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

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispatch(BaseEvent be) throws SFSException {
		// TODO Auto-generated method stub
		
	}

}
