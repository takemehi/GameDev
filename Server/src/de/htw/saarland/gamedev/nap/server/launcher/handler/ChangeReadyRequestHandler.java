package de.htw.saarland.gamedev.nap.server.launcher.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.server.ServerExtension;
import de.htw.saarland.gamedev.nap.server.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.launcher.LauncherOpcodes;

public class ChangeReadyRequestHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject ev) {
		Launcher launcher = ((ServerExtension)getParentExtension()).getLauncher();
		
		boolean val = launcher.changeReady(user);
		SFSObject params = new SFSObject();
		params.putBool(LauncherOpcodes.READY_STATE_PARAMETER, val);
		send(LauncherOpcodes.LAUNCHER_CHANGE_READY_SUCCESS, params, user);
	}

}
