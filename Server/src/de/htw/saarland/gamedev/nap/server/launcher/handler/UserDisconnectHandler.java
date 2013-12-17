package de.htw.saarland.gamedev.nap.server.launcher.handler;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import de.htw.saarland.gamedev.nap.server.ServerExtension;
import de.htw.saarland.gamedev.nap.server.launcher.Launcher;

public class UserDisconnectHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent ev) throws SFSException {
		Launcher launcher = ((ServerExtension)getParentExtension()).getLauncher();
		
		launcher.removePlayer((User)ev.getParameter(SFSEventParam.USER));
	}

}
