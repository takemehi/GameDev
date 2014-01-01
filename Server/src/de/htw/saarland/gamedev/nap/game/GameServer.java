package de.htw.saarland.gamedev.nap.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.Mage;
import de.htw.saarland.gamedev.nap.data.NPC;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.SpawnPoint;
import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
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
	SpriteBatch batch;
	OrthogonalTiledMapRenderer mapRenderer;
	LinkedList<SensorEntity> fireBalls;
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public GameServer(String mapName, int teamSize, Array<Player> team1, Array<Player> team2){
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
		
		//initialize npcs
		
		//Test stuff
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
		camera.position.set(Gdx.graphics.getWidth()/40-2, Gdx.graphics.getHeight()/40-2, 0);
		camera.update();
		mapRenderer = new OrthogonalTiledMapRenderer(map, PIXELS_TO_METERS);
		mapRenderer.setView(camera);
		batch = new SpriteBatch();
		fireBalls = new LinkedList<SensorEntity>();
		
		/*
		BodyDef bd = new BodyDef();
		bd.position.set(new Vector2(2,6));
		FixtureDef fd1 = new FixtureDef();
		fd1.isSensor=true;
		fd1.shape=new WarriorShape();
		FixtureDef fd2=new FixtureDef();
		CircleShape cs = new CircleShape();
		cs.setRadius(0.2f);
		cs.setPosition(new Vector2(0.2f,0));
		fd2.isSensor=true;
		fd2.shape=cs;
		
		Body body = world.createBody(bd);
		body.createFixture(fd1);
		body.createFixture(fd2);*/
		
		/*Warrior warrior = new Warrior(new Vector2(2,2), currentId++);
		warrior.setBody(world.createBody(warrior.getBodyDef()));
		warrior.setFixture(warrior.getBody().createFixture(warrior.getFixtureDef()));
		warrior.setMeleeSensorFixture(warrior.getBody().createFixture(warrior.getMeleeSensorFixtureDef()));
		Player player = new Player(null, warrior, ID_TEAM_BLUE);*/
		Mage mage = new Mage(new Vector2(2,2), PlayableCharacter.ID_TEAM_BLUE, currentId++);
		mage.setBody(world.createBody(mage.getBodyDef()));
		mage.setFixture(mage.getBody().createFixture(mage.getFixtureDef()));
		mage.setHealth(20);
		Player player = new Player(null, mage);
		teamBlue.add(player);
		
		Mage mage1 = new Mage(new Vector2(4,4), PlayableCharacter.ID_TEAM_BLUE, currentId++);
		mage1.setBody(world.createBody(mage1.getBodyDef()));
		mage1.setFixture(mage1.getBody().createFixture(mage1.getFixtureDef()));
		mage1.setHealth(20);
		Player player1 = new Player(null, mage1);
		teamBlue.add(player1);
		
		Mage mage2 = new Mage(new Vector2(6,4 ), PlayableCharacter.ID_TEAM_RED, currentId++);
		mage2.setBody(world.createBody(mage2.getBodyDef()));
		mage2.setFixture(mage2.getBody().createFixture(mage2.getFixtureDef()));
		mage2.setHealth(100);
		Player player2 = new Player(null, mage2);
		teamRed.add(player2);
	}		
	
	@Override
	public void render() {		
		//TODO add red team or change from seperate lists to one global list		
		//iterate through all the players
		//for(Player p: teamBlue){
		for(int i=0; i<1; i++){
			PlayableCharacter plCh=teamBlue.get(i).getPlChar();
			//PlayableCharacter plCh = p.getPlChar();
			Vector3 mouseCoords3 = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			camera.unproject(mouseCoords3);
			Vector2 mouseCoords = new Vector2(mouseCoords3.x, mouseCoords3.y);
			
			//Attacks
			if(!Gdx.input.isButtonPressed(Keys.LEFT)){
				plCh.setAttacking(false);
			}
			if(Gdx.input.isButtonPressed(Keys.LEFT)){
				plCh.setAttacking(true);
			}
			if(plCh.isSwinging()){
				if (plCh.getSwingTime()==0 && plCh.getAttacking()) {
					//TODO call attack method
					switch(plCh.getCharacterClass()){
					case PlayableCharacter.ID_MAGE:
						SensorEntity ball;
						CircleShape shape = new CircleShape();
						shape.setRadius(0.1f);
						ball = new SensorEntity(shape, plCh.getBody().getPosition().x, plCh.getBody().getPosition().y, 5);
						ball.setBody(world.createBody(ball.getBodyDef()));
						ball.getFixtureDef().filter.groupIndex=PlayableCharacter.GROUP_TEAM_BLUE;
						ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
						//TODO change to constant
						ball.getFixture().setUserData("fireball");
						ball.getBody().setType(BodyDef.BodyType.DynamicBody);							
						Vector2 direction = mouseCoords.sub(plCh.getBody().getPosition());
						direction = direction.nor();
						Vector2 velocityBall=direction.mul(6);
						ball.getBody().setLinearVelocity(velocityBall);
						fireBalls.add(ball);
						break;
					case PlayableCharacter.ID_WARRIOR:
						break;
					}
					//player orientation
					if(mouseCoords.x > plCh.getBody().getPosition().x)
		 				plCh.getBody().setTransform(plCh.getBody().getPosition(), MathUtils.degreesToRadians*360);
					else
						plCh.getBody().setTransform(plCh.getBody().getPosition(), MathUtils.degreesToRadians*180);
				}
				plCh.setSwingTime(plCh.getSwingTime()+Gdx.graphics.getDeltaTime());
				//TODO proper swingtime constant
				if(plCh.getSwingTime()>=plCh.getMaxSwingTime()){
					plCh.setSwingTime(0);
					if(!plCh.getAttacking()) plCh.setSwinging(false);
				}
			}
			
			//Movement
			if(!isGrounded(plCh)) plCh.setTimeonGround(0);
			else plCh.setTimeonGround(plCh.getTimeOnGround()+Gdx.graphics.getDeltaTime());
			
			if(!Gdx.input.isKeyPressed(Keys.A))
				plCh.getBody().setLinearVelocity(0, plCh.getBody().getLinearVelocity().y);
			if(!Gdx.input.isKeyPressed(Keys.D))
				plCh.getBody().setLinearVelocity(0, plCh.getBody().getLinearVelocity().y);
			if(Gdx.input.isKeyPressed(Keys.A)){
				plCh.getBody().setLinearVelocity(-plCh.getMaxVelocity().x, plCh.getBody().getLinearVelocity().y);
				plCh.getBody().setTransform(plCh.getBody().getPosition(), MathUtils.degreesToRadians*180);
			}
			if(Gdx.input.isKeyPressed(Keys.D)){
				plCh.getBody().setLinearVelocity(plCh.getMaxVelocity().x, plCh.getBody().getLinearVelocity().y);
				plCh.getBody().setTransform(plCh.getBody().getPosition(), MathUtils.degreesToRadians*360);
			}
			if(!Gdx.input.isKeyPressed(Keys.SPACE) && isGrounded(plCh)) plCh.setJumping(false);
			if(Gdx.input. isKeyPressed(Keys.SPACE) && !plCh.isJumping()){
				if(isGrounded(plCh)){
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
			}
		}
		
		for(Player p: teamRed){
			//death test
			if(p.getPlChar().getHealth()<=0) p.getPlChar().setFlaggedForDelete(true);
		}
		//Fireball test
		for(SensorEntity s: fireBalls){
			//TODO constant for that value
			if(s.getDistanceTraveled()>100f){
				s.setFlaggedForDelete(true);
			}else{
				s.setDistanceTraveled((s.getPositionOriginal().sub(s.getBody().getPosition()).len()));
				s.getBody().applyForceToCenter(0, 20, true);
			}
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
		
		batch.begin();
		batch.end();
		
		mapRenderer.render(LAYERS_TO_RENDER);
		renderer.render(world, camera.combined);
		world.step(TIME_STEP, ITERATIONS_VELOCITY, ITERATIONS_POSITION);
		//delete entities
		for (Iterator<SensorEntity> it = fireBalls.iterator(); it.hasNext(); ) {
		    SensorEntity s = it.next();
		    if(s.isFlaggedForDelete() && !world.isLocked()){
				fireBalls.remove(s);
				world.destroyBody(s.getBody());
			}
		}

		for(Player p: teamBlue){
			
		}
		for(Player p: teamRed){
			if(p.getPlChar().isFlaggedForDelete()){
				teamRed.removeValue(p, true);
				world.destroyBody(p.getPlChar().getBody());
			}
		}
		
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
	
	private void initPlayer(User user, PlayableCharacter character){
		//TODO set the correct starting position depending on the map
		Player player = new Player(user, character);
		if(character.getTeamId()==PlayableCharacter.ID_TEAM_BLUE) teamBlue.add(player);
		if(character.getTeamId()==PlayableCharacter.ID_TEAM_RED) teamRed.add(player);
		
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
}
