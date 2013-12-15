package de.htw.saarland.gamedev.nap.game;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.box2d.editor.BodyEditorLoader;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.entities.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.entities.NPC;
import de.htw.saarland.gamedev.nap.data.entities.StaticEntity;
import de.htw.saarland.gamedev.nap.data.platforms.OneWayPlatformContactListener;


public class GameServer implements ApplicationListener, InputProcessor {
	
	//exceptions
	private final static String EXCEPTION_NO_PACKET = "Packet object is missing!";
	private final static String EXCEPTION_NO_PLAYER = "Player object is missing!";
	private final static String EXCEPTION_MAP_EMPTY = "Map name is empty!";
	private final static String EXCEPTION_NO_TEAM1 = "Team1 object is missing!";
	private final static String EXCEPTION_NO_TEAM2 = "Team2 object is missing!";
	private final static String EXCEPTION_ILLEGAL_TEAMSIZE = "Teamsize can't be less than 1!";
	private final static String EXCEPTION_TEAM_INVALID = "Team is not existing!";
	private final static String EXCEPTION_TEAM_FULL = "Team is already full!";
	//folders
	private final static String FOLDER_MAPS = "data/maps/";
	//packets processed per tick
	private final static int PACKETS_PER_TICK = 50;
	//world renderer constants
	private final static float TIME_STEP = 1/60f;
	private final static int ITERATIONS_VELOCITY = 6;
	private final static int ITERATIONS_POSITION = 2;
	//world constants
	private final static Vector2 GRAVITY = new Vector2(0, -20);
	//transformation constants
	private final static float PIXELS_TO_METERS = 1/8f;
	
	//input parameters
	private String mapName;
	private TiledMap map;
	private int teamSize;
	private ArrayList<Player> team1;
	private ArrayList<Player> team2;
	//TODO maybe change the type from npc to extended class
	private ArrayList<NPC> npcs;
	
	//internal variables
	private ConcurrentLinkedQueue<SFSObject> packetQueue;	
	private World world;
	private Body mapBody;
	
	//test variables
	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;
	private Vector2 velocity = new Vector2(0,0);
	MoveableEntity ball;
	MoveableEntity player;
	StaticEntity platform;
	
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public GameServer(String mapName, int teamSize, ArrayList<Player> team1, ArrayList<Player> team2){
		if(mapName.trim().length()<1) throw new IllegalArgumentException(EXCEPTION_MAP_EMPTY);
		if(teamSize<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAMSIZE);
		if(team1 == null) throw new NullPointerException(EXCEPTION_NO_TEAM1);
		if(team2 == null) throw new NullPointerException(EXCEPTION_NO_TEAM2);
		
		this.mapName=mapName;
		this.teamSize=teamSize;
		this.team1=team1;
		this.team2=team2;
		
		//box2d stuff
		
		//instantiate variables
		//load stuff from external files
		//load chosen Map	
	}
	
	//////////////////////////
	//	override methods	//
	//////////////////////////
	
	@Override
	public void create() {
		//initialize world
		world = new World(GRAVITY, true);
		//initialize map
		initMap(mapName);
		
		//initialize platforms
		
		//initialize npcs
		
		//initialize the body objects of the players
		for(Player p: team1){
			initPlayer(p);
		}
		
		for(Player p: team2){
			initPlayer(p);
		}
		
		//Test stuff
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 5);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(1f);
		ball = new MoveableEntity(shape, 1, 1, 0, new Vector2(0,1), new Vector2(10,10));
		
		PolygonShape playerShape = new PolygonShape();
		playerShape.setAsBox(1, 2);
		player = new MoveableEntity(playerShape, 1, 0, 0, new Vector2(3,1), new Vector2(10,10));
		
		ChainShape platformShape = new ChainShape();
		platformShape.createChain(new Vector2[]{new Vector2(-8,0), new Vector2(-5,0)});
		platform = new StaticEntity(platformShape, 0.3f, -5f, -23);
		
		
		ball.setBody(world.createBody(ball.getBodyDef()));
		ball.setFixture(ball.getBody().createFixture(ball.getFixtureDef()));
		ball.getFixture().setUserData("p");
		player.setBody(world.createBody(player.getBodyDef()));
		player.setFixture(player.getBody().createFixture(player.getFixtureDef()));
		platform.setBody(world.createBody(platform.getBodyDef()));
		platform.setFixture(platform.getBody().createFixture(platform.getFixtureDef()));
		platform.getFixture().setUserData("platformOne");
		world.setContactListener(new OneWayPlatformContactListener());
		
		Gdx.input.setInputProcessor(this);
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
		
		//recalculate positions and
		//check for dem collisions
		
		
		
		
		
		
		
		
		//check for ability collisions, damage abilities
		
		//calculate hp, apply status effects, recalculate position
		
		//send dat shit
		
		//Test stuff
		Vector2 pos = player.getBody().getPosition();
		if(!Gdx.input.isKeyPressed(Keys.A))
			player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
		if(!Gdx.input.isKeyPressed(Keys.D))
			player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
		if(Gdx.input.isKeyPressed(Keys.A))
			player.getBody().setLinearVelocity(-10, player.getBody().getLinearVelocity().y);
		if(Gdx.input.isKeyPressed(Keys.D))
			player.getBody().setLinearVelocity(10, player.getBody().getLinearVelocity().y);
		if(Gdx.input. isKeyPressed(Keys.SPACE)){
			if(isOnGround(player))
				player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 10);
				//player.getBody().applyLinearImpulse(0, 50, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
		}
		//if(velocity.x == 0)box.setLinearVelocity(0, box.getLinearVelocity().y);
		//if(velocity.y == 0) box.setLinearVelocity(box.getLinearVelocity().x, 0);
			
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.render(world, camera.combined);
		world.step(TIME_STEP, ITERATIONS_VELOCITY, ITERATIONS_POSITION);
	}
	
	@Override
	public void dispose() {
		world.dispose();
		renderer.dispose();		
	}

	@Override
	public void resize(int width, int height) {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode){
		case (Keys.W):
			
			return true;
		case (Keys.S):
			velocity.y=-10;
			return true;
		case (Keys.A):
			velocity.x=-10;
			return true;
		case (Keys.D):
			velocity.x=10;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode){
		case (Keys.W):
			velocity.y=0;
			return true;
		case (Keys.S):
			velocity.y=0;
			return true;
		case (Keys.A):
			velocity.x=0;
			return true;
		case (Keys.D):
			velocity.x=0;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//////////////////////
	//	intern methods	//
	//////////////////////
	
	private void initMap(String mapName){
		
		int mapWidth;
		
		TiledMap map = new TiledMap();
		TmxMapLoader loader = new TmxMapLoader();
		map = loader.load(FOLDER_MAPS+mapName+".tmx");
		mapWidth=map.getProperties().get("width", Integer.class)
				* map.getProperties().get("tilewidth", Integer.class);
		map.dispose();
		
		
		//create bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(-30,-30);
		//create fixtureDef
		FixtureDef fDef = new FixtureDef();
		fDef.density=1;
		fDef.friction=0.3f;
		//create world body
		mapBody = world.createBody(bodyDef);
		
		BodyEditorLoader bLoader = new BodyEditorLoader(new FileHandle(FOLDER_MAPS+mapName+".json"));
		bLoader.attachFixture(mapBody, "Name", fDef
				,mapWidth*PIXELS_TO_METERS);
	}
	
	private void initNpc(NPC npc){
		//TODO set the correct starting position depending on the map
		npc.setBody(world.createBody(npc.getBodyDef()));
		npc.getBody().createFixture(npc.getFixtureDef());
	}
	
	private void initPlayer(Player player){
		//TODO set the correct starting position depending on the map
		player.getPlChar().setBody(world.createBody(player.getPlChar().getBodyDef()));
		player.getPlChar().setFixture(
				player.getPlChar().getBody()
				.createFixture(player.getPlChar().getFixtureDef()));
	}
	
	private boolean isOnGround(MoveableEntity entity){
		
		for(Contact c: world.getContactList()){
			if(c.isTouching()
					&& (c.getFixtureA()==entity.getFixture()
					|| c.getFixtureB()==entity.getFixture())){
				Vector2 pos = entity.getBody().getPosition();
				WorldManifold manifold = c.getWorldManifold();
				boolean below = true;
				for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
				}
				if((c.getFixtureA()==entity.getFixture() && c.getFixtureB().getUserData()!=null && c.getFixtureB().getUserData().equals("p")))
					below=false;
				if((c.getFixtureB()==entity.getFixture() && c.getFixtureA().getUserData()!=null && c.getFixtureA().getUserData().equals("p")))
					below=false;
				if(below) return true;
			}
		}
		
		return false;
	}
	
	//////////////////////
	//	public methods	//
	//////////////////////

	public void addPacket(SFSObject packet){
		if(packet == null) throw new NullPointerException(EXCEPTION_NO_PACKET);
		packetQueue.add(packet);
	}
	
	public void joinGame(Player player, int whichTeam){
		if(player == null) throw new NullPointerException(EXCEPTION_NO_PLAYER);
		if(whichTeam<0 || whichTeam>1) throw new IllegalArgumentException(EXCEPTION_TEAM_INVALID);
		
		if(whichTeam==0){
			if(team1.size()>=teamSize) throw new IllegalStateException(EXCEPTION_TEAM_FULL);
			else {
				team1.add(player);
				initPlayer(player);
			}
			
		}else{
			if(team2.size()>=teamSize) throw new IllegalStateException(EXCEPTION_TEAM_FULL);
			else {
				team2.add(player);
				initPlayer(player);
			}
		}
	}

	
}
