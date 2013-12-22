package de.htw.saarland.gamedev.nap.data.network;

public class GameOpcodes {
	/**
	 * Opcode to send once the game is initialized to tell the server that the logic can start
	 */
	public static final String GAME_INITIALIZED = "game.init";
	
	/////////////////////////////////////////////////////////////////////////
	// Movement															   //
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * Opcode to request that the player would like to move left
	 * 
	 * returns:
	 * 	GAME_MOVE_LEFT_START when the server accepted the move and the character started to move left
	 * 	GAME_MOVE_STOP when the character stopped moving
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_LEFT_REQUEST = "game.player.move.left";
	/**
	 * Opcode to request that the player would like stop moving
	 * 
	 * returns:
	 * 	GAME_MOVE_STOP when the character stopped moving
	 */
	public static final String GAME_MOVE_STOP_REQUEST = "game.player.move.stop.request";
	/**
	 * Opcode to request that the player would like to move right
	 * 
	 * returns:
	 * 	GAME_MOVE_RIGHT_START when the server accepted the move and the character started to move right
	 * 	GAME_MOVE_STOP when the character stopped moving
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_RIGHT_REQUEST = "game.player.move.right";
	/**
	 * Opcode to request that the player would like to jump
	 * 
	 * returns:
	 * 	GAME_MOVE_JUMP_START when the server accepted the move and the character started to jump
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_JUMP_REQUEST = "game.player.move.jump";
	
	/**
	 * Opcode that indicates that a request to move left succeeded and the character should start moving left
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving left
	 */
	public static final String GAME_MOVE_LEFT_START = "game.player.move.left.start";
	/**
	 * Opcode that indicates that a request to move right succeeded and the character should start moving right
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving right
	 */
	public static final String GAME_MOVE_RIGHT_START = "game.player.move.right.start";
	/**
	 * Opcode that indicates that a request to jump succeeded and the character should start jumping right now
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that starts to jump
	 */
	public static final String GAME_MOVE_JUMP_START = "game.player.move.jump.start";
	/**
	 * Opcode that indicates that the character should stop moving, this can happen
	 * because of a collision with something or because the player stopped to press a move key
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that stopped moving
	 */
	public static final String GAME_MOVE_STOP = "game.player.move.stop";
	
	/**
	 * Opcode that indicates that a game object should update its coordinates to improve accuracy
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity whose position should get updated
	 * 	float COORD_X_PARAM the x coordinate
	 * 	float COORD_Y_PARAM the y coordinate
	 */
	public static final String GAME_OBJECT_COORD_UPDATE = "game.player.position.update";
	
	///////////////////////////////////////////////////////////////////////////////
	// Player actions															 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Opcode that is sent as soon as a player wants to capture a capture point
	 */
	public static final String GAME_CAPTURE_START_REQUEST = "game.player.capture.start";
	/**
	 * Opcode that is sent when the player stops to capture a capture point
	 */
	public static final String GAME_CAPTURE_STOP_REQUEST = "game.player.capture.stop";
	/**
	 * Opcode that indicates that a player started to capture a capture point
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the player that started to capture a point
	 */
	public static final String GAME_CAPTURE_STARTED = "game.player.capture.started";
	/**
	 * Opcode that indicates that a player stopped to capture a capture point
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the player that stopped to capture a point
	 */
	public static final String GAME_CAPTURE_STOPPED = "game.player.capture.stopped";
	
	///////////////////////////////////////////////////////////////////////////////
	// Game Objects																 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Opcode to send to get all moveable entities on the map (for initialization purposes)
	 * Send all the players including their current position (except for the own player) and
	 * all NPC's including their current position
	 */
	public static final String GAME_GET_MOVEABLE_ENTITIES = "game.get.objects";
	
	/**
	 * Packet that contains 1!! NPC to set on the map (only used for initialization!)
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM
	 * TODO other params to init npc
	 */
	public static final String GAME_SPAWN_NPC = "game.spawn.npc";
	
	/**
	 * Packet that contains 1!! Player to set on the map (only used for initialization!)
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM
	 * TODO other params to init npc
	 */
	public static final String GAME_SPAWN_PLAYER = "game.spawn.player";
	
	///////////////////////////////////////////////////////////////////////////////
	// Params																	 //
	///////////////////////////////////////////////////////////////////////////////
	
	public static final String ENTITY_ID_PARAM = "entityid";
	public static final String COORD_X_PARAM = "coordx";
	public static final String COORD_Y_PARAM = "coordy";
}
