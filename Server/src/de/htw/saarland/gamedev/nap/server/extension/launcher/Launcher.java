package de.htw.saarland.gamedev.nap.server.extension.launcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.smartfoxserver.v2.entities.User;

import de.htw.saarland.gamedev.nap.server.extension.ServerExtension;
import de.htw.saarland.gamedev.nap.server.extension.launcher.exception.PlayerNotFoundException;
import de.htw.saarland.gamedev.nap.server.extension.launcher.exception.TeamFullException;

/**
 * Thread safe Launcher implementation to handle game creation tasks
 * 
 * @author Pascal
 *
 */
public class Launcher {
	
	private int maxTeamSize;
	private List<LauncherPlayer> redTeamPlayers;
	private List<LauncherPlayer> blueTeamPlayers;
	private LauncherClientSender launcherSender;
	
	public Launcher(ServerExtension extension, int maxTeamSize) {
		if (maxTeamSize <= 0) {
			throw new IllegalArgumentException("Team size has to be greater 0!");
		}
		
		this.maxTeamSize = maxTeamSize;
		redTeamPlayers = Collections.synchronizedList(new ArrayList<LauncherPlayer>());
		blueTeamPlayers = Collections.synchronizedList(new ArrayList<LauncherPlayer>());
		launcherSender = new LauncherClientSender(extension);
	}
	
	public int getRedTeamSize() {
		return redTeamPlayers.size();
	}
	
	public int getBlueTeamSize() {
		return blueTeamPlayers.size();
	}
	
	/**
	 * Add a new player to the red or blue team
	 * 
	 * @param redTeam true to add the player to the red team, false for the blue team
	 * @param sfsUser the SmartFoxServer User object associated with the Player
	 * @param characterId the character Id of the character the player wants to play
	 */
	public void addPlayer(boolean redTeam, User sfsUser, int characterId) {
		if (redTeam && redTeamPlayers.size() == maxTeamSize || !redTeam && blueTeamPlayers.size() == maxTeamSize) {
			throw new TeamFullException(redTeam ? "Red" : "Blue");
		}
		
		if (redTeam) {
			redTeamPlayers.add(new LauncherPlayer(characterId, sfsUser));
		}
		else {
			blueTeamPlayers.add(new LauncherPlayer(characterId, sfsUser));
		}
		
		ArrayList<LauncherPlayer> red = new ArrayList<LauncherPlayer>();
		ArrayList<LauncherPlayer> blue = new ArrayList<LauncherPlayer>();
		getTeams(red, blue);
		launcherSender.sendTeamStructureChanged(red, blue);
	}
	
	/**
	 * Remove a player
	 * 
	 * @param sfsUser the User to remove
	 */
	public void removePlayer(User sfsUser) {
		if (!redTeamPlayers.remove(new LauncherPlayer(0, sfsUser))) {
			blueTeamPlayers.remove(new LauncherPlayer(0, sfsUser));
		}
		
		ArrayList<LauncherPlayer> red = new ArrayList<LauncherPlayer>();
		ArrayList<LauncherPlayer> blue = new ArrayList<LauncherPlayer>();
		getTeams(red, blue);
		launcherSender.sendTeamStructureChanged(red, blue);
	}
	
	/**
	 * Change the character of an user
	 * 
	 * @param sfsUser the user who wants to change the character
	 * @param characterId the id of the new character
	 */
	public void changeCharacterId(User sfsUser, int characterId) {
		boolean found = false;
		
		synchronized (redTeamPlayers) {
			for (LauncherPlayer lp: redTeamPlayers) {
				if (lp.getSfsUser().equals(sfsUser)) {
					lp.setCharacterId(characterId);
					found = true;
					break;
				}
			}
		}
		
		if (!found) {
			synchronized (blueTeamPlayers) {
				for (LauncherPlayer lp: blueTeamPlayers) {
					if (lp.getSfsUser().equals(sfsUser)) {
						lp.setCharacterId(characterId);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Changes the Team of the user blue<->red depending on current team
	 * 
	 * @param sfsUser the User who wants to change the team
	 */
	public void changeTeam(User sfsUser) {
		boolean found = false;
		LauncherPlayer player = null;
		
		synchronized (redTeamPlayers) {
			for (LauncherPlayer lp: redTeamPlayers) {
				if (lp.getSfsUser().equals(sfsUser)) {
					player = lp;
					found = true;
					break;
				}
			}
		}
		
		if (found) {
			if (blueTeamPlayers.size() == maxTeamSize) {
				throw new TeamFullException("blue");
			}
			
			redTeamPlayers.remove(player);
			blueTeamPlayers.add(player);
		}
		else {
			synchronized (blueTeamPlayers) {
				for (LauncherPlayer lp: blueTeamPlayers) {
					if (lp.getSfsUser().equals(sfsUser)) {
						player = lp;
						break;
					}
				}
			}
			
			if (player == null) {
				throw new PlayerNotFoundException(sfsUser.toString());
			}
			else if (redTeamPlayers.size() == maxTeamSize) {
				throw new TeamFullException("red");
			}
			
			blueTeamPlayers.remove(player);
			redTeamPlayers.add(player);
		}
		
		ArrayList<LauncherPlayer> red = new ArrayList<LauncherPlayer>();
		ArrayList<LauncherPlayer> blue = new ArrayList<LauncherPlayer>();
		getTeams(red, blue);
		launcherSender.sendTeamStructureChanged(red, blue);
	}
	
	/**
	 * Copies the red an blue team into the argument collections
	 * 
	 * @param redTeam A collection to copy the red Teams players into
	 * @param blueTeam A collection to copy the blue Teams players into
	 */
	public void getTeams(Collection<LauncherPlayer> redTeam, Collection<LauncherPlayer> blueTeam) {
		if (redTeam == null || blueTeam == null) {
			throw new NullPointerException();
		}
		
		redTeam.clear();
		blueTeam.clear();
		
		synchronized (redTeamPlayers) {
			for (LauncherPlayer lp: redTeamPlayers) {
				redTeam.add(lp);
			}
		}
		
		synchronized (blueTeamPlayers) {
			for (LauncherPlayer lp: blueTeamPlayers) {
				blueTeam.add(lp);
			}
		}
	}
}
