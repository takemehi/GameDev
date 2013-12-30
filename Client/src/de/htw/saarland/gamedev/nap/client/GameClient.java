package de.htw.saarland.gamedev.nap.client;

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
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import de.htw.saarland.gamedev.nap.NetworkConstants;
import de.htw.saarland.gamedev.nap.client.entity.ClientPlayer;
import de.htw.saarland.gamedev.nap.client.input.IBaseInput;
import de.htw.saarland.gamedev.nap.client.input.KeyboardMouseInputProcessor;
import de.htw.saarland.gamedev.nap.client.world.RenderableGameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
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
	private ClientPlayer player;
	
	private boolean isInitialized;
	private volatile boolean allObjectsReceived;
	private boolean gameStarted;
	
	private World world;
	private RenderableGameWorld gameWorld;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
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
		
		//TODO is viewport ok?
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		sfClient.send(new ExtensionRequest(GameOpcodes.GAME_GET_MAP_CHARACTER, null, gameRoom));
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
			case GameOpcodes.GAME_OWN_CHARACTER:
				PlayableCharacter character = null;
				// TODO init character based on param OWN_CHARACTER_PARAM, COORD_X_PARAM, COORD_Y_PARAM, ENTITY_ID
				player = new ClientPlayer(character, params.getInt(GameOpcodes.TEAM_ID_PARAM), inputProcessor, sfClient);
				
				checkInitialized();
				break;
			case GameOpcodes.GAME_END_OBJECTS:
				allObjectsReceived = true;
				checkInitialized();
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

	@Override
	public void pause() {
		// Ignore this method, only for android apps!
	}
	@Override
	public void resume() {
		// Ignore this method, only for android apps!
	}
	
}
