package de.htw.saarland.gamedev.nap.data.network;

public class GameOpcodes {
	/**
	 * Opcode to send once the game is initialized to tell the server that the logic can start
	 */
	public static final String GAME_INITIALIZED = "game.init";
	
	/**
	 * Opcode to request that the player should move left
	 */
	public static final String GAME_MOVE_LEFT_REQUEST = "game.player.move.left";
	/**
	 * Opcode to request that the player should move left
	 */
	public static final String GAME_MOVE_STOP_REQUEST = "game.player.move.stop";
	/**
	 * Opcode to request that the player should move left
	 */
	public static final String GAME_MOVE_RIGHT_REQUEST = "game.player.move.right";
	/**
	 * Opcode to request that the player should move left
	 */
	public static final String GAME_MOVE_JUMP_REQUEST = "game.player.move.jump";
}
