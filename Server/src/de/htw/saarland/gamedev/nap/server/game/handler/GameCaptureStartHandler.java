package de.htw.saarland.gamedev.nap.server.game.handler;

import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public class GameCaptureStartHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject ev) {
		GameServer game = ((ServerExtension)getParentExtension()).getGame();
		
		Player player = game.getPlayerBySFSUser((SFSUser)user);
		player.getPlChar().setCapturing(true);
	}

}
