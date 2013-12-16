package de.htw.saarland.gamedev.nap.server.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import de.htw.saarland.gamedev.nap.server.extension.ServerExtension;
import de.htw.saarland.gamedev.nap.server.extension.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.extension.launcher.LauncherOpcodes;

public class ChangeCharacterRequestHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject ev) {
		Launcher launcher = ((ServerExtension)getParentExtension()).getLauncher();
		
		try {
			launcher.changeCharacterId(user, ev.getInt(LauncherOpcodes.CHARACTER_ID_PARAMETER));
			send(LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_SUCCESS, null, user);
		}
		catch (RuntimeException e) {
			send(LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_ERROR, null, user);
		}
	}

}
