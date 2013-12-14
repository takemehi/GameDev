package de.htw.saarland.gamedev.nap.server.handler;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import de.htw.saarland.gamedev.nap.server.extension.ServerExtension;
import de.htw.saarland.gamedev.nap.server.extension.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.extension.launcher.exception.TeamFullException;

public class RoomJoinServerHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent ev) throws SFSException {
		Launcher launcher = ((ServerExtension)getParentExtension()).getLauncher();
		
		try {
			launcher.addPlayer(launcher.getRedTeamSize() < 5, (User)ev.getParameter(SFSEventParam.USER), 0);
		}
		catch (TeamFullException e) {
			throw new SFSException(e);
		}
	}

}
