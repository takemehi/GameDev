package de.htw.saarland.gamedev.nap.server.game.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public class ClientInitializedHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject args) {
		ServerExtension extension = (ServerExtension)getParentExtension();
		
		extension.clientInitialized();
		if (extension.isClientInitDone()) {
			send(GameOpcodes.GAME_START, null, extension.getParentRoom().getPlayersList());
			extension.getGame().startGame();
		}
	}

}
