package de.htw.saarland.gamedev.nap.client.entity;

import sfs2x.client.SmartFox;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.client.render.PlayerAnimation;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;

public class ClientPlayer implements IRender {

	private PlayableCharacter character;
	private int team;
	private IBaseInput inputProcessor;
	
	private PlayerAnimation animations;
	private float stateTime;
	private SmartFox sfClient;
	
	public ClientPlayer(PlayableCharacter character, int team, IBaseInput inputProcessor, SmartFox sfClient) {
		if (character == null || inputProcessor == null || sfClient == null) {
			throw new NullPointerException();
		}
		
		this.character = character;
		this.team = team;
		this.inputProcessor = inputProcessor;
		this.sfClient = sfClient;
		
		//init Player Animation based on character
	}

	@Override
	public void render(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		//TODO calculate x & y pos to render
		//batch.draw(animations.getAnimationFrame(getCharacterState(), stateTime), x, y);
		
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
		
		// TODO capture request
	}

}
