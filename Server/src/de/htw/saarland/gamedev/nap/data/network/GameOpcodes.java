package de.htw.saarland.gamedev.nap.data.network;

public class GameOpcodes {
	
	/////////////////////////////////////////////////////////////////////////
	// Game Start														   //
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to send once the game is initialized to tell the server that the logic can start
	 */
	public static final String GAME_INITIALIZED = "game.init";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * The Server sends this Opcode to each player when the game started
	 */
	public static final String GAME_START = "game.start";
	
	/////////////////////////////////////////////////////////////////////////
	// Movement															   //
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like to move left<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_LEFT_START when the server accepted the move and the character started to move left<br/>
	 * 	GAME_MOVE_STOP when the character stopped moving<br/>
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_LEFT_REQUEST = "game.player.move.left";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like stop moving<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_STOP when the character stopped moving
	 */
	public static final String GAME_MOVE_STOP_RIGHT_REQUEST = "game.player.move.stop.right.request";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like stop moving<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_STOP when the character stopped moving
	 */
	public static final String GAME_MOVE_STOP_LEFT_REQUEST = "game.player.move.stop.left.request";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like to move right<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_RIGHT_START when the server accepted the move and the character started to move right<br/>
	 * 	GAME_MOVE_STOP when the character stopped moving<br/>
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_RIGHT_REQUEST = "game.player.move.right";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like to drop through a platform<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_DOWN_START when the server accepted the move and the character started to drop through<br/>
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_DOWN_REQUEST = "game.player.move.down";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like set the down var to false<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_DOWN_STOP when the server setted down to false on the character<br/>
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_DOWN_STOP_REQUEST = "game.player.down.stop";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like to jump<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_JUMP_START when the server accepted the move and the character started to jump<br/>
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_JUMP_REQUEST = "game.player.move.jump";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to request that the player would like to jump<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_MOVE_JUMP_START when the server accepted the move and the character started to jump<br/>
	 * 	GAME_OBJECT_COORD_UPDATE regularly updates to improve accuracy
	 */
	public static final String GAME_MOVE_JUMP_STOP_REQUEST = "game.player.move.jump.stop";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a request to move left succeeded and the character should start moving left<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving left
	 */
	public static final String GAME_MOVE_LEFT_START = "game.player.move.left.start";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a request to move right succeeded and the character should start moving right<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving right
	 */
	public static final String GAME_MOVE_RIGHT_START = "game.player.move.right.start";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a request to move down (through a platform) succeeded and the character should drop through<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving right
	 */
	public static final String GAME_MOVE_DOWN_START = "game.player.move.down.start";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a request to stop moving down succeeded and the character should stop dropping through platforms<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that starts moving right
	 */
	public static final String GAME_MOVE_DOWN_STOP = "game.player.move.down.stop";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a request to jump succeeded and the character should start jumping right now<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that starts to jump
	 */
	public static final String GAME_MOVE_JUMP_START = "game.player.move.jump.start";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a request to jump succeeded and the character should start jumping right now<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that starts to jump
	 */
	public static final String GAME_MOVE_JUMP_STOP = "game.player.move.jump.stop";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that the character should stop moving, this can happen<br/>
	 * because of a collision with something or because the player stopped to press a move key<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that stopped moving
	 */
	public static final String GAME_MOVE_STOP_RIGHT = "game.player.move.stop.right";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that the character should stop moving, this can happen<br/>
	 * because of a collision with something or because the player stopped to press a move key<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity that stopped moving
	 */
	public static final String GAME_MOVE_STOP_LEFT = "game.player.move.stop.left";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a game object should update its coordinates to improve accuracy<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the entity whose position should get updated<br/>
	 * 	float COORD_X_PARAM the x coordinate<br/>
	 * 	float COORD_Y_PARAM the y coordinate
	 */
	public static final String GAME_OBJECT_COORD_UPDATE = "game.player.position.update";
	
	///////////////////////////////////////////////////////////////////////////////
	// Player actions															 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode that is sent as soon as a player wants to capture a capture point
	 */
	public static final String GAME_CAPTURE_START_REQUEST = "game.player.capture.start";
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode that is sent when the player stops to capture a capture point
	 */
	public static final String GAME_CAPTURE_STOP_REQUEST = "game.player.capture.stop";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a player started to capture a capture point<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the player that started to capture a point
	 */
	public static final String GAME_CAPTURE_STARTED = "game.player.capture.started";
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that indicates that a player stopped to capture a capture point<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the player that stopped to capture a point
	 */
	public static final String GAME_CAPTURE_STOPPED = "game.player.capture.stopped";
	
	///////////////////////////////////////////////////////////////////////////////
	// Skills																	 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S<br/>
	 * Request to cast Skill1 (Skill1 key is being pressed)
	 */
	public static final String GAME_SKILL1_START_REQUEST = "game.player.skill1.start.request";
	/**
	 * S->C<br/>
	 * Cast Skill1 started succeeded<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the casting entity
	 */
	public static final String GAME_SKILL1_CAST_START = "game.player.skill1.caststart";
	/**
	 * S->C<br/>
	 * Cast Skill1 succeeded ended<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the casting entity
	 *  float DIRECTION_X_PARAM the x value of the direction vector
	 *  float DIRECTION_Y_PARAM the y value of the direction vector
	 */
	public static final String GAME_SKILL1_START = "game.player.skill1.start";
	
	/**
	 * C->S<br/>
	 * Request to cast Skill2 (Skill2 key is being pressed)
	 */
	public static final String GAME_SKILL2_START_REQUEST = "game.player.skill2.start.request";
	/**
	 * S->C<br/>
	 * Cast Skill2 succeeded started<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the casting entity
	 */
	public static final String GAME_SKILL2_CAST_START = "game.player.skill2.caststart";
	/**
	 * S->C<br/>
	 * Cast Skill2 succeeded ended<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the casting entity
	 *  float DIRECTION_X_PARAM the x value of the direction vector
	 *  float DIRECTION_Y_PARAM the y value of the direction vector
	 */
	public static final String GAME_SKILL2_START = "game.player.skill2.start";
	
	/**
	 * C->S<br/>
	 * Request to cast Skill3 (Skill3 key is being pressed)
	 */
	public static final String GAME_SKILL3_START_REQUEST = "game.player.skill3.start.request";
	/**
	 * S->C<br/>
	 * Cast Skill3 succeeded started<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the casting entity
	 */
	public static final String GAME_SKILL3_CAST_START = "game.player.skill3.caststart";
	/**
	 * S->C<br/>
	 * Cast Skill3 succeeded ended<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM the id of the casting entity
	 * 	float DIRECTION_X_PARAM the x value of the direction vector
	 *  float DIRECTION_Y_PARAM the y value of the direction vector
	 */
	public static final String GAME_SKILL3_START = "game.player.skill3.start";
	
	/**
	 * C->S<br/>
	 * Direction update for a skill<br/>
	 * <br/>
	 * params:<br/>
	 * 	float DIRECTION_X_PARAM x part of the direction vector
	 * 	float DIRECTION_Y_PARAM y part of the direction vector
	 */
	public static final String GAME_SKILL_DIRECTION_UPDATE = "game.player.skill.direction.update";
	/**
	 * S->C<br/>
	 * Direction request update for a client
	 */
	public static final String GAME_SKILL_DIRECTION_REQUEST = "game.player.skill.direction.request";
	
	///////////////////////////////////////////////////////////////////////////////
	// Game Objects / Initialization											 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to send to get the map of the game and the own character (for initialization purposes)<br/>
	 * <br/>
	 * returns:<br/>
	 * 	GAME_CURRENT_MAP contains the name of the map the current game plays on<br/>
	 * 	GAME_OWN_CHARACTER contains the id of the own character in the current game
	 */
	public static final String GAME_GET_MAP_CHARACTER = "game.get.mapchar";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Opcode that contains the current map the game plays on<br/>
	 * <br/>
	 * params:<br/>
	 * 	string CURRENT_MAP_PARAM name of the map
	 */
	public static final String GAME_CURRENT_MAP = "game.init.map";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Packet that contains the own game character<br/>
	 * <br/>
	 * params:<br/>
	 * 	int CHARACTER_ID_PARAM id of the character<br/>
	 * 	float COORD_X_PARAM x coord of player<br/>
	 * 	float COORD_Y_PARAM y coord of player<br/>
	 * 	int ENTITY_ID_PARAM entity id of player<br/>
	 * 	int TEAM_ID_PARAM team id of player
	 */
	public static final String GAME_OWN_CHARACTER = "game.init.owncharacter";
	
	/**
	 * C->S<br/>
	 * <br/>
	 * Opcode to send to get all moveable entities on the map (for initialization purposes)<br/>
	 * Send all the players including their current position (except for the own player) and
	 * all NPC's including their current position
	 */
	public static final String GAME_GET_MOVEABLE_ENTITIES = "game.get.objects";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Packet that contains 1!! NPC to set on the map (only used for initialization!)<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM<br/>
	 * TODO other params to init npc
	 */
	public static final String GAME_SPAWN_NPC = "game.spawn.npc";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Packet that contains 1!! Player to set on the map (only used for initialization!)<br/>
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM entity id of player<br/>
	 *  int CHARACTER_ID_PARAM char id of the player<br/>
	 *  int TEAM_ID_PARAM team if of the player<br/>
	 *  float COORD_X_PARAM x coord of that player<br/>
	 *  float COORD_Y_PARAM y coord of that player<br/>
	 *  String PLAYER_NAME_PARAM the name of the player
	 */
	public static final String GAME_SPAWN_PLAYER = "game.spawn.player";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * This opcode is sent as soon as all npcs and players are sent to the player
	 */
	public static final String GAME_END_OBJECTS = "game.done.sendobjects";
	
	///////////////////////////////////////////////////////////////////////////////
	// Status Updates															 //
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Sends the updated health of any game entity to a client
	 * <br/>
	 * params:<br/>
	 * 	int ENTITY_ID_PARAM entity id of the entity<br/>
	 * 	int HEALTH_PARAM the new health of the entity
	 */
	public static final String GAME_UPDATE_HEALTH = "game.update.health";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Sets the stun status of an entity<br>
	 * <br/>
	 * param:<br/>
	 * 	int ENTITY_ID_PARAM id of the entity<br/>
	 * 	boolean STUN_STATUS_PARAM true if the entity is stunned, else false
	 */
	public static final String GAME_UPDATE_STATUS_STUN = "game.update.status.stun";
	
	/**
	 * S->C<br/>
	 * <br/>
	 * Sets the snare status of an entity<br>
	 * <br/>
	 * param:<br/>
	 * 	int ENTITY_ID_PARAM id of the entity<br/>
	 * 	boolean SNARE_STATUS_PARAM true if the entity is snared, else false
	 */
	public static final String GAME_UPDATE_STATUS_SNARE = "game.update.status.snare";
	
	///////////////////////////////////////////////////////////////////////////////
	// Params																	 //
	///////////////////////////////////////////////////////////////////////////////
	
	public static final String ENTITY_ID_PARAM = "entityid";
	public static final String COORD_X_PARAM = "coordx";
	public static final String COORD_Y_PARAM = "coordy";
	public static final String CURRENT_MAP_PARAM = "currentmap";
	public static final String CHARACTER_ID_PARAM = "charid";
	public static final String TEAM_ID_PARAM = "teamid";
	public static final String DIRECTION_X_PARAM = "directionx";
	public static final String DIRECTION_Y_PARAM = "directiony";
	public static final String PLAYER_NAME_PARAM = "playername";
	public static final String HEALTH_PARAM = "health";
	public static final String STUN_STATUS_PARAM = "stun";
	public static final String SNARE_STATUS_PARAM = "snare";
}
