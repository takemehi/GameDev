package de.htw.saarland.gamedev.nap.game;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.NPC;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.SpawnPoint;
import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.entities.StaticEntity;
import de.htw.saarland.gamedev.nap.data.platforms.CustomContactListener;

public class GameServer implements ApplicationListener {
	
	//exceptions
	private final static String EXCEPTION_ILLEGAL_MAP = "Map files are missing!";
	private final static String EXCEPTION_ILLEGAL_MAP_EMPTY = "Map name is empty!";
	private final static String EXCEPTION_ILLEGAL_PLATFORM_ID = "Platform Id doesn't exist!";
	private final static String EXCEPTION_ILLEGAL_TEAMSIZE = "Teamsize can't be less than 1!";
	private final static String EXCEPTION_ILLEGAL_TEAM_FULL = "Team is already full!";
	private final static String EXCEPTION_ILLEGAL_TEAM_ID = "Team Id is not existing!";
	private final static String EXCEPTION_NULL_ENTITY = "Entity object is null!";
	private final static String EXCEPTION_NULL_PACKET = "Packet object is null!";
	private final static String EXCEPTION_NULL_PLAYER = "Player object is null!";
	private final static String EXCEPTION_NULL_TEAM1 = "Team1 object is null!";
	private final static String EXCEPTION_NULL_TEAM2 = "Team2 object is null!";
	private final static String EXCEPTION_NULL_VECTOR = "Vector object is null!";
	//folders
	private final static String FOLDER_DATA = "data/";
	private final static String FOLDER_MAPS = "data/maps/";
	//packets processed per tick
	private final static int PACKETS_PER_TICK = 50;
	//world renderer constants
	private final static float TIME_STEP = 1/60f;
	private final static int ITERATIONS_VELOCITY = 6;
	private final static int ITERATIONS_POSITION = 2;
	//world constants
	private final static Vector2 GRAVITY = new Vector2(0, -20);
	//others
	private final static float PIXELS_TO_METERS = 1/96f;
	private final static int MAX_POINTS = 200;
	private final static float INTERVAL_POINTS = 5.0f;
	private final static int[] LAYERS_TO_RENDER = {0,1,2};
	//ids
	private static final int ID_TEAM_BLUE = 0;
	private static final int ID_TEAM_RED = 1;
	private final static int ID_TILE_PLATFORM_ONE = -1;
	private final static int ID_TILE_PLATFORM_TWO = 2;
	private final static int ID_TILE_SPAWN_POINT_BLUE = 12;
	private final static int ID_TILE_SPAWN_POINT_RED = 13;
	private final static int ID_TILE_CAPTURE_POINT = 0;
	private final static int ID_TILE_CREEP_DMG = 0;
	private final static int ID_TILE_CREEP_RES = 0;
	
	
	//input parameters
	private String mapName;
	private TiledMap map;
	private int teamSize;
	//TODO maybe change the type from npc to extended class
	private ArrayList<NPC> npcs;
	
	//internal variables
	private ConcurrentLinkedQueue<SFSObject> packetQueue;	
	private World world;
	private GameWorld gameWorld;
	private SpawnPoint SpawnPointBlue;
	private SpawnPoint SpawnPointRed;
	private ArrayList<Player> teamBlue;
	private ArrayList<Player> teamRed;
	private ArrayList<CapturePoint> capturePoints;
	private ArrayList<StaticEntity> platforms;
	boolean capturing = false;
	float captureTime = 0;
	int currentId;
	int pointsBlue;
	int pointsRed;
	boolean gameEnded;
	float deltaTime;
	
	
	//test variables
	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;
	//rendering test
	SpriteBatch batch;
	OrthogonalTiledMapRenderer mapRenderer;
	Texture walkSheet;
	TextureRegion currentFrame;
	TextureRegion walkFrames[];
	Animation walkAnimation;
	float stateTime;
	
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public GameServer(String mapName, int teamSize, ArrayList<Player> team1, ArrayList<Player> team2){
		if(mapName.trim().length()<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_MAP_EMPTY);
		if(teamSize<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAMSIZE);
		if(team1 == null) throw new NullPointerException(EXCEPTION_NULL_TEAM1);
		if(team2 == null) throw new NullPointerException(EXCEPTION_NULL_TEAM2);
		if(!new File(FOLDER_MAPS+mapName+".tmx").exists() || !new File(FOLDER_MAPS+mapName+".json").exists())
			throw new IllegalArgumentException(EXCEPTION_ILLEGAL_MAP);
		
		this.mapName=mapName;
		this.teamSize=teamSize;
		this.teamBlue=team1;
		this.teamRed=team2;
		platforms=new ArrayList<StaticEntity>();
		capturePoints=new ArrayList<CapturePoint>();
		currentId=0;
		gameEnded=false;
		deltaTime=0;
	}
	
	//////////////////////////
	//	override methods	//
	//////////////////////////
	
	@Override
	public void create() {
		//initialize world
		world = new World(GRAVITY, true);
		world.setContactListener(new CustomContactListener());
		//initialize map
		gameWorld = new GameWorld(world, mapName, currentId);
		this.map = gameWorld.getTiledMap();
		//initialize players
		
		//Test stuff
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
		camera.position.set(Gdx.graphics.getWidth()/40-2, Gdx.graphics.getHeight()/40-2, 0);
		camera.update();
		mapRenderer = new OrthogonalTiledMapRenderer(map, PIXELS_TO_METERS);
		mapRenderer.setView(camera);
		batch = new SpriteBatch();		
		
		PolygonShape playerShape = new PolygonShape();
		playerShape.setAsBox(.2f, .35f);
		PlayableCharacter playerEntity = new PlayableCharacter(playerShape, 0.1f, 0, 0, new Vector2(2,2), new Vector2(5,2), new Vector2(10,2), 100, currentId++);		
		
		playerEntity.setBody(world.createBody(playerEntity.getBodyDef()));
		playerEntity.setFixture(playerEntity.getBody().createFixture(playerEntity.getFixtureDef()));
		playerEntity.getFixture().setUserData("player");
		Player player = new Player(null, playerEntity, ID_TEAM_BLUE);
		teamBlue.add(player);
	}
	
	@Override
	public void render() {
		//get client packets
		/*
		for(int i=0; i<PACKETS_PER_TICK; i++){
			packetQueue.remove();
			//do damn cool stuff
			//nigga
		}*/
		//check for ability collisions, damage abilities		
		//calculate hp, apply status effects, recalculate position
		//send stuff

		//TODO add red team or change from seperate lists to one global list
		//Movement 
		for(Player p: teamBlue){
			if(p.getPlChar().isJumping()) p.getPlChar().setTimeonGround(0);
			else p.getPlChar().setTimeonGround(p.getPlChar().getTimeOnGround()+Gdx.graphics.getDeltaTime());
			
			if(!Gdx.input.isKeyPressed(Keys.A))
				p.getPlChar().getBody().setLinearVelocity(0, p.getPlChar().getBody().getLinearVelocity().y);
			if(!Gdx.input.isKeyPressed(Keys.D))
				p.getPlChar().getBody().setLinearVelocity(0, p.getPlChar().getBody().getLinearVelocity().y);
			if(Gdx.input.isKeyPressed(Keys.A))
				p.getPlChar().getBody().setLinearVelocity(-5, p.getPlChar().getBody().getLinearVelocity().y);
			if(Gdx.input.isKeyPressed(Keys.D))
				p.getPlChar().getBody().setLinearVelocity(5, p.getPlChar().getBody().getLinearVelocity().y);
			if(!Gdx.input.isKeyPressed(Keys.SPACE) && isGrounded(p.getPlChar())) p.getPlChar().setJumping(false);
			if(Gdx.input. isKeyPressed(Keys.SPACE) && !p.getPlChar().isJumping()){
				if(isGrounded(p.getPlChar())){
					p.getPlChar().getBody().setAwake(true);
					p.getPlChar().getBody().setLinearVelocity(p.getPlChar().getBody().getLinearVelocity().x, 10);
					p.getPlChar().setJumping(true);
				}		
			}
			//TODO implement dropping through platforms here or introduce GroundTime in the contactlistener
			if(Gdx.input.isKeyPressed(Keys.S) && p.getPlChar().getTimeOnGround()>0.2f)
				p.getPlChar().getBody().setAwake(true);
			//capturing point
			/*
			if(Gdx.input.isKeyPressed(Keys.Q) && !capturing){
				for(Contact c: world.getContactList()){
					if(c.getFixtureA()==capturePoints.get(0).getFixture()){
						if(c.getFixtureB()==p.getPlChar().getFixture()){
							System.out.println("capturing");
							capturing=true;
						}
					}
					if(c.getFixtureB()==capturePoints.get(0).getFixture()){
						if(c.getFixtureA()==p.getPlChar().getFixture()){
							System.out.println("capturing");
							capturing=true;
						}
					}
				}
			}*/
		}
		if(capturing) captureTime+=Gdx.graphics.getDeltaTime();
		if(captureTime>5){
			capturing=false;
			captureTime=0;
			System.out.println("Point captured!");
		}
		
		//Game Logic
		if(!gameEnded){
			deltaTime+=Gdx.graphics.getDeltaTime();
			if(deltaTime>=INTERVAL_POINTS){
				for(CapturePoint cp: capturePoints){
					if(cp.getTeamId()==ID_TEAM_BLUE) pointsBlue++;
					if(cp.getTeamId()==ID_TEAM_RED) pointsRed++;
				}
				if(pointsBlue>=MAX_POINTS || pointsRed>= MAX_POINTS) gameEnded=true;
				deltaTime=0;
			}
		}
		
		//rendering
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.end();
		
		mapRenderer.render(LAYERS_TO_RENDER);
		renderer.render(world, camera.combined);
		world.step(TIME_STEP, ITERATIONS_VELOCITY, ITERATIONS_POSITION);
	}
	
	@Override
	public void dispose() {
		world.dispose();		
		renderer.dispose();
		map.dispose();
	}

	@Override
	public void resize(int width, int height) {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	
	//////////////////////
	//	intern methods	//
	//////////////////////
	
	private void initPlayer(User user, int team, PlayableCharacter character){
		//TODO set the correct starting position depending on the map
		Player player = new Player(user, character, team);
		if(team==ID_TEAM_BLUE) teamBlue.add(player);
		if(team==ID_TEAM_RED) teamRed.add(player);
		
		player.getPlChar().setBody(world.createBody(player.getPlChar().getBodyDef()));
		player.getPlChar().dispose();
		player.getPlChar().setFixture(
				player.getPlChar().getBody()
				.createFixture(player.getPlChar().getFixtureDef()));
	}
	
	private boolean isGrounded(MoveableEntity entity){
		if(entity==null) throw new NullPointerException(EXCEPTION_NULL_ENTITY);
		//calculate y offset
		Vector2 tmpVector = new Vector2();
		PolygonShape tmpShape= (PolygonShape) entity.getFixture().getShape();
		tmpShape.getVertex(0, tmpVector);
		
		for(Contact c: world.getContactList()){
			if(c.isTouching()
					&& (c.getFixtureA()==entity.getFixture()
					|| c.getFixtureB()==entity.getFixture())){
				Vector2 pos = entity.getBody().getPosition();
				WorldManifold manifold = c.getWorldManifold();
				boolean below = false;
				if(Math.abs(c.getWorldManifold().getNormal().x)<Math.abs(c.getWorldManifold().getNormal().y)){
					below=true;
					for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
						below &= (manifold.getPoints()[j].y < pos.y - Math.abs(tmpVector.y));
					}
					if((c.getFixtureA()==entity.getFixture() && c.getFixtureB().getUserData()!=null && c.getFixtureB().getUserData().equals("player")))
						below=false;
					if((c.getFixtureB()==entity.getFixture() && c.getFixtureA().getUserData()!=null && c.getFixtureA().getUserData().equals("player")))
						below=false;
					if(below) return true;
				}
			}
		}
		return false;
	}
	
	//////////////////////
	//	public methods	//
	//////////////////////

	public void addPacket(SFSObject packet){
		if(packet == null) throw new NullPointerException(EXCEPTION_NULL_PACKET);
		packetQueue.add(packet);
	}
	
	//TODO overhaul
		/*
		public void joinGame(Player player, int team){
			if(player == null) throw new NullPointerException(EXCEPTION_NULL_PLAYER);
			if(team!=ID_TEAM_BLUE && team!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAM_ID);
			
			if(team==ID_TEAM_BLUE){
				if(teamBlue.size()>=teamSize) throw new IllegalStateException(EXCEPTION_ILLEGAL_TEAM_FULL);
				else {
					teamBlue.add(player);
					initPlayer(player);
				}
				
			}else{
				if(teamRed.size()>=teamSize) throw new IllegalStateException(EXCEPTION_ILLEGAL_TEAM_FULL);
				else {
					teamRed.add(player);
					initPlayer(player);
				}
			}
		}*/	
}
