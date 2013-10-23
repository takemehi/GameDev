package pj.development.game.entity;

import pj.development.game.assets.GraphicStorage;
import pj.development.game.map.Level.TileLocation;
import pj.development.game.map.World;

import com.badlogic.gdx.math.Vector2;

public abstract class Character extends Entity
{
	private final float velocityStart;
	
	private float maxHp;
	private float currentHp;
	protected float velocity;
	protected World world;
	protected boolean jump;
	
	/**
	 * Creates a new Character, currentHp is set to maxHp
	 * 
	 * @param maxHp maxHp of the char
	 * @param position the position of the char on the screen
	 * @throws IllegalArgumentException if maxHp is less or equal 0
	 */
	public Character(float maxHp, Vector2 position, World world, float acceleration, float velocityStart, boolean left)
	{
		this(maxHp, maxHp, position, world, acceleration, velocityStart, left);
	}
	
	/**
	 * Creates a new Character, currentHp is set to maxHp
	 * 
	 * @param maxHp maxHp of the char
	 * @param x x position of the char on the screen
	 * @param y y position of the char n the screen
	 * @throws IllegalArgumentException if maxHp is less or equal 0
	 */
	public Character(float maxHp, int x, int y, World world, float acceleration, float velocityStart, boolean left)
	{
		this(maxHp, maxHp, x, y, world, acceleration, velocityStart, left);
	}
	
	/**
	 * Instances a new Character
	 * 
	 * @param maxHp maxHp of the Char
	 * @param currentHp currentHp of the char
	 * @param x x position of the char on the screen
	 * @param y y position of the char n the screen
	 * @throws IllegalArgumentException if any hp is less or equal 0
	 */
	public Character(float maxHp, float currentHp, float x, float y, World world, float acceleration, float velocityStart, boolean left)
	{
		this(maxHp, currentHp, new Vector2(x, y), world, acceleration, velocityStart, left);
	}
	
	/**
	 * Instances a new Character
	 * 
	 * @param maxHp maxHp of the Char
	 * @param currentHp currentHp of the char
	 * @throws IllegalArgumentException if any hp is less or equal 0
	 */
	public Character(float maxHp, float currentHp, Vector2 position, World world, float acceleration, float velocityStart, boolean left)
	{
		super(acceleration, world, left);
		
		if (maxHp <= 0 || currentHp <= 0)
			throw new IllegalArgumentException("Hp has to be greater 0");
		
		this.maxHp = maxHp;
		this.currentHp = maxHp;
		this.position = position;
		this.stateTime = 0;
		this.world = world;
		this.velocityStart = velocityStart;
	}

	public float getCurrentHp()
	{
		return currentHp;
	}
	
	public float getMaxp()
	{
		return maxHp;
	}
	
	public abstract void die();
	public abstract void flip();
	
	/**
	 * Override this method to handle the objects stuff and call this method from there!
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		if (jump)
		{
			velocity += World.GRAVITY * deltaTime;
			
			position.y += velocity * deltaTime * 10;
		}
	}
	
	/**
	 * Returns true if the char moved against a wall (left or right)
	 * 
	 * @param tiles
	 * @param deltaTime
	 * @param walksLeft
	 * @param walkTexWidth
	 * @return
	 */
	protected boolean checkWorldCollision(TileLocation[] tiles, float deltaTime, int walkTexWidth)
	{
		if (tiles == null)
			return false;
		
		if (jump && velocity < 0 && tiles[4].i != -1)
		{
			//if ((position.y <= (int)(position.y / GraphicStorage.TILE_HEIGHT) * GraphicStorage.TILE_HEIGHT + 1))
			//{
				jump = false;
				position.y = (int)(position.y / GraphicStorage.TILE_HEIGHT) * GraphicStorage.TILE_HEIGHT + GraphicStorage.TILE_HEIGHT;
			//}
		}
		else if (!jump && tiles[7].i == -1)
		{
			//falling
			fall();			
		}
		else if (tiles[5].i != -1 && !left)
		{
			int maxPosX = ((int)(position.x / GraphicStorage.TILE_WIDTH) * GraphicStorage.TILE_WIDTH) + (walkTexWidth / 2);
			
			if (position.x > maxPosX)
				position.x = maxPosX;
			
			return true;
		}
		else if (tiles[3].i != -1 && left)
		{
			int maxPosX = ((int)(position.x / GraphicStorage.TILE_WIDTH) * GraphicStorage.TILE_WIDTH) + (walkTexWidth / 2);
			
			if (position.x < maxPosX)
				position.x = maxPosX;
			
			return true;
		}
		else if (jump && tiles[1].i != -1 && velocity > 0)
		{
			velocity = 0f;
		}
		
		return false;
	}
	
	/**
	 * Lets the Character jump. Frame independent
	 */
	public void jump()
	{
		jump = true;
		velocity = velocityStart;
	}
	
	/**
	 * lets the character fall
	 */
	protected void fall()
	{
		jump = true;
		velocity = 0f;
	}
	
	/**
	 * Reduces the currentHp by hpLoss
	 * 
	 * @param hpLoss the lost hp, the absolute value is taken, so this value can slo be less zero
	 * 		the hp gets reduced by this value
	 */
	public void reduceCurrentHp(float hpLoss)
	{
		hpLoss = Math.abs(hpLoss);
		
		currentHp -= hpLoss;
		
		if (currentHp <= 0)
		{
			currentHp = 0;
			die();
		}
	}
}
