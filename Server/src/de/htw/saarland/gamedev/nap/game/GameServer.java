package de.htw.saarland.gamedev.nap.game;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.CharacterClass;
import de.htw.saarland.gamedev.nap.data.MoveableEntity;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.StaticEntity;


public class GameServer implements ApplicationListener, InputProcessor {
	
	//exceptions
	private final static String EXCEPTION_NO_MAP = "Map object is missing!";
	private final static String EXCEPTION_NO_TEAM1 = "Team1 object is missing!";
	private final static String EXCEPTION_NO_TEAM2 = "Team2 object is missing!";
	private final static String EXCEPTION_ILLEGAL_TEAMSIZE = "Teamsize can't be less than 1!";
	//packets processed per tick
	private final static int PACKETS_PER_TICK = 50;
	//world renderer constants
	private final static float TIME_STEP = 1/60f;
	private final static int VELOCITY_ITERATIONS = 6;
	private final static int POSITION_ITERATIONS = 2;
	//world constants
	private final static Vector2 GRAVITY = new Vector2(0, -9.81f);
	
	//input parameters
	private String map;
	private int teamSize;
	private ArrayList<Player> team1;
	private ArrayList<Player> team2;
	
	//internal variables
	private ConcurrentLinkedQueue<SFSObject> packetQueue;	
	private World world;
	
	//test variables
	private Body box;
	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;
	
	//////////////////////
	//	constructors	//
	//////////////////////
	
	public GameServer(String map, int teamSize, ArrayList<Player> team1, ArrayList<Player> team2){
		if(map == null) throw new NullPointerException(EXCEPTION_NO_MAP);
		if(teamSize<1) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAMSIZE);
		if(team1 == null) throw new NullPointerException(EXCEPTION_NO_TEAM1);
		if(team2 == null) throw new NullPointerException(EXCEPTION_NO_TEAM2);
		this.map=map;
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
		
		//Test stuff
		world = new World(GRAVITY, true);
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);
		
		ChainShape groundShape = new ChainShape();
		Vector2 groundDim[] = {new Vector2(-50, 0), new Vector2(50, 0)};
		groundShape.createChain(groundDim);
		StaticEntity ground = new StaticEntity(groundShape, 1, new Vector2(0,0));
		
		CircleShape shape = new CircleShape();
		shape.setRadius(1f);
		MoveableEntity ent = new MoveableEntity(shape, 1, 1, 1, 1, new Vector2(0,3));
		
		PolygonShape playerShape = new PolygonShape();
		playerShape.setAsBox(1, 2);
		PlayableCharacter player = new PlayableCharacter(playerShape, 0.1f, 0.1f, 0.75f, 0f, new Vector2(-3, 1), new CharacterClass());
		
		world.createBody(ground.getBodyDef()).createFixture(ground.getFixtureDef());
		world.createBody(ent.getBodyDef()).createFixture(ent.getFixtureDef());
		box = world.createBody(player.getBodyDef());
		box.createFixture(player.getFixtureDef());
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	//TODO AI, capture points
	
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.render(world, camera.combined);
		world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}

	@Override
	public void pause() {
		//pausing is for pussies
		//thus not supported!
		
	}

	@Override
	public void resume() {
		//see above
	}

	@Override
	public void dispose() {
		world.dispose();
		renderer.dispose();		
	}
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode){
		case (Keys.W):
			box.applyForceToCenter(new Vector2(0, 100), true);
			return true;
		case (Keys.S):
			box.applyForceToCenter(new Vector2(0, -100), true);
			return true;
		case (Keys.A):
			box.applyForceToCenter(new Vector2(-100, 0), true);
			return true;
		case (Keys.D):
			box.applyForceToCenter(new Vector2(100, 0), true);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode){
		case (Keys.W):
			box.applyForceToCenter(new Vector2(0, 0), true);
			return true;
		case (Keys.S):
			box.applyForceToCenter(new Vector2(0, 0), true);
			return true;
		case (Keys.A):
			box.applyForceToCenter(new Vector2(0, 0), true);
			return true;
		case (Keys.D):
			box.applyForceToCenter(new Vector2(0, 0), true);
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
	//	own methods		//
	//////////////////////

	/**
	 * This method accepts incoming packets from players
	 * 
	 * @param packet Packet with recent player action informations
	 */
	public void addPacket(SFSObject packet){
		if(packet == null) throw new NullPointerException();
		packetQueue.add(packet);
	}

	
}
