package de.htw.saarland.gamedev.nap.data;

import java.util.ArrayList;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.htw.saarland.gamedev.nap.box2d.editor.BodyEditorLoader;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.data.entities.StaticEntity;

public class GameWorld {
	
	private final static String FOLDER_MAPS = "data/maps/";
	private final static float PIXELS_TO_METERS = 1/96f;
	//IDs
	private static final int ID_TEAM_BLUE = 0;
	private static final int ID_TEAM_RED = 1;
	private final static int ID_TILE_PLATFORM_ONE = -1;
	private final static int ID_TILE_PLATFORM_TWO = 2;
	private final static int ID_TILE_SPAWN_POINT_BLUE = 12;
	private final static int ID_TILE_SPAWN_POINT_RED = 13;
	private final static int ID_TILE_CREEP_DMG = 14;
	private final static int ID_TILE_CREEP_RES = 15;
	//Exceptions
	private final static String EXCEPTION_ILLEGAL_PLATFORM_ID = "Platform Id doesn't exist!";
	private final static String EXCEPTION_ILLEGAL_TEAM_ID = "Team Id is not existing!";
	private final static String EXCEPTION_NULL_VECTOR = "Vector object is null!";
	
	private ArrayList<CapturePoint> capturePoints;
	private TiledMap tiledMap;
	private ArrayList<StaticEntity> platforms;
	private SpawnPoint spawnPointBlue;
	private SpawnPoint spawnPointRed;
	
	private World world;
	private Body mapBody;
	private int currentId;
	
	public GameWorld(World world, String mapName, int currentId){
		this.world=world;
		this.currentId=currentId;
		
		platforms = new ArrayList<StaticEntity>();
		capturePoints = new ArrayList<CapturePoint>();
		
		initMap(mapName);
	}
	
	private void initCapturePoint(Vector2 position){
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_VECTOR);
		PolygonShape captureShape = new PolygonShape();
		captureShape.setAsBox(.5f, .5f);
		SensorEntity capturePoint = new SensorEntity(captureShape, position.x+.5f, position.y+.5f, currentId++);
		capturePoint.setBody(world.createBody(capturePoint.getBodyDef()));
		capturePoint.setFixture(capturePoint.getBody().createFixture(capturePoint.getFixtureDef()));
		capturePoint.getFixture().setUserData("capturePoint");
		CapturePoint cp = new CapturePoint(capturePoint);
		capturePoints.add(cp);
	}
	
	private void initCapturePoint(float x, float y){
		initCapturePoint(new Vector2(x,y));
	}
	
	private void initMap(String mapName){
			
		int mapWidth;
		tiledMap = new TiledMap();
		TmxMapLoader loader = new TmxMapLoader();
		tiledMap = loader.load(FOLDER_MAPS+mapName+".tmx");
		mapWidth=tiledMap.getProperties().get("width", Integer.class)
				* tiledMap.getProperties().get("tilewidth", Integer.class);
		//create bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(0,0);
		//create fixtureDef
		FixtureDef fDef = new FixtureDef();
		fDef.density=1;
		fDef.friction=0.3f;
		//create world body
		mapBody = world.createBody(bodyDef);
		//create world fixture
		BodyEditorLoader bLoader = new BodyEditorLoader(new FileHandle(FOLDER_MAPS+mapName+".json"));
		bLoader.attachFixture(mapBody, "map", fDef
				,mapWidth*PIXELS_TO_METERS);
		
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
					initSpawnPoint(i,j, ID_TEAM_BLUE);
				if(layer.getCell(i, j)!=null && layer.getCell(i, j).getTile().getId()==ID_TILE_SPAWN_POINT_RED)	
					initSpawnPoint(i,j, ID_TEAM_RED);
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
		platform = new StaticEntity(platformShape, 0.3f, position, currentId++);
		platform.setBody(world.createBody(platform.getBodyDef()));
		platform.setFixture(platform.getBody().createFixture(platform.getFixtureDef()));
		if(type==ID_TILE_PLATFORM_ONE) platform.getFixture().setUserData("platformOne");
		if(type==ID_TILE_PLATFORM_TWO) platform.getFixture().setUserData("platformTwo");
		platforms.add(platform);
	}
	
	private void initPlatform(float x, float y, int type){
		initPlatform(new Vector2(x,y), type);
	}
	
	private void initSpawnPoint(Vector2 position, int team){
		if(position==null) throw new NullPointerException(EXCEPTION_NULL_VECTOR);
		if(team!=ID_TEAM_BLUE && team!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAM_ID);
		PolygonShape spawnShape = new PolygonShape();
		spawnShape.setAsBox(.5f, .5f);
		SensorEntity entity;
		//TODO position
		if(team==ID_TEAM_BLUE){
			entity = new SensorEntity(spawnShape, position.x+.5f, position.y+.5f, currentId++);
			entity.setBody(world.createBody(entity.getBodyDef()));
			entity.setFixture(entity.getBody().createFixture(entity.getFixtureDef()));
			entity.getFixture().setUserData("spawnBlue");
			spawnPointBlue= new SpawnPoint(entity, ID_TEAM_BLUE);
		}else{
			entity = new SensorEntity(spawnShape, position.x+.5f, position.y+.5f, currentId++);
			entity.setBody(world.createBody(entity.getBodyDef()));
			entity.setFixture(entity.getBody().createFixture(entity.getFixtureDef()));
			entity.getFixture().setUserData("spawnRed");
			spawnPointRed= new SpawnPoint(entity, ID_TEAM_RED);
		}
	}
	
	private void initSpawnPoint(float x, float y, int type){
		initSpawnPoint(new Vector2(x,y), type);
	}
	
	//getter / setter
	
	public TiledMap getTiledMap(){
		return tiledMap;
	}

}