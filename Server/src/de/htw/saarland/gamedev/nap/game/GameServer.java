package de.htw.saarland.gamedev.nap.game;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.NPC;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.SpawnPoint;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.entities.StaticEntity;

public class GameServer implements ApplicationListener {
	
	//exceptions
	private final static String EXCEPTION_ILLEGAL_MAP = "Map files are missing!";
	private final static String EXCEPTION_ILLEGAL_MAP_EMPTY = "Map name is empty!";
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
	public final static float TIME_STEP = 1/60f;
	public final static int ITERATIONS_VELOCITY = 6;
	public final static int ITERATIONS_POSITION = 2;
	//world constants
	public final static Vector2 GRAVITY = new Vector2(0, -20);
	//others
	public final static float PIXELS_TO_METERS = 1/96f;
	private final static int MAX_POINTS = 200;
	private final static float INTERVAL_POINTS = 5.0f;
	public final static int[] LAYERS_TO_RENDER = {0,1,2};
	private static final float INTERVAL_REGEN_SPAWN = 0.5f;	
	
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
	private Array <SFSUser> userBlue;
	private Array <SFSUser> userRed;
	private int charactersBlue[];
	private int charactersRed[];
	protected Array<Player> teamBlue;
	protected Array<Player> teamRed;
	private Array<CapturePoint> capturePoints;
	private Array<StaticEntity> platforms;
	boolean capturing = false;
	float captureTime = 0;
	int currentId;
	int pointsBlue;
	int pointsRed;
	boolean gameEnded;
	float deltaTime;
	float spawnRegTime;
	
	
	//test variables
	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;
	//rendering test
	OrthogonalTiledMapRenderer mapRenderer;
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public GameServer(String mapName, int teamSize, Array <SFSUser> userBlue, Array <SFSUser> userRed, int[] charactersBlue, int[] charactersRed){
		if(mapName.trim().length()<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_MAP_EMPTY);
		if(teamSize<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAMSIZE);
		if(userBlue == null) throw new NullPointerException(EXCEPTION_NULL_TEAM1);
		if(userRed == null) throw new NullPointerException(EXCEPTION_NULL_TEAM2);
		if(!new File(FOLDER_MAPS+mapName+".tmx").exists() || !new File(FOLDER_MAPS+mapName+".json").exists())
			throw new IllegalArgumentException(EXCEPTION_ILLEGAL_MAP);
		
		this.mapName=mapName;
		this.teamSize=teamSize;
		this.userBlue=userBlue;
		this.userRed=userRed;
		this.charactersBlue=charactersBlue;
		this.charactersRed=charactersRed;
		this.teamBlue = new Array<Player>();
		this.teamRed =new Array<Player>();
		//TODO remove
		this.userBlue=new Array<SFSUser>();
		this.userRed=new Array<SFSUser>();
		platforms=new Array<StaticEntity>();
		capturePoints=new Array<CapturePoint>();
		currentId=0;
		gameEnded=false;
		deltaTime=0;
		spawnRegTime=0;
	}
	
	//////////////////////////
	//	override methods	//
	//////////////////////////
	
	@Override
	public void create() {
		//initialize world
		world=new World(GRAVITY, true);
		world.setContactListener(new CustomContactListener(this));
		//initialize gameWorld
		gameWorld=new GameWorld(world, mapName, currentId);
		//initialize map
		this.map=gameWorld.getTiledMap();
		this.currentId=gameWorld.getCurrentId();
		//initialize capturePoints
		this.capturePoints=gameWorld.getCapturePoints();
		//initialize spawnPoints
		this.SpawnPointBlue=gameWorld.getSpawnPointBlue();
		this.SpawnPointRed=gameWorld.getSpawnPointRed();
		//initialize players
		
		for(int i=0; i<charactersBlue.length; i++){
			//TODO change user
			teamBlue.add(new Player(null, world, SpawnPointBlue.getSpawnPoint().getPositionOriginal(), charactersBlue[i], PlayableCharacter.ID_TEAM_BLUE, currentId++));
		}
		for(int i=0; i<charactersRed.length; i++){
			//TODO change user
			teamRed.add(new Player(null, world, SpawnPointRed.getSpawnPoint().getPositionOriginal(), charactersRed[i], PlayableCharacter.ID_TEAM_RED, currentId++));
		}
		//initialize npcs
		
		//Test stuff
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
		camera.position.set(Gdx.graphics.getWidth()/40-2, Gdx.graphics.getHeight()/40-2, 0);
		camera.update();
		mapRenderer = new OrthogonalTiledMapRenderer(map, PIXELS_TO_METERS);
		mapRenderer.setView(camera);
	}		
	
	@Override
	public void render() {		
		//TODO add red team or change from seperate lists to one global list		
		//iterate through all the players
		//for(Player p: teamBlue){
		for(int i=0; i<1; i++){
			PlayableCharacter plCh=teamBlue.get(i).getPlChar();
			//PlayableCharacter plCh = p.getPlChar();
			//get mouse position
			Vector3 mouseCoords3 = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			camera.unproject(mouseCoords3);
			Vector2 mouseCoords = new Vector2(mouseCoords3.x, mouseCoords3.y);
			//player orientation
			if(mouseCoords.x > plCh.getBody().getPosition().x)
				plCh.setOrientation(PlayableCharacter.ORIENTATION_RIGHT);			
			else
				plCh.setOrientation(PlayableCharacter.ORIENTATION_LEFT);
			
			//Attacks
			if(!Gdx.input.isButtonPressed(Keys.LEFT)){
				plCh.setAttacking(false);
			}
			if(Gdx.input.isButtonPressed(Keys.LEFT)){
				plCh.setAttacking(true);
			}
			if(plCh.isSwinging()){
				if (plCh.getSwingTime()==0 && plCh.getAttacking())					
					plCh.getAttack3().start(world, plCh, currentId++, mouseCoords);
					
				plCh.setSwingTime(plCh.getSwingTime()+Gdx.graphics.getDeltaTime());
				//TODO proper swingtime constant
				if(plCh.getSwingTime()>=plCh.getMaxSwingTime()){
					plCh.setSwingTime(0);
					if(!plCh.getAttacking()) plCh.setSwinging(false);
				}
			}
			
			//update attacks
			plCh.getAttack3().update();
			
			//Movement
			if(!isGrounded(plCh)) plCh.setTimeonGround(0);
			else plCh.setTimeonGround(plCh.getTimeOnGround()+Gdx.graphics.getDeltaTime());
			
			if(!Gdx.input.isKeyPressed(Keys.A))
				plCh.getBody().setLinearVelocity(0, plCh.getBody().getLinearVelocity().y);
			if(!Gdx.input.isKeyPressed(Keys.D))
				plCh.getBody().setLinearVelocity(0, plCh.getBody().getLinearVelocity().y);
			if(Gdx.input.isKeyPressed(Keys.A)){
				plCh.getBody().setLinearVelocity(-plCh.getMaxVelocity().x, plCh.getBody().getLinearVelocity().y);
				plCh.setOrientation(PlayableCharacter.ORIENTATION_LEFT);
			}
			if(Gdx.input.isKeyPressed(Keys.D)){
				plCh.getBody().setLinearVelocity(plCh.getMaxVelocity().x, plCh.getBody().getLinearVelocity().y);
				plCh.setOrientation(PlayableCharacter.ORIENTATION_RIGHT);
			}
			if(!Gdx.input.isKeyPressed(Keys.SPACE) && isGrounded(plCh)) plCh.setJumping(false);
			if(Gdx.input. isKeyPressed(Keys.SPACE) && !plCh.isJumping()){
				//if(isGrounded(plCh)){
				if(true){
					plCh.getBody().setAwake(true);
					plCh.getBody().setLinearVelocity(plCh.getBody().getLinearVelocity().x, plCh.getMaxVelocity().y);
					plCh.setJumping(true);
				}		
			}
			if(Gdx.input.isKeyPressed(Keys.S) && plCh.getTimeOnGround()>0.2f)
				plCh.getBody().setAwake(true);
			
			//Spawn regeneration
			spawnRegTime+=Gdx.graphics.getDeltaTime();
			if(plCh.isAtSpawn() && spawnRegTime>=INTERVAL_REGEN_SPAWN){
				plCh.setHealth(plCh.getHealth()+5);
				spawnRegTime=0;
				
			//death test
			if(plCh.getHealth()<=0) plCh.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
		}
		
		for(Player p: teamRed){
			PlayableCharacter plCh = p.getPlChar();
			//Spawn regeneration
			spawnRegTime+=Gdx.graphics.getDeltaTime();
			if(plCh.isAtSpawn() && spawnRegTime>=INTERVAL_REGEN_SPAWN){
				plCh.setHealth(plCh.getHealth()+5);
				spawnRegTime=0;
			}
			//death test
			if(p.getPlChar().getHealth()<=0) p.getPlChar().getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
		}
		//Capturing
		
		//Game Logic
		if(!gameEnded){
			deltaTime+=Gdx.graphics.getDeltaTime();
			if(deltaTime>=INTERVAL_POINTS){
				for(CapturePoint cp: capturePoints){
					if(cp.getTeamId()==PlayableCharacter.ID_TEAM_BLUE) pointsBlue++;
					if(cp.getTeamId()==PlayableCharacter.ID_TEAM_RED) pointsRed++;
				}
				if(pointsBlue>=MAX_POINTS || pointsRed>= MAX_POINTS) gameEnded=true;
				deltaTime=0;
			}
		}
		
		//rendering
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mapRenderer.render(LAYERS_TO_RENDER);
		renderer.render(world, camera.combined);
		world.step(TIME_STEP, ITERATIONS_VELOCITY, ITERATIONS_POSITION);
		
		//delete entities

		for(int i=0; i<teamBlue.size; i++){
			PlayableCharacter character;
			character = teamBlue.get(i).getPlChar();
			if(character.getBody().getUserData()!=null && character.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE)){
				teamBlue.removeIndex(i);
				world.destroyBody(character.getBody());
			}
			character.getAttack1().cleanUp(world);
			character.getAttack3().cleanUp(world);
		}
		for(int i=0; i<teamRed.size; i++){
			PlayableCharacter character;
			character = teamRed.get(i).getPlChar();
			if(character.getBody().getUserData()!=null && character.getBody().getUserData().equals(Entity.USERDATA_BODY_FLAG_DELETE)){
				teamRed.removeIndex(i);
				world.destroyBody(character.getBody());
			}
			character.getAttack1().cleanUp(world);
		}
		//System.out.println(teamBlue.get(0).getPlChar().getHealth()+"\t"+teamRed.get(0).getPlChar().getHealth());
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
					if((c.getFixtureA()==entity.getFixture() 
							&& c.getFixtureB().getUserData()!=null && c.getFixtureB().getUserData().equals(PlayableCharacter.USERDATA_PLAYER)))
						below=false;
					if((c.getFixtureB()==entity.getFixture() 
							&& c.getFixtureA().getUserData()!=null && c.getFixtureA().getUserData().equals(PlayableCharacter.USERDATA_PLAYER)))
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
}
