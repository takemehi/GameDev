package de.htw.saarland.gamedev.nap.game;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.smartfoxserver.v2.entities.data.SFSObject;


public class GameServer implements ApplicationListener {
	
	private final static int PACKETS_PER_TICK = 50;

	private String map;
	private int teamSize;
	private ConcurrentLinkedQueue<SFSObject> packetQueue;
	
	private World world;
	//TODO remove debugrenderer and camera
	private Box2DDebugRenderer renderer;
	private Vector2 gravity;
	private OrthographicCamera camera;
	
	
	//TODO Map
	public GameServer(String map, int teamSize){
		this.map=map;
		this.teamSize=teamSize;
		
		//////////////////////////
		//	box2d stuff			//
		//////////////////////////
		gravity = new Vector2(0,-9.81f);
		world = new World(gravity, true);
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//instantiate variables
		//load stuff from external files
		//load chosen Map	
	}
	
	//////////////////////////
	//	override methods	//
	//////////////////////////
	
	@Override
	public void create() {
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	//TODO AI, capture points
	
	@Override
	public void render() {
		//get client packages
		for(int i=0; i<PACKETS_PER_TICK; i++){
			packetQueue.remove();
			//do damn cool stuff
			//nigga
		}
		
		//recalculate positions and
		//check for dem collisions
		
		//check for ability collisions, damage abilities
		
		//calculate hp, apply status effects, recalculate position
		
		//send dat shit
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.render(world, camera.combined);
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
