package de.htw.saarland.gamedev.nap.server.launcher.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.server.ServerExtension;
import de.htw.saarland.gamedev.nap.server.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.launcher.LauncherOpcodes;

public class ChangeTeamRequestHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject ev) {
		Launcher launcher = ((ServerExtension)getParentExtension()).getLauncher();
		
		try {
			launcher.changeTeam(user);
		}
		catch (RuntimeException e) {
			SFSObject params = new SFSObject();
			params.putUtfString(LauncherOpcodes.ERROR_MESSAGE_PARAMETER, e.getMessage());
			
			send(LauncherOpcodes.LAUNCHER_CHANGE_TEAM_ERROR, params, user);
		}
	}

}
