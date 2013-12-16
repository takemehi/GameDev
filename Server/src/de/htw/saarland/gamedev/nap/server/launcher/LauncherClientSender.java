package de.htw.saarland.gamedev.nap.server.extension.launcher;

import java.util.ArrayList;
import java.util.List;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.server.extension.ServerExtension;

public class LauncherClientSender {
	private ServerExtension extension;
	
	public LauncherClientSender(ServerExtension extension) {
		if (extension == null) {
			throw new NullPointerException();
		}
		
		this.extension = extension;
	}
	
	public void sendTeamStructureChanged(List<LauncherPlayer> redTeam, List<LauncherPlayer> blueTeam) {
		List<User> allUsers = new ArrayList<User>();
		
		SFSObject params = new SFSObject();
		SFSArray sfsRedTeam = new SFSArray();
		
		for (LauncherPlayer lp: redTeam) {
			sfsRedTeam.addUtfString(lp.getSfsUser().getName());
			allUsers.add(lp.getSfsUser());
		}
		
		SFSArray sfsBlueTeam = new SFSArray();
		
		for (LauncherPlayer lp: blueTeam) {
			sfsBlueTeam.addUtfString(lp.getSfsUser().getName());
			allUsers.add(lp.getSfsUser());
		}
		
		params.putSFSArray(LauncherOpcodes.RED_TEAM_STRUCTURE_ARRAY_PARAMETER, sfsRedTeam);
		params.putSFSArray(LauncherOpcodes.BLUE_TEAM_STRUCTURE_ARRAY_PARAMETER, sfsBlueTeam);
		
		extension.send(LauncherOpcodes.LAUNCHER_TEAMS_CHANGED, params, allUsers);
	}
}
