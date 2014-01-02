package de.htw.saarland.gamedev.nap.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import de.htw.saarland.gamedev.nap.NetworkConstants;
import de.htw.saarland.gamedev.nap.client.entity.ClientNPC;
import de.htw.saarland.gamedev.nap.client.entity.ClientPlayer;
import de.htw.saarland.gamedev.nap.client.entity.EntityNotFound;
import de.htw.saarland.gamedev.nap.client.entity.IMoveable;
import de.htw.saarland.gamedev.nap.client.entity.MeClientPlayer;
import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.client.input.KeyboardMouseInputProcessor;
import de.htw.saarland.gamedev.nap.client.world.RenderableGameWorld;
import de.htw.saarland.gamedev.nap.data.Mage;
import de.htw.saarland.gamedev.nap.data.NPC;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Warrior;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;

/**
 * Main Game loop
 * 
 * @author Pascal
 * 
 */
public class GameClient implements ApplicationListener, IEventListener {

	private SmartFox sfClient;
	private Room gameRoom;
	private IBaseInput inputProcessor;
	private MeClientPlayer player;
	
	private boolean isInitialized;
	private volatile boolean allObjectsReceived;
	private boolean gameStarted;
	
	private World world;
	private RenderableGameWorld gameWorld;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private List<ClientPlayer> players;
	private List<ClientNPC> npcs;
	
	public GameClient(SmartFox sfClient, Room gameRoom) {
		if (sfClient == null || gameRoom == null) {
			throw new NullPointerException();
		}
		
		this.sfClient = sfClient;
		this.gameRoom = gameRoom;
		this.isInitialized = false;
		this.gameStarted = false;
		this.allObjectsReceived = false;
		
		sfClient.addEventListener(SFSEvent.PING_PONG, this);
		sfClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
	}
	
	@Override
	public void create() {
		// TODO load keys from own file
		this.inputProcessor = new KeyboardMouseInputProcessor(
				Buttons.LEFT,
				Keys.NUM_1,
				Keys.NUM_2,
				Keys.SPACE,
				Keys.A,
				Keys.D,
				Keys.S,
				Keys.F);
		
		batch = new SpriteBatch();
		world = new World(GameServer.GRAVITY, true);
		
		players = Collections.synchronizedList(new ArrayList<ClientPlayer>());
		npcs = Collections.synchronizedList(new ArrayList<ClientNPC>());
		
		//TODO is viewport ok?
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		sfClient.send(new ExtensionRequest(GameOpcodes.GAME_GET_MAP_CHARACTER, null, gameRoom));
		sfClient.send(new ExtensionRequest(GameOpcodes.GAME_GET_MOVEABLE_ENTITIES, null, gameRoom));
	}

	@Override
	public void resize(int width, int height) {
		// TODO what todo on resizing?
	}

	@Override
	public void render() {
		if (!gameStarted)
			return;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		inputProcessor.process();
		player.render(batch);
		
		synchronized (players) {
			for (ClientPlayer player: players) {
				player.render(batch);
			}
		}
		
		gameWorld.render(batch);
		world.step(GameServer.TIME_STEP, GameServer.ITERATIONS_VELOCITY, GameServer.ITERATIONS_POSITION);
	}
	
	@Override
	public void dispose() {
		gameWorld.dispose();
		world.dispose();
		
		sfClient.removeAllEventListeners();
		sfClient.disconnect();
	}

	@Override
	public void dispatch(BaseEvent be) throws SFSException {
		switch (be.getType()) {
			case SFSEvent.PING_PONG:
				break;
			case SFSEvent.EXTENSION_RESPONSE:
				dispatchExtensionResponse(be);
				break;
			default:
				System.out.println("Unknown packet received! : " + be.getType());
				break;
		}
	}
	
	public void dispatchExtensionResponse(BaseEvent be) {
		String cmd = (String)be.getArguments().get(NetworkConstants.CMD_KEY);
		SFSObject params = (SFSObject)be.getArguments().get(NetworkConstants.PARAMS_KEY);
		
		switch (cmd) {
			case GameOpcodes.GAME_START:
				gameStarted = true; //lets go!
				break;
			case GameOpcodes.GAME_CURRENT_MAP:
				gameWorld = new RenderableGameWorld(world, params.getUtfString(GameOpcodes.CURRENT_MAP_PARAM), 0, camera); // TODO is there a concurrency problem?
				checkInitialized();
				break;
			case GameOpcodes.GAME_SPAWN_PLAYER:
			case GameOpcodes.GAME_OWN_CHARACTER:
				PlayableCharacter character = null;
				
				int charid = params.getInt(GameOpcodes.CHARACTER_ID_PARAM);
				float coordx = params.getFloat(GameOpcodes.COORD_X_PARAM);
				float coordy = params.getFloat(GameOpcodes.COORD_Y_PARAM);
				int entid = params.getInt(GameOpcodes.ENTITY_ID_PARAM);
				int teamid = params.getInt(GameOpcodes.TEAM_ID_PARAM);
				
				switch (charid) {
					case PlayableCharacter.ID_WARRIOR:
						character = new Warrior(new Vector2(coordx, coordy), teamid, entid);
						break;
					case PlayableCharacter.ID_MAGE:
						character = new Mage(new Vector2(coordx, coordy), teamid, entid);
						break;
					default:
						System.out.println("This character id does not exist! " + charid);
						break;
				}
				
				if (cmd.equals(GameOpcodes.GAME_OWN_CHARACTER)) {
					player = new MeClientPlayer(character, teamid, inputProcessor, sfClient, gameRoom);
					checkInitialized();
				}
				else {
					players.add(new ClientPlayer(character, teamid));
				}
				
				break;
			case GameOpcodes.GAME_SPAWN_NPC:
				// TODO spawn npc, maybe this method is not necessary depending on GameWorld class
				break;
			case GameOpcodes.GAME_END_OBJECTS:
				allObjectsReceived = true;
				checkInitialized();
				break;
			//Init opcodes end
			
			case GameOpcodes.GAME_OBJECT_COORD_UPDATE:
				updateCoordsOfObject(params.getInt(GameOpcodes.ENTITY_ID_PARAM),
						params.getFloat(GameOpcodes.COORD_X_PARAM),
						params.getFloat(GameOpcodes.COORD_Y_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_LEFT_START:
				moveEntity(Direction.LEFT, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_RIGHT_START:
				moveEntity(Direction.RIGHT, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_JUMP_START:
				moveEntity(Direction.JUMP, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_STOP:
				moveEntity(Direction.STOP, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			default:
				System.out.println("Unknown packet received! : " + cmd);
				break;
		}
	}
	
	private void checkInitialized() {
		if (isInitialized)
			return;
		
		isInitialized = gameWorld != null && world != null && player != null && allObjectsReceived;
		
		if (isInitialized) {
			sfClient.send(new ExtensionRequest(GameOpcodes.GAME_INITIALIZED, null, gameRoom));
		}
	}
	
	private ClientPlayer getPlayerById(int entityId) {
		Object entity = getMoveableEntityById(entityId);
		
		if (entity instanceof ClientPlayer) {
			return (ClientPlayer)entity;
		}
		else {
			return null;
		}
	}
	
	private IMoveable getMoveableEntityById(int entityId) {
		if (entityId == player.getEntityId()) {
			//bitch its me
			return player;
		}
		else {
			synchronized (players) {
				for (ClientPlayer player: players) {
					if (entityId == player.getEntityId()) {
						return player;
					}
				}
			}
			
			synchronized (npcs) {
				for (ClientNPC npc: npcs) {
					if (entityId == npc.getEntityId()) {
						return npc;
					}
				}
			}
		}
		
		throw new EntityNotFound("Entity with id " + entityId + "not found!");
	}
	
	private void updateCoordsOfObject(int entityId, float coordX, float coordY) {
		Vector2 pos = new Vector2(coordX, coordY);
		IMoveable entity = getMoveableEntityById(entityId);
		
		entity.setPosition(pos);
	}
	
	private void moveEntity(Direction direction, int entityId) {
		IMoveable entity = getMoveableEntityById(entityId);
		
		switch (direction) {
			case LEFT:
				entity.moveLeft();
				break;
			case RIGHT:
				entity.moveRight();
				break;
			case JUMP:
				entity.startJump();
				break;
			case STOP:
				entity.stopMove();
				break;
		}
	}

	@Override
	public void pause() {
		// Ignore this method, only for android apps!
	}
	@Override
	public void resume() {
		// Ignore this method, only for android apps!
	}
	
	public enum Direction {
		LEFT,
		RIGHT,
		JUMP,
		STOP
	}
	
}
