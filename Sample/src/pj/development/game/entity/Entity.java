package pj.development.game.entity;

import pj.development.game.map.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity
{
	protected final float acceleration;
	
	public Vector2 position;
	protected float stateTime;
	protected World world;
	protected boolean left;
	
	public Entity(float acceleration, World world, boolean left)
	{
		this.acceleration = acceleration;
		this.world = world;
		this.left = left;
	}
	
	public boolean getLeft()
	{
		return left;
	}
	
	/**
	 * Moves the Entity to the left. Movement is frame independent
	 */
	public void moveLeft(float deltaTime)
	{
		position.x -= acceleration * deltaTime;
	}
	
	/**
	 * Moves the Entity to the right. Movement is frame independent
	 */
	public void moveRight(float deltaTime)
	{
		position.x += acceleration * deltaTime;
	}
	
	public abstract void update(float deltaTime);
	public abstract void render(float deltaTime, SpriteBatch batch);
	public abstract Rectangle getBoundingBox();
}
