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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import de.htw.saarland.gamedev.nap.data.Warrior;
import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.entities.StaticEntity;
import de.htw.saarland.gamedev.nap.data.platforms.CustomContactListener;

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
	public static final int ID_TEAM_BLUE = 0;
	public static final int ID_TEAM_RED = 1;
	
	
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
		if (gameWorld.getCurrentId()!=-1)this.currentId= gameWorld.getCurrentId();
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
		
		Warrior warrior = new Warrior(new Vector2(2,2), currentId++);
		warrior.setBody(world.createBody(warrior.getBodyDef()));
		warrior.setFixture(warrior.getBody().createFixture(warrior.getFixtureDef()));
		warrior.setMeleeSensorFixture(warrior.getBody().createFixture(warrior.getMeleeSensorFixtureDef()));
		Player player = new Player(null, warrior, ID_TEAM_BLUE);
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
			if(Gdx.input.isKeyPressed(Keys.A)){
				p.getPlChar().getBody().setLinearVelocity(-p.getPlChar().getMaxVelocity().x, p.getPlChar().getBody().getLinearVelocity().y);
				p.getPlChar().getBody().setTransform(p.getPlChar().getBody().getPosition(), MathUtils.degreesToRadians*180);
			}
			if(Gdx.input.isKeyPressed(Keys.D)){
				p.getPlChar().getBody().setLinearVelocity(p.getPlChar().getMaxVelocity().x, p.getPlChar().getBody().getLinearVelocity().y);
				p.getPlChar().getBody().setTransform(p.getPlChar().getBody().getPosition(), MathUtils.degreesToRadians*360);
			}
			if(!Gdx.input.isKeyPressed(Keys.SPACE) && isGrounded(p.getPlChar())) p.getPlChar().setJumping(false);
			if(Gdx.input. isKeyPressed(Keys.SPACE) && !p.getPlChar().isJumping()){
				if(isGrounded(p.getPlChar())){
					p.getPlChar().getBody().setAwake(true);
					p.getPlChar().getBody().setLinearVelocity(p.getPlChar().getBody().getLinearVelocity().x, p.getPlChar().getMaxVelocity().y);
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
		//Capturing
		if(capturing) captureTime+=Gdx.graphics.getDeltaTime();
		if(captureTime>5){
			capturing=false;
			captureTime=0;
			System.out.println("Point captured!");
		}
		
		//Attacks
		//TODO add in loop
		Warrior w = (Warrior)teamBlue.get(0).getPlChar();
		if(!Gdx.input.isButtonPressed(Keys.LEFT)){
			w.setAttacking(false);
		}
		if(Gdx.input.isButtonPressed(Keys.LEFT)){
			w.setAttacking(true);
		}
		if(w.isSwinging()){
			if (w.getSwingTime()==0 && w.getAttacking()) {
				System.out.println("attack");
				//player orientation
				Vector3 coords = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
				camera.unproject(coords);
				float angle = w.getBody().getAngle();
				if(coords.x > w.getBody().getPosition().x)
					w.getBody().setTransform(w.getBody().getPosition(), MathUtils.degreesToRadians*360);
				else
					w.getBody().setTransform(w.getBody().getPosition(), MathUtils.degreesToRadians*180);
				//w.getBody().setTransform(w.getBody().getPosition(), angle);
			}
			w.setSwingTime(w.getSwingTime()+Gdx.graphics.getDeltaTime());
			//TODO proper swingtime constant
			if(w.getSwingTime()>=w.TIME_SWING){
				w.setSwingTime(0);
				if(!w.getAttacking()) w.setSwinging(false);
			}
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
