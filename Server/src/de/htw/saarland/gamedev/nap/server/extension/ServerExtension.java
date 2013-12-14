package de.htw.saarland.gamedev.nap.server.extension;

import java.util.ArrayList;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

import de.htw.saarland.gamedev.nap.server.extension.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.extension.launcher.LauncherOpcodes;
import de.htw.saarland.gamedev.nap.server.extension.launcher.LauncherPlayer;
import de.htw.saarland.gamedev.nap.server.handler.ChangeCharacterRequestHandler;
import de.htw.saarland.gamedev.nap.server.handler.ChangeTeamRequestHandler;
import de.htw.saarland.gamedev.nap.server.handler.RoomJoinServerHandler;
import de.htw.saarland.gamedev.nap.server.handler.RoomLeaveServerHandler;

public class ServerExtension extends SFSExtension {

	private Launcher launcher;
	
	@Override
	public void init() {
		launcher = new Launcher(this, getParentRoom().getCapacity() / 2);
		
		addEventHandler(SFSEventType.USER_JOIN_ROOM, RoomJoinServerHandler.class);
		addEventHandler(SFSEventType.USER_LEAVE_ROOM, RoomLeaveServerHandler.class);
		
		addRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_REQUEST, ChangeCharacterRequestHandler.class);
		addRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_TEAM_REQUEST, ChangeTeamRequestHandler.class);
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	public void startGame() {
		//clean up and start game logic
		removeEventHandler(SFSEventType.USER_JOIN_ROOM);
		removeEventHandler(SFSEventType.USER_LEAVE_ROOM);
		
		removeRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_REQUEST);
		removeRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_TEAM_REQUEST);
		
		ArrayList<LauncherPlayer> redTeam = new ArrayList<LauncherPlayer>();
		ArrayList<LauncherPlayer> blueTeam = new ArrayList<LauncherPlayer>();
		launcher.getTeams(redTeam, blueTeam);
		launcher = null;
		
		// TODO start game
		
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
