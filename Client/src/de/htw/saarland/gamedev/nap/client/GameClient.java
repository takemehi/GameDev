package de.htw.saarland.gamedev.nap.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.requests.ExtensionRequest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import de.htw.saarland.gamedev.nap.NetworkConstants;
import de.htw.saarland.gamedev.nap.client.entity.ClientNPC;
import de.htw.saarland.gamedev.nap.client.entity.ClientPlayer;
import de.htw.saarland.gamedev.nap.client.entity.EntityNotFound;
import de.htw.saarland.gamedev.nap.client.entity.IMoveable;
import de.htw.saarland.gamedev.nap.client.entity.MeClientPlayer;
import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.client.input.InputConfigLoader;
import de.htw.saarland.gamedev.nap.client.input.KeyboardMouseInputProcessor;
import de.htw.saarland.gamedev.nap.client.world.RenderableGameWorld;
import de.htw.saarland.gamedev.nap.data.Mage;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Warrior;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.CustomContactListener;
import de.htw.saarland.gamedev.nap.game.GameServer;

/**
 * Main Game loop
 * 
 * @author Pascal
 * 
 */
public class GameClient implements ApplicationListener, IEventListener {

	public final static String FOLDER_CONFIG = "data/config/";
	public static final String FOLDER_MAPS = "data/maps/";
	
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
	
	private List<BaseEvent> initPackets;
	
	//TODO remove
	private Box2DDebugRenderer debugRenderer;
	
	public GameClient(SmartFox sfClient, Room gameRoom) {
		if (sfClient == null || gameRoom == null) {
			throw new NullPointerException();
		}
		
		this.sfClient = sfClient;
		this.gameRoom = gameRoom;
		this.isInitialized = false;
		this.gameStarted = false;
		this.allObjectsReceived = false;
		this.initPackets = Collections.synchronizedList(new ArrayList<BaseEvent>());
		
		sfClient.addEventListener(SFSEvent.PING_PONG, this);
		sfClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
	}
	
	@Override
	public void create() {
		//this is the standard config!
//		this.inputProcessor = new KeyboardMouseInputProcessor(
//				Buttons.LEFT,
//				Keys.NUM_1,
//				Keys.NUM_2,
//				Keys.SPACE,
//				Keys.A,
//				Keys.D,
//				Keys.S,
//				Keys.F);
		
		InputConfigLoader icf = new InputConfigLoader(new File(FOLDER_CONFIG, "keys.cfg"));
		try {
			icf.load();
		} catch (NumberFormatException e) {
			System.out.println("Error loading config file, invalid character!");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Error loading config file, io error!");
			e.printStackTrace();
			System.exit(-1);
		}
		this.inputProcessor = new KeyboardMouseInputProcessor(
				icf.getSkill1Key(),
				icf.getSkill2Key(),
				icf.getSkill3Key(),
				icf.getJumpKey(),
				icf.getRightKey(),
				icf.getLeftKey(),
				icf.getDownKey(),
				icf.getCaptureKey());
		
		batch = new SpriteBatch();
		world = new World(GameServer.GRAVITY, true);
		
		players = Collections.synchronizedList(new ArrayList<ClientPlayer>());
		npcs = Collections.synchronizedList(new ArrayList<ClientNPC>());
		
		//TODO is viewport ok?
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
		camera.position.set(Gdx.graphics.getWidth()/40-2, Gdx.graphics.getHeight()/40-2, 0);
		camera.update();
		
		sfClient.send(new ExtensionRequest(GameOpcodes.GAME_GET_MAP_CHARACTER, null, gameRoom));
		sfClient.send(new ExtensionRequest(GameOpcodes.GAME_GET_MOVEABLE_ENTITIES, null, gameRoom));
		
		//TODO remove
		debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		if (initPackets.size() > 0) {
			synchronized (initPackets) {
				for (BaseEvent be: initPackets) {
					dispatchExtensionResponse(be);
				}
				initPackets.clear();
			}
		}
		
		if (!gameStarted)
			return;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		inputProcessor.process();
		player.getPlayableCharacter().update(Gdx.graphics.getDeltaTime(), gameWorld.getCapturePoints());
		player.render(batch);
		
		synchronized (players) {
			for (ClientPlayer player: players) {
				player.getPlayableCharacter().update(Gdx.graphics.getDeltaTime(), gameWorld.getCapturePoints());
				player.render(batch);
			}
		}
		
		synchronized (npcs) {
			for (ClientNPC npc: npcs) {
				npc.render(batch);
			}
		}
		
		gameWorld.render(batch);
		
		//TODO remove
		debugRenderer.render(world, camera.combined);
		
		batch.end();
		
		world.step(GameServer.TIME_STEP, GameServer.ITERATIONS_VELOCITY, GameServer.ITERATIONS_POSITION);
	}
	
	@Override
	public void dispose() {
		gameWorld.dispose();
		world.dispose();
		player.dispose();
		
		for (ClientPlayer player: players) {
			player.dispose();
		}
		
		for (ClientNPC npc: npcs) {
			npc.dispose();
		}
		
		sfClient.removeAllEventListeners();
		sfClient.disconnect();
	}

	@Override
	public void dispatch(BaseEvent be) throws SFSException {
		switch (be.getType()) {
			case SFSEvent.PING_PONG:
				break;
			case SFSEvent.EXTENSION_RESPONSE:
				if (!isInitialized) {
					initPackets.add(be);
				}
				else {
					dispatchExtensionResponse(be);
				}
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
				gameWorld = new RenderableGameWorld(world, FOLDER_MAPS + params.getUtfString(GameOpcodes.CURRENT_MAP_PARAM), 0, camera); // TODO is there a concurrency problem?
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
						character = new Warrior(world, new Vector2(coordx, coordy), teamid, entid);
						break;
					case PlayableCharacter.ID_MAGE:
						character = new Mage(world, new Vector2(coordx, coordy), teamid, entid);
						break;
					default:
						System.out.println("This character id does not exist! " + charid);
						break;
				}
				
				character.setBody(world.createBody(character.getBodyDef()));
				character.setFixture(character.getBody().createFixture(character.getFixtureDef()));
				
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
			case GameOpcodes.GAME_MOVE_DOWN_START:
				moveEntity(Direction.DOWN, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_JUMP_START:
				moveEntity(Direction.JUMP, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_STOP:
				moveEntity(Direction.STOP, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_DOWN_STOP:
				moveEntity(Direction.STOP_DOWN, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
				break;
			case GameOpcodes.GAME_MOVE_JUMP_STOP:
				moveEntity(Direction.STOP_JUMP, params.getInt(GameOpcodes.ENTITY_ID_PARAM));
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
			//TODO init contact listener
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
				System.out.println("left");
				entity.moveLeft();
				break;
			case RIGHT:
				System.out.println("right");
				entity.moveRight();
				break;
			case DOWN:
				System.out.println("down");
				entity.moveDown();
				break;
			case JUMP:
				System.out.println("jump");
				entity.startJump();
				break;
			case STOP_JUMP:
				System.out.println("stop jump");
				entity.stopJump();
				break;
			case STOP:
				System.out.println("stop");
				entity.stopMove();
				break;
			case STOP_DOWN:
				System.out.println("stop down");
				entity.stopDown();
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
		DOWN,
		JUMP,
		STOP,
		STOP_DOWN,
		STOP_JUMP
	}
	
}
