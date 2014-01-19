package de.htw.saarland.gamedev.nap.server.game.handler;

import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public class GameMoveJumpStopHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject args) {
		GameServer game = ((ServerExtension)getParentExtension()).getGame();
		
		Player player = game.getPlayerBySFSUser((SFSUser)user);
		
		SFSObject params = new SFSObject();
		params.putInt(GameOpcodes.ENTITY_ID_PARAM, player.getPlChar().getId());
		send(GameOpcodes.GAME_MOVE_JUMP_STOP, params, getParentExtension().getParentRoom().getPlayersList());
		
		try {
			Thread.sleep(System.currentTimeMillis() - user.getLastRequestTime());
		} catch (InterruptedException e) {
		}
		
		player.getPlChar().setUp(false);
	}

}
