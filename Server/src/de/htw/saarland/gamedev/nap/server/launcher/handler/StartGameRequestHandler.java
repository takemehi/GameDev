package de.htw.saarland.gamedev.nap.server.launcher.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.server.ServerExtension;
import de.htw.saarland.gamedev.nap.server.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.launcher.LauncherOpcodes;

public class StartGameRequestHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject ev) {
		if (getParentExtension().getParentRoom().getOwner().equals(user)) {
			Launcher launcher = ((ServerExtension)getParentExtension()).getLauncher();
			
			if (launcher.isGameReadyToStart()) {
				// TODO start game
			}
			else {
				SFSObject ret = new SFSObject();
				ret.putUtfString(LauncherOpcodes.ERROR_MESSAGE_PARAMETER, "Some players aren't ready yet!");
				
				send(LauncherOpcodes.LAUNCHER_START_GAME_ERROR, ret, user);
			}
		}
		else {
			SFSObject ret = new SFSObject();
			ret.putUtfString(LauncherOpcodes.ERROR_MESSAGE_PARAMETER, "You are not the game owner!");
			
			send(LauncherOpcodes.LAUNCHER_START_GAME_ERROR, ret, user);
		}
	}

}
