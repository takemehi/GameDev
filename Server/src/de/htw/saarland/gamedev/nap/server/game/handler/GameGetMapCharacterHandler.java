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

public class GameGetMapCharacterHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject args) {
		GameServer game = ((ServerExtension)getParentExtension()).getGame();
		
		SFSObject map = new SFSObject();
		map.putUtfString(GameOpcodes.CURRENT_MAP_PARAM, game.getMapName());
		send(GameOpcodes.GAME_CURRENT_MAP, map, user);
		
		Player player = game.getPlayerBySFSUser((SFSUser)user);
		
		SFSObject character = new SFSObject();
		character.putInt(GameOpcodes.CHARACTER_ID_PARAM, player.getPlChar().getCharacterClass());
		character.putFloat(GameOpcodes.COORD_X_PARAM, player.getPlChar().getBody().getPosition().x);
		character.putFloat(GameOpcodes.COORD_Y_PARAM, player.getPlChar().getBody().getPosition().y);
		character.putInt(GameOpcodes.ENTITY_ID_PARAM, player.getPlChar().getId());
		character.putInt(GameOpcodes.TEAM_ID_PARAM, player.getPlChar().getTeamId());
		send(GameOpcodes.GAME_OWN_CHARACTER, character, user);
	}

}
