package de.htw.saarland.gamedev.nap.data;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.box2d.editor.BodyEditorLoader;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.data.entities.StaticEntity;
import de.htw.saarland.gamedev.nap.data.generic.KeyValueFile;

public class GameWorld {
	
	private static final String KEY_TIME_TO_CAPTUREPOINT = "time_to_capturepoint";
	private static final String KEY_POINTS_PER_INTERVAL = "points_per_interval";
	private static final String KEY_POINTS_TO_WIN = "points_to_win";
	private static final String KEY_INTERVAL_POINTS = "interval_points";
	private static final String KEY_RESPAWN_MULTIPLICATOR = "respawn_multiplicator";
	private static final String KEY_GRAVITY_X = "gravity_x";
	private static final String KEY_GRAVITY_Y = "gravity_y";
	
	private final static float PIXELS_TO_METERS = 1/96f;
	//tile ids
	private final static int ID_TILE_PLATFORM_ONE = -1;
	private final static int ID_TILE_PLATFORM_TWO = 2;
	private final static int ID_TILE_SPAWN_POINT_BLUE = 12;
	private final static int ID_TILE_SPAWN_POINT_RED = 13;
	private final static int ID_TILE_CREEP_DMG = 14;
	private final static int ID_TILE_CREEP_RES = 15;
	//fixture user data
	public final static String USERDATA_FIXTURE_CAPTUREPOINT = "capturepoint";
	public final static String USERDATA_FIXTURE_PLATFORM_ONE = "platform_one";
	public final static String USERDATA_FIXTURE_PLATFORM_TWO = "platform_two";
	public final static String USERDATA_FIXTURE_SPAWNPOINT_BLUE = "spawnpoint_blue";
	public final static String USERDATA_FIXTURE_SPAWNPOINT_RED = "spawnpoint_red";
	public static final String USERDATA_FIXTURE_WORLD = "world";
	//Exceptions
	private final static String EXCEPTION_ILLEGAL_PLATFORM_ID = "Platform Id doesn't exist!";
	private final static String EXCEPTION_ILLEGAL_TEAM_ID = "Team Id is not existing!";
	private final static String EXCEPTION_NULL_VECTOR = "Vector object is null!";
	
	private Array<CapturePoint> capturePoints;
	private TiledMap tiledMap;
	private Array<StaticEntity> platforms;
	private SpawnPoint spawnPointBlue;
	private SpawnPoint spawnPointRed;
	
	private World world;
	private Body mapBody;
	private int currentId;
	private boolean idReturned;
	private int width;
	private int height;
	
	public class WorldInfo {
		public final float timeToCapture;
		public final int pointsPerInterval;
		public final int pointsToWin;
		public final float intervalPoints;
		public final float respawnMultiplicator;
		public final Vector2 gravity;
		
		public WorldInfo(float timeToCapture, int pointsPerInterval, int pointsToWin, float intervalPoints, float respawnMultiplicator, Vector2 gravity) {
			this.timeToCapture = timeToCapture;
			this.pointsPerInterval = pointsPerInterval;
			this.pointsToWin = pointsToWin;
			this.intervalPoints = intervalPoints;
			this.respawnMultiplicator = respawnMultiplicator;
			this.gravity = gravity;
		}
	}
	
	public final WorldInfo worldInfo;
	
	public GameWorld(World world, String mapPath, String metaPath, int currentId, TmxMapLoader loader){
		this.world=world;
		this.currentId=currentId;
		this.idReturned=false;
		
		platforms = new Array<StaticEntity>();
		capturePoints = new Array<CapturePoint>();
		width=-1;
		height=-1;
		
		try {
			KeyValueFile values = new KeyValueFile(metaPath);
			
			values.load();
			
			float timeToCapture = values.getValueFloat(KEY_TIME_TO_CAPTUREPOINT);
			int pointsPerInterval = values.getValueInt(KEY_POINTS_PER_INTERVAL);
			int pointsToWin = values.getValueInt(KEY_POINTS_TO_WIN);
			float intervalPoints = values.getValueFloat(KEY_INTERVAL_POINTS);
			float respawnMultiplicator = values.getValueFloat(KEY_RESPAWN_MULTIPLICATOR);
			float gravityX = values.getValueFloat(KEY_GRAVITY_X);
			float gravityY = values.getValueFloat(KEY_GRAVITY_Y);
			
			worldInfo = new WorldInfo(
					timeToCapture,
					pointsPerInterval,
					pointsToWin,
					intervalPoints,
					respawnMultiplicator,
					new Vector2(gravityX, gravityY)
				);
		}
		catch (IOException e) {
			throw new RuntimeException(e); //crash program cause of unrepairable error
		}
		
		world.setGravity(worldInfo.gravity);
		
		initMap(mapPath, loader);
	}
	
	private void initCapturePoint(Vector2 position){
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_VECTOR);
		PolygonShape captureShape = new PolygonShape();
		captureShape.setAsBox(.5f, .5f);
		SensorEntity capturePoint = new SensorEntity(world, captureShape, position.x+.5f, position.y+.5f, currentId++);
		capturePoint.setBody(world.createBody(capturePoint.getBodyDef()));
		capturePoint.setFixture(capturePoint.getBody().createFixture(capturePoint.getFixtureDef()));
		capturePoint.getFixture().setUserData(USERDATA_FIXTURE_CAPTUREPOINT);
		CapturePoint cp = new CapturePoint(capturePoint, worldInfo);
		capturePoints.add(cp);
	}
	
	private void initCapturePoint(float x, float y){
		initCapturePoint(new Vector2(x,y));
	}
	
	private void initMap(String mapName, TmxMapLoader loader){
			
		tiledMap = loader.load(mapName+".tmx");
		width=tiledMap.getProperties().get("width", Integer.class)
				* tiledMap.getProperties().get("tilewidth", Integer.class);
		height=tiledMap.getProperties().get("height", Integer.class)
				* tiledMap.getProperties().get("tileheight", Integer.class);
		//create bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(0,0);
		//create fixtureDef
		FixtureDef fDef = new FixtureDef();
		fDef.density=1;
		fDef.friction=1f;
		//create world body
		mapBody = world.createBody(bodyDef);
		//create world fixture
		BodyEditorLoader bLoader = new BodyEditorLoader(new FileHandle(mapName+".json"));
		bLoader.attachFixture(mapBody, "map", fDef
				,width*PIXELS_TO_METERS);
		for(Fixture f: mapBody.getFixtureList())
			f.setUserData(USERDATA_FIXTURE_WORLD);
		
		//check for meta tiles
		//platforms
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(1);
		for(int i=0; i<layer.getWidth(); i++){
			for(int j=0; j<layer.getHeight(); j++){
				//check for two way platforms
				if(layer.getCell(i, j)!=null)	
					initPlatform(i,j+1, ID_TILE_PLATFORM_TWO);
			}
		}
		//capturepoints
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(2);
		for(int i=0; i<layer.getWidth(); i++){
			for(int j=0; j<layer.getHeight(); j++){
				if(layer.getCell(i, j)!=null)	
					initCapturePoint(i,j);
			}
		}
		//spawnpoints
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(3);
		for(int i=0; i<layer.getWidth(); i++){
			for(int j=0; j<layer.getHeight(); j++){
				if(layer.getCell(i, j)!=null && layer.getCell(i, j).getTile().getId()==ID_TILE_SPAWN_POINT_BLUE)	
					initSpawnPoint(i,j, PlayableCharacter.ID_TEAM_BLUE);
				if(layer.getCell(i, j)!=null && layer.getCell(i, j).getTile().getId()==ID_TILE_SPAWN_POINT_RED)	
					initSpawnPoint(i,j, PlayableCharacter.ID_TEAM_RED);
			}
		}
		//creeps
		//TODO implement Creeps
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(4);
		for(int i=0; i<layer.getWidth(); i++){
			for(int j=0; j<layer.getHeight(); j++){
				if(layer.getCell(i, j)!=null && layer.getCell(i, j).getTile().getId()==ID_TILE_CREEP_DMG)	
					//initNpc(npc)
					;
				if(layer.getCell(i, j)!=null && layer.getCell(i, j).getTile().getId()==ID_TILE_CREEP_RES)
					//initNpc(npc)
					;
			}
		}
			
	}
	
	private void initNpc(NPC npc){
		//TODO set the correct starting position depending on the map
		npc.setBody(world.createBody(npc.getBodyDef()));
		npc.getBody().createFixture(npc.getFixtureDef());
	}
	
	private void initPlatform(Vector2 position, int type){
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_VECTOR);
		if(type!=ID_TILE_PLATFORM_ONE && type!=ID_TILE_PLATFORM_TWO) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_PLATFORM_ID);
		ChainShape platformShape = new ChainShape();
		StaticEntity platform;
		platformShape.createChain(new Vector2[]{new Vector2(0,0), new Vector2(1,0)});
		platform = new StaticEntity(world, platformShape, 1f, position, currentId++);
		platform.setBody(world.createBody(platform.getBodyDef()));
		platform.setFixture(platform.getBody().createFixture(platform.getFixtureDef()));
		if(type==ID_TILE_PLATFORM_ONE) platform.getFixture().setUserData(USERDATA_FIXTURE_PLATFORM_ONE);
		if(type==ID_TILE_PLATFORM_TWO) platform.getFixture().setUserData(USERDATA_FIXTURE_PLATFORM_TWO);
		platforms.add(platform);
	}
	
	private void initPlatform(float x, float y, int type){
		initPlatform(new Vector2(x,y), type);
	}
	
	private void initSpawnPoint(Vector2 position, int team){
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_VECTOR);
		if(team!=PlayableCharacter.ID_TEAM_BLUE && team!=PlayableCharacter.ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAM_ID);
		PolygonShape spawnShape = new PolygonShape();
		spawnShape.setAsBox(.5f, .5f);
		SensorEntity entity;
		//TODO position
		if(team==PlayableCharacter.ID_TEAM_BLUE){
			entity = new SensorEntity(world, spawnShape, position.x+.5f, position.y+.5f, currentId++);
			entity.setBody(world.createBody(entity.getBodyDef()));
			entity.setFixture(entity.getBody().createFixture(entity.getFixtureDef()));
			entity.getFixture().setUserData(USERDATA_FIXTURE_SPAWNPOINT_BLUE);
			spawnPointBlue= new SpawnPoint(entity, PlayableCharacter.ID_TEAM_BLUE);
		}else{
			entity = new SensorEntity(world, spawnShape, position.x+.5f, position.y+.5f, currentId++);
			entity.setBody(world.createBody(entity.getBodyDef()));
			entity.setFixture(entity.getBody().createFixture(entity.getFixtureDef()));
			entity.getFixture().setUserData(USERDATA_FIXTURE_SPAWNPOINT_RED);
			spawnPointRed= new SpawnPoint(entity, PlayableCharacter.ID_TEAM_RED);
		}
	}
	
	private void initSpawnPoint(float x, float y, int type){
		initSpawnPoint(new Vector2(x,y), type);
	}
	
	//getter / setter
	
	public Array<CapturePoint> getCapturePoints(){
		return capturePoints;
	}
	
	public int getCurrentId(){
		if (idReturned) return -1;
		idReturned=true;
		return currentId;
	}
	
	public Body getMapBody(){
		return mapBody;
	}
	
	public Array<StaticEntity> getPlatforms(){
		return platforms;
	}
	
	public SpawnPoint getSpawnPointBlue(){
		return spawnPointBlue;
	}
	
	public SpawnPoint getSpawnPointRed(){
		return spawnPointRed;
	}
	
	public TiledMap getTiledMap(){
		return tiledMap;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
}
