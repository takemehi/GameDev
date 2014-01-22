package de.htw.saarland.gamedev.nap.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.SpawnPoint;
import de.htw.saarland.gamedev.nap.data.Team;
import de.htw.saarland.gamedev.nap.data.map.ServerTmxMapLoader;
import de.htw.saarland.gamedev.nap.server.DeltaTime;
import de.htw.saarland.gamedev.nap.server.ServerExtension;
import de.htw.saarland.gamedev.nap.server.launcher.exception.PlayerNotFoundException;

public class GameServer implements ApplicationListener, ISendPacket {
	
	//exceptions
	private final static String EXCEPTION_ILLEGAL_MAP = "Map files are missing!";
	private final static String EXCEPTION_ILLEGAL_MAP_EMPTY = "Map name is empty!";
	private final static String EXCEPTION_NULL_TEAM1 = "Team1 object is null!";
	private final static String EXCEPTION_NULL_TEAM2 = "Team2 object is null!";
	//folders
	private final static String FOLDER_DATA = "extensions/nap/data/";
	private final static String FOLDER_MAPS = "extensions/nap/data/maps/";
	//world renderer constants
	public final static float TIME_STEP = 1/30f;
	public final static int ITERATIONS_VELOCITY = 6;
	public final static int ITERATIONS_POSITION = 2;
	//world constants
	public final static Vector2 GRAVITY = new Vector2(0, -20);
	//others
	public final static float PIXELS_TO_METERS = 1/96f;
	public final static int POINTS_PER_INTERVAL = 2;
	public final static float INTERVAL_POINTS = 5.0f;
	public final static int[] LAYERS_TO_RENDER = {0,1,2};
	//input parameters
	private String mapName;
	private TiledMap map;
	
	private Team blueTeam;
	private Team redTeam;
	
	private World world;
	private GameWorld gameWorld;
	private float worldTime;
	
	private ArrayList<Integer> charactersBlue; //temporarily needed
	private ArrayList<SFSUser> blueMembers; //temporarily needed
	private ArrayList<Integer> charactersRed; //temporarily needed
	private ArrayList<SFSUser> redMembers; //temporarily needed	
	
	private Array<CapturePoint> capturePoints;
	
	private int currentId;
	
	private boolean gameEnded;
	private boolean started;
	
	DeltaTime deltaTime;
	
	ServerExtension extension;
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public GameServer(String mapName,
			ArrayList<SFSUser> userBlue,
			ArrayList<SFSUser> userRed,
			ArrayList<Integer> charactersBlue,
			ArrayList<Integer> charactersRed,
			DeltaTime deltaTime,
			ServerExtension extension){
		
		if(mapName.trim().length()<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_MAP_EMPTY);
		if(userBlue == null) throw new NullPointerException(EXCEPTION_NULL_TEAM1);
		if(userRed == null) throw new NullPointerException(EXCEPTION_NULL_TEAM2);
		if(!new File(FOLDER_MAPS+mapName+".tmx").exists() || !new File(FOLDER_MAPS+mapName+".json").exists())
			throw new IllegalArgumentException(EXCEPTION_ILLEGAL_MAP);
		if (deltaTime == null || extension == null) {
			throw new NullPointerException();
		}
		
		this.mapName = mapName;
		
		this.blueMembers = userBlue;
		this.charactersBlue = charactersBlue;
		
		this.redMembers = userRed;
		this.charactersRed = charactersRed;
		
		capturePoints=new Array<CapturePoint>();
		
		currentId=0;
		worldTime = 0;
		gameEnded=false;
		started = false;
		this.deltaTime=deltaTime;
		this.extension = extension;
	}
	
	//////////////////////////
	//	override methods	//
	//////////////////////////
	
	@Override
	public void create() {
		//initialize world
		try {
			GdxNativesLoader.load();
		}
		catch (UnsatisfiedLinkError e) {
			//libs are already loaded
		}
		world = new World(GRAVITY, true);
		//initialize gameWorld
		gameWorld = new GameWorld(world, FOLDER_MAPS + mapName, currentId, new ServerTmxMapLoader());
		
		//initialize map
		this.map = gameWorld.getTiledMap();
		this.currentId = gameWorld.getCurrentId();
		//initialize capturePoints
		this.capturePoints = gameWorld.getCapturePoints();
		//initialize spawnPoints
		SpawnPoint spawnPointBlue = gameWorld.getSpawnPointBlue();
		SpawnPoint spawnPointRed = gameWorld.getSpawnPointRed();
		//initialize players
		
		Array<Player> blue = new Array<Player>();
		for(int i=0; i<charactersBlue.size(); i++){
			blue.add(new Player(blueMembers.get(i), world, spawnPointBlue.getSpawnPoint().getPositionOriginal(), charactersBlue.get(i), PlayableCharacter.ID_TEAM_BLUE, currentId++, this));
		}
		blueTeam = new Team(spawnPointBlue, blue);
		
		Array<Player> red = new Array<Player>();
		for(int i=0; i<charactersRed.size(); i++){
			red.add(new Player(redMembers.get(i), world, spawnPointRed.getSpawnPoint().getPositionOriginal(), charactersRed.get(i), PlayableCharacter.ID_TEAM_RED, currentId++, this));
		}
		redTeam = new Team(spawnPointRed, red);
		
		world.setContactListener(new CustomContactListener(blueTeam, redTeam, capturePoints));
		
		// TODO initialize npcs
		
		//free memory
		blueMembers = null;
		charactersBlue = null;
		redMembers = null;
		charactersRed = null;
	}		
	
	@Override
	public void render() {
		if (!started)
			return;
		
		Array<Player> players = new Array<>(blueTeam.getMembers());
		players.addAll(redTeam.getMembers());
		for (Player player: players) {
			player.update(deltaTime.getDeltaTime(), capturePoints);
			player.getPlChar().getAttack1().cleanUp();
			player.getPlChar().getAttack2().cleanUp();
			player.getPlChar().getAttack3().cleanUp();
		}
		
		
		for (CapturePoint cp: capturePoints) {
			cp.update(deltaTime.getDeltaTime());
		}
		
		worldTime += deltaTime.getDeltaTime();
		if (worldTime > TIME_STEP) {
			world.step(TIME_STEP, ITERATIONS_VELOCITY, ITERATIONS_POSITION);
			worldTime = 0;
		}
	}
	
	@Override
	public void dispose() {
		world.dispose();
		map.dispose();
	}

	@Override
	public void resize(int width, int height) {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	
	//////////////////////
	//	public methods	//
	//////////////////////
	
	public Team getBlueTeam() {
		return blueTeam;
	}
	
	public Team getRedTeam() {
		return redTeam;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayerBySFSUser(SFSUser user) {
		for (int i = 0; i < blueTeam.getMembers().size; i++) {
			if (blueTeam.getMembers().get(i).getUser().equals(user)) {
				return blueTeam.getMembers().get(i);
			}
		}
		
		for (int i = 0; i < redTeam.getMembers().size; i++) {
			if (redTeam.getMembers().get(i).getUser().equals(user)) {
				return redTeam.getMembers().get(i);
			}
		}
		
		throw new PlayerNotFoundException();
	}
	
	public void startGame() {
		this.started = true;
	}

//	public void addPacket(SFSObject packet){
//		if(packet == null) throw new NullPointerException(EXCEPTION_NULL_PACKET);
//		packetQueue.add(packet);
//	}
	
	public void handlePacket(String opcode, User user, ISFSObject args) {
		
	}
	
	public boolean isGameEnd() {
		return gameEnded;
	}

	@Override
	public void sendServerPacket(String opcode, ISFSObject params, List<User> recipients) {
		extension.send(opcode, params, recipients);		
	}

	@Override
	public void sendServerPacket(String opcode, ISFSObject params, User recipient) {
		extension.send(opcode, params, recipient);
	}

	@Override
	public void sendServerPacket(String opcode, ISFSObject params) {
		extension.send(opcode, params, extension.getParentRoom().getPlayersList());
	}

	@Override
	public void sendServerPacketUDP(String opcode, ISFSObject params) {
		extension.send(opcode, params, extension.getParentRoom().getPlayersList(), true);
	}

	@Override
	public void sendServerPacket(String opcode, ISFSObject params, int entityId) {
		Player p = null;
		for (Player player : blueTeam.getMembers()) {
			if (player.getPlChar().getId() == entityId) {
				p = player;
				break;
			}
		}
		
		if (p == null) {
			for (Player player : redTeam.getMembers()) {
				if (player.getPlChar().getId() == entityId) {
					p = player;
					break;
				}
			}
		}
		
		if (p != null) { 
			extension.send(opcode, params, p.getUser());
		}
		
	}
}
