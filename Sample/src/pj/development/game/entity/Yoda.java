package pj.development.game.entity;

import pj.development.game.Game;
import pj.development.game.assets.Animation;
import pj.development.game.assets.Animation.AnimationException;
import pj.development.game.assets.GraphicStorage;
import pj.development.game.entity.Droid.DroidStates;
import pj.development.game.map.Level.TileLocation;
import pj.development.game.map.World;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Yoda extends Character
{
	public enum YodaStates
	{
		Stand1, Stand2, Walk, Attack, Died
	}
	
	private enum KeyIndex
	{
		LEFT(0), RIGHT(1), JUMP(2), ATTACK(3);
		
		public final int index;
		
		KeyIndex(int index)
		{
			this.index = index;
		}
	}
	
	private static final int STAND_WIDTH = 28;
	private static final int STAND_HEIGHT = 25;
	private static final int WALK_WIDTH = 30;
	private static final int WALK_HEIGHT = 25;
	private static final int ATTACK_WIDTH = 36;
	private static final int ATTACK_HEIGHT = 32;
	private static final int WALK_FRAMES = 3;
	private static final int ATTACK_FRAMES = 6;
	private static final float FRAME_DURATION = 0.08f;
	
	private static final float ACCELERATION = 100f;
	private static final float VELOCITY_START = 45f;
	
	private static final int LEFT_KEY = Input.Keys.LEFT;
	private static final int RIGHT_KEY = Input.Keys.RIGHT;
	private static final int JUMP_KEY = Input.Keys.UP;
	private static final int ATTACK_KEY = Input.Keys.SPACE;
	
	private TextureRegion yoda_stand1;
	private TextureRegion yoda_stand2;
	private Animation yoda_walk;
	private Animation yoda_attack;
	private YodaStates state;
	private boolean[] pressedKeys;

	public Yoda(float maxHp, Vector2 position, World world)
	{
		super(maxHp, position, world, ACCELERATION, VELOCITY_START, false);
		
		state = YodaStates.Stand2;		
		yoda_stand1 = new TextureRegion(GraphicStorage.yoda, 0, 0, STAND_WIDTH, STAND_HEIGHT);
		yoda_stand2 = new TextureRegion(GraphicStorage.yoda, STAND_WIDTH, 0, STAND_WIDTH, STAND_HEIGHT);
		pressedKeys = new boolean[4];
		jump = false;
		
		try
		{
			yoda_walk = new Animation(GraphicStorage.yoda, WALK_FRAMES, FRAME_DURATION, STAND_HEIGHT, WALK_WIDTH, WALK_HEIGHT);
			yoda_attack = new Animation(GraphicStorage.yoda, ATTACK_FRAMES, FRAME_DURATION, STAND_HEIGHT + WALK_HEIGHT, ATTACK_WIDTH, ATTACK_HEIGHT);
		}
		catch(AnimationException e)
		{
			Game.logger.error("Couldn't load yoda animations: " + e.getMessage());
			System.exit(-1);
		}
	}

	public Yoda(float maxHp, int x, int y, World world)
	{
		this(maxHp, new Vector2(x, y), world);
	}

	@Override
	public void render(float deltaTime, SpriteBatch batch)
	{ 
		if (state == YodaStates.Died && position.y + STAND_HEIGHT < 0)
			return;
		
		batch.begin();
		
		switch(state)
		{
			case Died:
			case Stand1:
				batch.draw(yoda_stand1, position.x, position.y);
				break;
			case Stand2:
				batch.draw(yoda_stand2, position.x - Math.abs(yoda_stand2.getRegionWidth() / 2.0f), position.y);
				break;
			case Attack:
				TextureRegion currFrameAttack = yoda_attack.getCurrentFrame(stateTime);
				batch.draw(currFrameAttack, position.x - Math.abs(currFrameAttack.getRegionWidth() / 2.0f), position.y);
				break;
			case Walk:
				TextureRegion currFrameWalk = yoda_walk.getCurrentFrame(stateTime); 
				batch.draw(currFrameWalk, position.x - Math.abs(currFrameWalk.getRegionWidth() / 2.0f), position.y);
				break;
		}
		
		if (world != null && Gdx.app.getType() == ApplicationType.Android)
		{
			float leftPos = world.getCamPos().x - Game.WIDTH / 2;
			float rightPos = world.getCamPos().x + Game.WIDTH / 2;
			batch.draw(GraphicStorage.leftButton, leftPos + 32, 15);
			batch.draw(GraphicStorage.rightButton, leftPos + 32 + 120 + 32, 15);
			batch.draw(GraphicStorage.jumpButton, rightPos - 120 - 32 - 32 - 120, 15);
			batch.draw(GraphicStorage.attackButton, rightPos - 32 - 120, 15);
		}
		
		batch.end();		
	}
	
	public YodaStates getState()
	{
		return state;
	}
	
	public int getWidth()
	{
		switch(state)
		{
			case Stand1:
			case Stand2:
				return STAND_WIDTH;
			case Attack:
				return ATTACK_WIDTH;
			case Walk:
				return WALK_WIDTH;
			case Died:
				return STAND_WIDTH;
			default:
				return STAND_WIDTH;
		}
	}
	
	public int getHeight()
	{
		switch(state)
		{
			case Stand1:
			case Stand2:
				return STAND_HEIGHT;
			case Attack:
				return ATTACK_HEIGHT;
			case Walk:
				return WALK_HEIGHT;
			case Died:
				return STAND_HEIGHT;
			default:
				return STAND_HEIGHT;
		}
	}

	@Override
	public void update(float deltaTime)
	{
		if (state == YodaStates.Died && position.y + STAND_HEIGHT < 0)
			return;
		
		stateTime += deltaTime;
		handleInput(Gdx.input, deltaTime);
		
		switch(state)
		{
			case Stand1:
				break;
			case Stand2:
				break;
			case Attack:
				if (stateTime / FRAME_DURATION > ATTACK_FRAMES)
					changeState(YodaStates.Stand2);
				break;
			case Walk:
				if (stateTime / FRAME_DURATION > ATTACK_FRAMES)
					stateTime = 0;
				break;
			case Died:
				break;
		}
		
		super.update(deltaTime);
	}

	@Override
	public void die()
	{
		changeState(YodaStates.Died);
		jump();
	}
	
	public void checkCharCollision(Character[] chars)
	{
		if (state == YodaStates.Died)
			return;
		
		for (Character c: chars)
		{
			if ((c instanceof Droid) && ((Droid)c).getState() != DroidStates.Died)
			{			
				if (c.getBoundingBox().overlaps(getBoundingBox()))
				{
					if (state != YodaStates.Attack)
						die();
					else
					{
						if ((position.x > c.position.x) == left)
							c.die();
						else
							die();
					}
				}
			}
		}
	}
	
	public void checkWorldCollision(TileLocation[] tiles, float deltaTime)
	{
		if (state != YodaStates.Died)
			super.checkWorldCollision(tiles, deltaTime, WALK_WIDTH);
	}
	
	/**
	 * Fills the array with the pressed Keys
	 * Index:
	 * 	0 : left
	 * 	1 : right
	 * 	2 : jump
	 * 	3 : attack
	 */
	private void getPressedKey(Input in)
	{
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			pressedKeys[KeyIndex.LEFT.index] = false;
			pressedKeys[KeyIndex.RIGHT.index] = false;
			pressedKeys[KeyIndex.JUMP.index] = false;
			pressedKeys[KeyIndex.ATTACK.index] = false;
			
			for (int i = 0; i < 3; i++)
			{
				/*pressedKeys[KeyIndex.LEFT.index] = pressedKeys[KeyIndex.LEFT.index] || (in.isTouched(i) && in.getX(i) < Game.WIDTH / 4);
				pressedKeys[KeyIndex.RIGHT.index] = pressedKeys[KeyIndex.RIGHT.index] || (in.isTouched(i) && in.getX(i) > Game.WIDTH / 4 && in.getX(i) < Game.WIDTH / 2);
				pressedKeys[KeyIndex.JUMP.index] = pressedKeys[KeyIndex.JUMP.index] || (in.isTouched(i) && in.getX(i) > Game.WIDTH / 2 && in.getX(i) < (Game.WIDTH * 3) / 4);
				pressedKeys[KeyIndex.ATTACK.index] = pressedKeys[KeyIndex.ATTACK.index] || (in.isTouched(i) && in.getX(i) > (Game.WIDTH * 3) / 4);*/
				
				pressedKeys[KeyIndex.LEFT.index] = pressedKeys[KeyIndex.LEFT.index] || (in.isTouched(i) && new Rectangle(32, Game.HEIGHT - 15 - 50, 120, 50).contains(in.getX(i), in.getY(i)));
				pressedKeys[KeyIndex.RIGHT.index] = pressedKeys[KeyIndex.RIGHT.index] || (in.isTouched(i) && new Rectangle(32 + 120 + 32, Game.HEIGHT - 15 - 50, 120, 50).contains(in.getX(i), in.getY(i)));
				pressedKeys[KeyIndex.JUMP.index] = pressedKeys[KeyIndex.JUMP.index] || (in.isTouched(i) && new Rectangle(Game.WIDTH - 120 - 32 - 32 - 120, Game.HEIGHT - 15 - 50, 120, 50).contains(in.getX(i), in.getY(i)));
				pressedKeys[KeyIndex.ATTACK.index] = pressedKeys[KeyIndex.ATTACK.index] || (in.isTouched(i) && new Rectangle(Game.WIDTH - 120 - 32, Game.HEIGHT - 15 - 50, 120, 50).contains(in.getX(i), in.getY(i)));
			}
		}
		else
		{
			pressedKeys[KeyIndex.LEFT.index] = in.isKeyPressed(LEFT_KEY);
			pressedKeys[KeyIndex.RIGHT.index] = in.isKeyPressed(RIGHT_KEY);
			pressedKeys[KeyIndex.JUMP.index] = in.isKeyPressed(JUMP_KEY);
			pressedKeys[KeyIndex.ATTACK.index] = in.isKeyPressed(ATTACK_KEY);
		}
	}
	
	protected void handleInput(Input in, float deltaTime)
	{
		if (state == YodaStates.Died)
			return;
		
		getPressedKey(in);
		
		if(pressedKeys[KeyIndex.LEFT.index])
		{
			if (!left)
			{
				left = true;
				flip();
			}
			
			if(state != YodaStates.Walk && state != YodaStates.Attack)
				changeState(YodaStates.Walk);
			
			moveLeft(deltaTime);
		}
		else if (pressedKeys[KeyIndex.RIGHT.index])
		{
			if (left)
			{
				left = false;
				flip();
			}
			
			if(state != YodaStates.Walk && state != YodaStates.Attack)
				changeState(YodaStates.Walk);
			
			moveRight(deltaTime);
		}
		
		if (pressedKeys[KeyIndex.ATTACK.index] && state != YodaStates.Attack)
		{
			changeState(YodaStates.Attack);
		}

		if (pressedKeys[KeyIndex.JUMP.index] && !jump)
		{
			jump();
		}
		
		if (!pressedKeys[KeyIndex.LEFT.index] &&
			!pressedKeys[KeyIndex.RIGHT.index] &&
			state != YodaStates.Attack &&
			!pressedKeys[KeyIndex.JUMP.index])
				changeState(YodaStates.Stand2);
	}
	
	/**
	 * Flips the sprites to let yoda move left or right
	 */
	@Override
	public void flip()
	{
		yoda_stand1.flip(true, false);
		yoda_stand2.flip(true, false);
		yoda_attack.setFlip(left);
		yoda_walk.setFlip(left);
	}
	
	public void changeState(YodaStates state)
	{
		this.state = state;
		stateTime = 0;
	}

	@Override
	public Rectangle getBoundingBox()
	{
		Rectangle box = new Rectangle(position.x - getWidth() / 4, position.y, (getWidth() * 3) / 4, getHeight());
		
		return box;
	}

}
