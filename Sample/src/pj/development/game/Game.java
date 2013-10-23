package pj.development.game;

import pj.development.game.fileManagement.Storage;
import pj.development.game.logic.MainLogic;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;

public class Game implements ApplicationListener
{
	public static int WIDTH;
	public static int HEIGHT;
	
	public static Logger logger;
	
	private SpriteBatch batch;
	private MainLogic mainLogic;
	private Storage contentManager;
	
	/**
	 * set the references, no calls!!!
	 * 
	 * @param mainLogic the mainLogic that contains all subLogics
	 * @param contentManager the fileManager to init
	 * @throws GameException if a param is null
	 */
	public Game(MainLogic mainLogic, Storage contentManager)
	{
		if (mainLogic == null || contentManager == null)
			throw new GameException("Logic or contentManager cannot be null");
		
		this.mainLogic = mainLogic;
		this.contentManager = contentManager;
	}

	@Override
	public void create()
	{
		logger = new Logger("main");
		logger.setLevel(Logger.DEBUG);
		
		batch = new SpriteBatch();
		contentManager.init();
		mainLogic.setup(batch);
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void pause()
	{

	}

	@Override
	public void render()
	{
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		mainLogic.update(Gdx.graphics.getDeltaTime());
		mainLogic.render(Gdx.graphics.getDeltaTime(), batch);
	}

	@Override
	public void resize(int width, int height)
	{

	}

	@Override
	public void resume()
	{

	}

	@SuppressWarnings("serial")
	public class GameException extends RuntimeException
	{
		public GameException()
		{
		}
		
		public GameException(String message)
		{
			super(message);
		}
	}
	
}
