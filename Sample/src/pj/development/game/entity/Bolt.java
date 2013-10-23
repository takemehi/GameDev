package pj.development.game.entity;

import pj.development.game.Game;
import pj.development.game.assets.Animation;
import pj.development.game.assets.Animation.AnimationException;
import pj.development.game.assets.GraphicStorage;
import pj.development.game.entity.Yoda.YodaStates;
import pj.development.game.map.Level.TileLocation;
import pj.development.game.map.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bolt extends Entity
{
	private enum BoltStates
	{
		Flying, Bump
	}
	
	private static final int BOLT_WIDTH = 25;
	private static final int BOLT_HEIGHT = 3;
	private static final int BUMP_WIDTH = 32;
	private static final int BUMP_HEIGHT = 31;
	private static final int BUMP_FRAMES = 3;
	private static final float FRAME_DURATION = 0.08f;
	
	private static final float ACCELERATION = 240f;
	
	private TextureRegion bolt;
	private Animation bump;
	
	private BoltStates state;
	public boolean remove;
	
	public Bolt(float x, float y, World world, boolean left)
	{
		super(ACCELERATION, world, left);
		
		this.position = new Vector2(left ? x - BOLT_WIDTH * 2 : x, y);
		bolt = new TextureRegion(GraphicStorage.bolt, 0, 0, BOLT_WIDTH, BOLT_HEIGHT);
		state = BoltStates.Flying;
		remove = false;
		
		try
		{
			bump = new Animation(GraphicStorage.bolt, BUMP_FRAMES, FRAME_DURATION, BOLT_HEIGHT, BUMP_WIDTH, BUMP_HEIGHT);
		}
		catch (AnimationException e)
		{
			Game.logger.error("Bolt couldn't get created: " + e.getMessage());
			System.exit(-1);
		}
	}

	@Override
	public void update(float deltaTime)
	{
		stateTime += deltaTime;
		
		if (left && state != BoltStates.Bump)
		{
			moveLeft(deltaTime);
		}
		else if (!left && state != BoltStates.Bump)
		{
			moveRight(deltaTime);
		}		
		else if (state == BoltStates.Bump && stateTime / FRAME_DURATION > BUMP_FRAMES)
		{
			//despawn
			remove = true;
		}
	}
	
	private void checkCharCollision(Character c)
	{		
		if (getBoundingBox().overlaps(c.getBoundingBox()))
		{
			if (!(c instanceof Yoda))
			{
				c.die();
				changeState(BoltStates.Bump);
				return;
			}
			
			if (left == ((Yoda)c).getLeft())
			{
				changeState(BoltStates.Bump);
				//hurt yoda
				c.die();
			}
			else if (left != ((Yoda)c).getLeft())
			{
				if (((Yoda)c).getState() == YodaStates.Attack)
				{
					left = !left;
					
					if (left)
					{
						position.x = ((Yoda)c).getBoundingBox().x - BOLT_WIDTH;
					}
					else
					{
						position.x = ((Yoda)c).getBoundingBox().x + ((Yoda)c).getBoundingBox().width;
					}
				}
				else
				{
					changeState(BoltStates.Bump);
					//hurt yoda :(
					c.die();
				}
			}
		}
	}
	
	public void checkCollision(TileLocation[] tiles, float deltaTime, Yoda yoda, Character[] chars)
	{
		if (state == BoltStates.Bump)
			return;
		
		if (tiles == null)
			state = BoltStates.Bump;
		else
			checkWorldCollision(tiles, deltaTime);
		checkCharCollision(yoda);
		for (int i = 0; i < chars.length; i++)
			checkCharCollision(chars[i]);
	}
	
	private void checkWorldCollision(TileLocation[] tiles, float deltaTime)
	{
		if (tiles[5].i != -1 && !left)
		{
			int maxPosX = ((int)(position.x / GraphicStorage.TILE_WIDTH) * GraphicStorage.TILE_WIDTH) + (BOLT_WIDTH / 2);
			
			if (position.x > maxPosX)
			{
				position.x = maxPosX;
				changeState(BoltStates.Bump);
			}
		}
		else if (tiles[3].i != -1 && left)
		{
			int maxPosX = ((int)(position.x / GraphicStorage.TILE_WIDTH) * GraphicStorage.TILE_WIDTH) + (BOLT_WIDTH / 2);
			
			if (position.x < maxPosX)
			{
				position.x = maxPosX;
				changeState(BoltStates.Bump);
			}
		}
	}

	@Override
	public void render(float deltaTime, SpriteBatch batch)
	{
		batch.begin();
		
		switch(state)
		{
			case Flying:
				batch.draw(bolt, position.x, position.y);
				break;
			case Bump:
				batch.draw(bump.getCurrentFrame(deltaTime), position.x, position.y - BUMP_HEIGHT / 2);
				break;
		}
		
		batch.end();
	}
	
	private void changeState(BoltStates state)
	{
		this.state = state;
		stateTime = 0;
	}

	@Override
	public Rectangle getBoundingBox()
	{
		return new Rectangle(position.x, position.y, BOLT_WIDTH, BOLT_HEIGHT);
	}
	
}
