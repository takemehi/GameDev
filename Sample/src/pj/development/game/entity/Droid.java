package pj.development.game.entity;

import pj.development.game.Game;
import pj.development.game.assets.Animation;
import pj.development.game.assets.GraphicStorage;
import pj.development.game.map.Level.TileLocation;
import pj.development.game.map.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Droid extends Character
{
	public enum DroidStates
	{
		Stand, Walk, Attack, Died
	}
	
	private static final int ATTACK_WIDTH = 55;
	private static final int ATTACK_HEIGHT = 50;
	private static final int ATTACK_FRAMES = 5;
	private static final int WALK_WIDTH = 31;
	private static final int WALK_HEIGHT = 49;
	private static final int WALK_FRAMES = 8;
	private static final int DIE_WIDTH = 126;
	private static final int DIE_HEIGHT = 64;
	private static final int DIE_FRAMES = 15;
	private static final float FRAME_DURATION = 0.08f;
	
	private static final float ACCELERATION = 100f;
	private static final float VELOCITY_START = 30f;
	
	private static final float SHOOT_DELAY = 1.5f; //3 secs
	private static final float RANGE = 250f;
	
	private TextureRegion stand;
	private Animation walk;
	private Animation attack;
	private Animation die;
	private DroidStates state;
	private float lastShoot;
	public boolean remove;

	public Droid(float maxHp, int x, int y, World world)
	{
		super(maxHp, x, y, world, ACCELERATION, VELOCITY_START, true);
		
		jump = false;
		state = DroidStates.Stand;
		lastShoot = 0;
		remove = false;
		
		stand = new TextureRegion(GraphicStorage.droid, 0, 0, ATTACK_WIDTH, ATTACK_HEIGHT);
		
		try
		{
			attack = new Animation(GraphicStorage.droid, ATTACK_FRAMES, FRAME_DURATION, 0, ATTACK_WIDTH, ATTACK_HEIGHT);
			walk = new Animation(GraphicStorage.droid, WALK_FRAMES, FRAME_DURATION, ATTACK_HEIGHT, WALK_WIDTH, WALK_HEIGHT);
			die = new Animation(GraphicStorage.droid, DIE_FRAMES, FRAME_DURATION, ATTACK_HEIGHT + WALK_HEIGHT, DIE_WIDTH, DIE_HEIGHT);
		}
		catch (Exception e)
		{
			Game.logger.error("Couldn't load droids textures: " + e.getMessage());
			System.exit(-1);
		}
	}

	@Override
	public void die()
	{
		changeState(DroidStates.Died);
	}

	@Override
	public void render(float deltaTime, SpriteBatch batch)
	{
		batch.begin();
		
		switch (state)
		{
			case Stand:
				batch.draw(stand, position.x - ATTACK_WIDTH / 2.0f, position.y);
				break;
			case Walk:
				batch.draw(walk.getCurrentFrame(stateTime), position.x - WALK_WIDTH / 2.0f, position.y);
				break;
			case Attack:
				batch.draw(attack.getCurrentFrame(stateTime), position.x - ATTACK_WIDTH / 2.0f, position.y);
				break;
			case Died:
				batch.draw(die.getCurrentFrame(stateTime), position.x - DIE_WIDTH / 2.0f, position.y);
				break;
		}
		
		batch.end();
	}
	
	public void checkWorldCollision(TileLocation[] tiles, float deltaTime)
	{
		if(super.checkWorldCollision(tiles, deltaTime, WALK_WIDTH))
		{
			left = !left;
			flip();
		}
	}
	
	public DroidStates getState()
	{
		return state;
	}

	@Override
	public void update(float deltaTime)
	{		
		stateTime += deltaTime;
		lastShoot += deltaTime;
		
		if (state != DroidStates.Died)
		{
			if (!left && state != DroidStates.Attack)
			{
				moveRight(deltaTime);
				if (state != DroidStates.Walk)
					changeState(DroidStates.Walk);
			}
			else if (left && state != DroidStates.Attack)
			{
				moveLeft(deltaTime);
				if (state != DroidStates.Walk)
					changeState(DroidStates.Walk);
			}
			
			if (((world.yoda.position.x < position.x && left) || (world.yoda.position.x > position.x && !left)) && lastShoot > SHOOT_DELAY && state != DroidStates.Attack)
			{
				if (Math.abs(world.yoda.position.x - position.x) < RANGE)
					changeState(DroidStates.Attack);
			}
		}
		
		switch (state)
		{
			case Stand:				
				break;
			case Walk:
				if (stateTime / FRAME_DURATION > WALK_FRAMES)
					stateTime = 0;//changeState(DroidStates.Stand);
				break;
			case Attack:
				if (stateTime / FRAME_DURATION > ATTACK_FRAMES)
				{
					changeState(DroidStates.Stand);
					lastShoot = 0;
					world.addBolt(new Bolt(left ? position.x : position.x + ATTACK_WIDTH, position.y + ATTACK_HEIGHT / 2, world, left));
				}
				break;
			case Died:
				if (stateTime / FRAME_DURATION > DIE_FRAMES)
				{
					//despawn
					remove = true;
				}
				break;
		}
		
		super.update(deltaTime);
	}
	
	private void changeState(DroidStates state)
	{
		this.state = state;
		stateTime = 0;
	}

	@Override
	public void flip()
	{
		stand.flip(true, false);
		attack.setFlip(!left);
		walk.setFlip(!left);
		die.setFlip(!left);
	}
	
	public int getWidth()
	{
		switch (state)
		{
			case Walk:
				return WALK_WIDTH;
			default:
			case Stand:
			case Attack:
				return ATTACK_WIDTH;
			case Died:
				return DIE_WIDTH;
		}
	}
	
	public int getHeight()
	{
		switch (state)
		{
			case Walk:
				return WALK_HEIGHT;
			default:
			case Stand:
			case Attack:
				return ATTACK_HEIGHT;
			case Died:
				return DIE_HEIGHT;
		}
	}

	@Override
	public Rectangle getBoundingBox()
	{
		return new Rectangle(position.x, position.y, getWidth(), getHeight());
	}

}
