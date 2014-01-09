package de.htw.saarland.gamedev.nap.data;

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

public class GameWorld {
	
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
	
	public GameWorld(World world, String mapPath, int currentId, TmxMapLoader loader){
		this.world=world;
		this.currentId=currentId;
		this.idReturned=false;
		
		platforms = new Array<StaticEntity>();
		capturePoints = new Array<CapturePoint>();
		
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
		CapturePoint cp = new CapturePoint(capturePoint);
		capturePoints.add(cp);
	}
	
	private void initCapturePoint(float x, float y){
		initCapturePoint(new Vector2(x,y));
	}
	
	private void initMap(String mapName, TmxMapLoader loader){
			
		int mapWidth;
		tiledMap = loader.load(mapName+".tmx");
		mapWidth=tiledMap.getProperties().get("width", Integer.class)
				* tiledMap.getProperties().get("tilewidth", Integer.class);
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
				,mapWidth*PIXELS_TO_METERS);
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
	//TODO implement
	public void update(){
		
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

}
