package de.htw.saarland.gamedev.nap.server.extension.launcher;

public class LauncherOpcodes {
	
	/**
	 * Opcode to request a character change
	 * 
	 * params:
	 *  CHARACTER_ID_PARAMETER the new character id
	 * 
	 * returns:
	 * 	LAUNCHER_CHANGE_CHARACTER_SUCCESS on success
	 * 	LAUNCHER_CHANGE_CHARACTER_ERROR on failure
	 */
	public static final String LAUNCHER_CHANGE_CHARACTER_REQUEST = "launcher.changechar";
	public static final String LAUNCHER_CHANGE_CHARACTER_SUCCESS = "launcher.changechar.success";
	public static final String LAUNCHER_CHANGE_CHARACTER_ERROR = "launcher.changechar.error";
	
	/**
	 * Opcode to request a team change
	 * 
	 * returns:
	 * 	LAUNCHER_TEAMS_CHANGED on success
	 * 	LAUNCHER_CHANGE_TEAM_ERROR on error
	 */
	public static final String LAUNCHER_CHANGE_TEAM_REQUEST = "launcher.changeteam";
	/**
	 * Opcode that indicates that a team structure changed
	 * 
	 * params:
	 * 	BLUE_TEAM_STRUCTURE_ARRAY_PARAMETER array containing user names of the blue team members
	 * 	RED_TEAM_STRUCTURE_ARRAY_PARAMETER array containing user names of the red team members
	 */
	public static final String LAUNCHER_TEAMS_CHANGED = "launcher.teamschanged";
	/**
	 * Opcode that indicates that a change team request failed
	 * 
	 * params:
	 * 	ERROR_MESSAGE_PARAMETER the error message
	 */
	public static final String LAUNCHER_CHANGE_TEAM_ERROR = "launcher.changeteam.error";
	
	/**
	 * Opcode to change the ready state of a player
	 * 
	 * returns:
	 * 	LAUNCHER_CHANGE_READY_SUCCESS
	 */
	public static final String LAUNCHER_CHANGE_READY_REQUEST = "launcher.changeready";
	/**
	 * Opcode that indicates that a change ready state request succeeded, what could go wrong Oo?
	 * 
	 * params:
	 * 	READY_STATE_PARAMETER
	 */
	public static final String LAUNCHER_CHANGE_READY_SUCCESS = "launcher.changeready.success";
	
	/**
	 * Opcode that indicates that every player is ready and the game is about to start, each player
	 * should send GAME_INITIALIZED packet to the server to indicate that they are ready to play
	 */
	public static final String LAUNCHER_GAME_STARTS = "launcher.gamestarts";
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//	Params																				//
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Param that contains a characterid
	 */
	public static final String CHARACTER_ID_PARAMETER = "characterid";
	
	public static final String RED_TEAM_STRUCTURE_ARRAY_PARAMETER = "redteamstruct";
	public static final String BLUE_TEAM_STRUCTURE_ARRAY_PARAMETER = "blueteamstruct";
	
	public static final String ERROR_MESSAGE_PARAMETER = "errormessage";
	
	public static final String READY_STATE_PARAMETER = "ready";
}
