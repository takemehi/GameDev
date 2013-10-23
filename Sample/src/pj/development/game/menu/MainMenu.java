package pj.development.game.menu;

import pj.development.game.Game;
import pj.development.game.assets.GraphicStorage;
import pj.development.game.entity.Yoda;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenu
{
	private class NPCYoda extends Yoda
	{
		public boolean jump;
		
		public NPCYoda(float maxHp, int x, int y)
		{
			super(maxHp, x, y, null);
			jump = false;
			//changeState(YodaStates.Walk);
		}
		
		/**
		 * Do Nothing, so yoda can't get moved by user input
		 */
		@Override
		protected void handleInput(Input in, float deltaTime)
		{
		}
		
		@Override
		public void update(float deltaTime)
		{
			super.update(deltaTime);
			
			if (position.y < 64)
			{
				//jump = false;
				position.y = 64;
			}
		}
		
	}
	
	private enum ActiveCaption
	{
		StartGame, Help, Exit
	}
	
	public enum ActiveMenu
	{
		Main, Help
	}
	
	private static final String TITLE = "Star Wars - Rise of Yoda";
	private static final String START_GAME = "Start Game";
	private static final String HELP = "Help";
	private static final String EXIT = "Exit";
	
	private static float ALPHA_REDUCE = 0.5f;
	
	private NPCYoda npcYoda;
	private BitmapFont font;
	private ActiveCaption activeCaption;
	private ActiveMenu activeMenu;
	private float alpha;
	private boolean[] pressedKeys;
	private MenuListener menuListener;
	
	public MainMenu(MenuListener menuListener)
	{
		npcYoda = new NPCYoda(1, -30, 64);
		font = GraphicStorage.font;
		activeCaption = ActiveCaption.StartGame;
		activeMenu = ActiveMenu.Main;
		alpha = 1f;
		pressedKeys = new boolean[3];
		this.menuListener = menuListener;
	}
	
	private void drawBackground(float deltaTime, SpriteBatch batch)
	{
		batch.begin();
		batch.disableBlending();
		
		batch.draw(GraphicStorage.sky, 0, 0, Game.WIDTH, Game.HEIGHT);
		
		for (int i = 0; i * GraphicStorage.TILE_WIDTH < Game.WIDTH; i++)
		{
			batch.draw(GraphicStorage.tileset[1][1], i * GraphicStorage.TILE_WIDTH, 0);
			batch.draw(GraphicStorage.tileset[1][5], i * GraphicStorage.TILE_WIDTH, GraphicStorage.TILE_HEIGHT);
		}
			
		batch.enableBlending();
		
		batch.end();
	}
	
	private void drawNpcYoda(float deltaTime, SpriteBatch batch)
	{
		npcYoda.update(deltaTime);
		
		if (npcYoda.position.x < Game.WIDTH / 2 && !npcYoda.jump)
			npcYoda.moveRight(deltaTime);
		else if (!npcYoda.jump)
		{
			npcYoda.jump();
			npcYoda.jump = true;
			npcYoda.flip();
		}
		else if (npcYoda.position.x > -30)
			npcYoda.moveLeft(deltaTime);
		
		npcYoda.render(deltaTime, batch);
	}
	
	private float getXForString(CharSequence s)
	{
		return (Game.WIDTH / 2.0f) - (font.getBounds(s).width / 2.0f); 
	}
	
	private float getHeightOfString(CharSequence s)
	{
		return font.getBounds(s).height;
	}
	
	public void setAlpha(float deltaTime)
	{
		alpha -= ALPHA_REDUCE * deltaTime;
		
		if (alpha <= 0 || alpha >= 1)
		{
			ALPHA_REDUCE *= -1;
			alpha = alpha <= 0 ? 0 : 1;
		}
	}
	
	private void drawAlpha(SpriteBatch batch, CharSequence s, float x, float y, boolean useAlpha)
	{
		if (useAlpha)
		{
			font.setColor(0f, 0f, 0f, alpha);
			font.draw(batch, s, x, y);
			font.setColor(0f, 0f, 0f, 1f);
		}
		else
			font.draw(batch, s, x, y);
	}
	
	private void drawInterface(float deltaTime, SpriteBatch batch)
	{
		handleInput();
		setAlpha(deltaTime);
		batch.begin();
		
		font.setScale(1.1f);
		font.draw(batch, TITLE, getXForString(TITLE), Game.HEIGHT / 4 * 3 + 30);
		font.setScale(1f);
		
		drawAlpha(batch, START_GAME, getXForString(START_GAME) , Game.HEIGHT / 4 * 3 - getHeightOfString(START_GAME), activeCaption == ActiveCaption.StartGame);
		
		drawAlpha(batch, HELP, getXForString(HELP) , Game.HEIGHT / 4 * 3 - getHeightOfString(START_GAME) - 10 - getHeightOfString(HELP), activeCaption == ActiveCaption.Help);
		
		drawAlpha(batch, EXIT, getXForString(EXIT) , Game.HEIGHT / 4 * 3 - getHeightOfString(START_GAME) - 10 - getHeightOfString(HELP) - 10 - getHeightOfString(EXIT), activeCaption == ActiveCaption.Exit);
		
		batch.end();
	}
	
	private void handleInput()
	{
		if (activeMenu == ActiveMenu.Main)
		{
			boolean[] pressedBefore = pressedKeys.clone();
			
			pressedKeys[0] = Gdx.app.getType() == ApplicationType.Android ? Gdx.input.isTouched() && Gdx.input.getX() < Game.WIDTH / 4 : Gdx.input.isKeyPressed(Input.Keys.UP);
			pressedKeys[1] = Gdx.app.getType() == ApplicationType.Android ? Gdx.input.isTouched() && Gdx.input.getX() > Game.WIDTH / 4 && Gdx.input.getX() < Game.WIDTH / 2 : Gdx.input.isKeyPressed(Input.Keys.DOWN);
			pressedKeys[2] = Gdx.app.getType() == ApplicationType.Android ? Gdx.input.isTouched() && Gdx.input.getX() > Game.WIDTH / 2 : Gdx.input.isKeyPressed(Input.Keys.ENTER);
			
			if (pressedKeys[2] && !pressedBefore[2]) // enter
			{
				switch (activeCaption)
				{
					case StartGame:
						menuListener.startGame("level1.txt");
						break;
					case Help:
						break;
					case Exit:
						Gdx.app.exit();
						break;
				}
			}
			else if (pressedKeys[0] && !pressedBefore[0])
			{
				switch (activeCaption)
				{
					case StartGame:
						activeCaption = ActiveCaption.Exit;
						break;
					case Help:
						activeCaption = ActiveCaption.StartGame;
						break;
					case Exit:
						activeCaption = ActiveCaption.Help;
						break;
				}
				
				alpha = 1f;
				ALPHA_REDUCE = Math.abs(ALPHA_REDUCE);
			}
			else if (pressedKeys[1] && !pressedBefore[1])
			{
				switch (activeCaption)
				{
					case StartGame:
						activeCaption = ActiveCaption.Help;
						break;
					case Help:
						activeCaption = ActiveCaption.Exit;
						break;
					case Exit:
						activeCaption = ActiveCaption.StartGame;
						break;
				}
				
				alpha = 1f;
				ALPHA_REDUCE = Math.abs(ALPHA_REDUCE);
			}
		}
	}
	
	private void drawHelp(SpriteBatch batch)
	{
		
	}
	
	public void render(float deltaTime, SpriteBatch batch)
	{
		if (activeMenu == ActiveMenu.Main)
		{
			drawBackground(deltaTime, batch);
			drawNpcYoda(deltaTime, batch);
			drawInterface(deltaTime, batch);
		}
		else if (activeMenu == ActiveMenu.Help)
		{
			drawHelp(batch);
		}
	}
}
