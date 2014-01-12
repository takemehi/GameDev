package de.htw.saarland.gamedev.nap.server.game.handler;

import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public class GameSkill3StartHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject args) {
		GameServer game = ((ServerExtension)getParentExtension()).getGame();
		
		PlayableCharacter player = game.getPlayerBySFSUser((SFSUser)user).getPlChar();
		
		player.setAttacking3(true);
		
		SFSObject params = new SFSObject();
		params.putInt(GameOpcodes.ENTITY_ID_PARAM, player.getId());
		
		send(GameOpcodes.GAME_SKILL3_START, params, getParentExtension().getParentRoom().getPlayersList());
	}
}
