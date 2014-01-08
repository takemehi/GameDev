package de.htw.saarland.gamedev.nap.server.game.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public class GetMoveableEntitiesHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject args) {
		GameServer game = ((ServerExtension)getParentExtension()).getGame();
		
		for (Player player : game.getBlueTeam().getMembers()) {
			if (!player.getUser().equals(user)) {
				spawnPlayer(player, user);
			}
		}
		
		for (Player player : game.getRedTeam().getMembers()) {
			if (!player.getUser().equals(user)) {
				spawnPlayer(player, user);
			}
		}
		
		send(GameOpcodes.GAME_END_OBJECTS, null, user);
	}
	
	private void spawnPlayer(Player player, User user) {
		SFSObject spawnPlayer = new SFSObject();
		spawnPlayer.putInt(GameOpcodes.ENTITY_ID_PARAM, player.getPlChar().getId());
		spawnPlayer.putInt(GameOpcodes.CHARACTER_ID_PARAM, player.getPlChar().getCharacterClass());
		spawnPlayer.putInt(GameOpcodes.TEAM_ID_PARAM, player.getPlChar().getTeamId());
		spawnPlayer.putFloat(GameOpcodes.COORD_X_PARAM, player.getPlChar().getBody().getPosition().x);
		spawnPlayer.putFloat(GameOpcodes.COORD_Y_PARAM, player.getPlChar().getBody().getPosition().y);
		send(GameOpcodes.GAME_SPAWN_PLAYER, spawnPlayer, user);
	}

}
