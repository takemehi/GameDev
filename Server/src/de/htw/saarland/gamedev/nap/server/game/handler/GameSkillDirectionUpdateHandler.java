package de.htw.saarland.gamedev.nap.server.game.handler;

import com.badlogic.gdx.math.Vector2;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.ServerExtension;

public class GameSkillDirectionUpdateHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		GameServer game = ((ServerExtension)getParentExtension()).getGame();
		
		Player player = game.getPlayerBySFSUser((SFSUser)user);
		
		Vector2 direction = new Vector2(params.getFloat(GameOpcodes.DIRECTION_X_PARAM), params.getFloat(GameOpcodes.DIRECTION_Y_PARAM));
		
		player.getPlChar().getAttack1().setDirection(direction);
		player.getPlChar().getAttack2().setDirection(direction);
		player.getPlChar().getAttack3().setDirection(direction);
	}
	
}
