package de.htw.saarland.gamedev.nap.data.network;

public class GameOpcodes {
	
	/////////////////////////////////////////////////////////////////////////
	// Game Start														   //
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S
	 * 
	 * Opcode to send once the game is initialized to tell the server that the logic can start
	 */
	public static final String GAME_INITIALIZED = "game.init";
	
	/**
	 * S->C
	 * 
	 * The Server sends this Opcode to each player when the game started
	 */
	public static final String GAME_START = "game.start";
	
	/////////////////////////////////////////////////////////////////////////
	// Movement															   //
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S
	 * 
	 * Opcode to request that the player would like to move left
	 * 
	 * returns:
	 * 	GAME_MOVE_LEFT_START when the server accepted the move and the character started to move left
	 * 	GAME_MOVE_STOP when the character stopped moving
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_LEFT_REQUEST = "game.player.move.left";
	/**
	 * C->S
	 * 
	 * Opcode to request that the player would like stop moving
	 * 
	 * returns:
	 * 	GAME_MOVE_STOP when the character stopped moving
	 */
	public static final String GAME_MOVE_STOP_REQUEST = "game.player.move.stop.request";
	/**
	 * C->S
	 * 
	 * Opcode to request that the player would like to move right
	 * 
	 * returns:
	 * 	GAME_MOVE_RIGHT_START when the server accepted the move and the character started to move right
	 * 	GAME_MOVE_STOP when the character stopped moving
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_RIGHT_REQUEST = "game.player.move.right";
	/**
	 * C->S
	 * 
	 * Opcode to request that the player would like to jump
	 * 
	 * returns:
	 * 	GAME_MOVE_JUMP_START when the server accepted the move and the character started to jump
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_JUMP_REQUEST = "game.player.move.jump";
	
	/**
	 * S->C
	 * 
	 * Opcode that indicates that a request to move left succeeded and the character should start moving left
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving left
	 */
	public static final String GAME_MOVE_LEFT_START = "game.player.move.left.start";
	/**
	 * S->C
	 * 
	 * Opcode that indicates that a request to move right succeeded and the character should start moving right
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving right
	 */
	public static final String GAME_MOVE_RIGHT_START = "game.player.move.right.start";
	/**
	 * S->C
	 * 
	 * Opcode that indicates that a request to jump succeeded and the character should start jumping right now
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that starts to jump
	 */
	public static final String GAME_MOVE_JUMP_START = "game.player.move.jump.start";
	/**
	 * S->C
	 * 
	 * Opcode that indicates that the character should stop moving, this can happen
	 * because of a collision with something or because the player stopped to press a move key
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the entity that stopped moving
	 */
	public static final String GAME_MOVE_STOP = "game.player.move.stop";
	
	/**
	 * S->C
	 * 
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
	 * C->S
	 * 
	 * Opcode that is sent as soon as a player wants to capture a capture point
	 */
	public static final String GAME_CAPTURE_START_REQUEST = "game.player.capture.start";
	/**
	 * C->S
	 * 
	 * Opcode that is sent when the player stops to capture a capture point
	 */
	public static final String GAME_CAPTURE_STOP_REQUEST = "game.player.capture.stop";
	/**
	 * S->C
	 * 
	 * Opcode that indicates that a player started to capture a capture point
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the player that started to capture a point
	 */
	public static final String GAME_CAPTURE_STARTED = "game.player.capture.started";
	/**
	 * S->C
	 * 
	 * Opcode that indicates that a player stopped to capture a capture point
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM the id of the player that stopped to capture a point
	 */
	public static final String GAME_CAPTURE_STOPPED = "game.player.capture.stopped";
	
	///////////////////////////////////////////////////////////////////////////////
	// Game Objects / Initialization											 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S
	 * 
	 * Opcode to send to get the map of the game and the own character (for initialization purposes)
	 * 
	 * returns:
	 * 	GAME_CURRENT_MAP contains the name of the map the current game plays on
	 * 	GAME_OWN_CHARACTER contains the id of the own character in the current game
	 */
	public static final String GAME_GET_MAP_CHARACTER = "game.get.mapchar";
	
	/**
	 * S->C
	 * 
	 * Opcode that contains the current map the game plays on
	 * 
	 * params:
	 * 	string CURRENT_MAP_PARAM name of the map
	 */
	public static final String GAME_CURRENT_MAP = "game.init.map";
	
	/**
	 * S->C
	 * 
	 * Packet that contains the own game character
	 * 
	 * params:
	 * 	int OWN_CHARACTER_PARAM id of the character
	 * 	float COORD_X_PARAM x coord of player
	 * 	float COORD_Y_PARAM y coord of player
	 * 	int ENTITY_ID_PARAM entity id of player
	 * 	int TEAM_ID_PARAM team id of player
	 */
	public static final String GAME_OWN_CHARACTER = "game.init.owncharacter";
	
	/**
	 * C->S
	 * 
	 * Opcode to send to get all moveable entities on the map (for initialization purposes)
	 * Send all the players including their current position (except for the own player) and
	 * all NPC's including their current position
	 */
	public static final String GAME_GET_MOVEABLE_ENTITIES = "game.get.objects";
	
	/**
	 * S->C
	 * 
	 * Packet that contains 1!! NPC to set on the map (only used for initialization!)
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM
	 * TODO other params to init npc
	 */
	public static final String GAME_SPAWN_NPC = "game.spawn.npc";
	
	/**
	 * S->C
	 * 
	 * Packet that contains 1!! Player to set on the map (only used for initialization!)
	 * 
	 * params:
	 * 	int ENTITY_ID_PARAM
	 * TODO other params to init npc
	 */
	public static final String GAME_SPAWN_PLAYER = "game.spawn.player";
	
	/**
	 * S->C
	 * 
	 * This opcode is sent as soon as all npcs and players are sent to the player
	 */
	public static final String GAME_END_OBJECTS = "game.done.sendobjects";
	
	///////////////////////////////////////////////////////////////////////////////
	// Params																	 //
	///////////////////////////////////////////////////////////////////////////////
	
	public static final String ENTITY_ID_PARAM = "entityid";
	public static final String COORD_X_PARAM = "coordx";
	public static final String COORD_Y_PARAM = "coordy";
	public static final String CURRENT_MAP_PARAM = "currentmap";
	public static final String OWN_CHARACTER_PARAM = "ownchar";
	public static final String TEAM_ID_PARAM = "teamid";
}
