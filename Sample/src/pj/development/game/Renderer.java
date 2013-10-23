package pj.development.game;

import pj.development.game.logic.MainLogic;
import pj.development.game.map.World;
import pj.development.game.menu.MainMenu;
import pj.development.game.menu.MenuListener;
import pj.development.game.menu.PauseMenu;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Renderer implements MainLogic, MenuListener
{
	private boolean inMenu;
	private boolean paused;
	private boolean pressedEscBefore;

	private MainMenu menu;
	private PauseMenu pauseMenu;
	private World world;
	
	public Renderer()
	{
		inMenu = true;
		paused = false;
		pressedEscBefore = false;
		world = new World(this);
		pauseMenu = new PauseMenu();
	}
	
	public void setup(SpriteBatch batch)
	{
		menu = new MainMenu(this);
	}
	
	@Override
	public void render(float deltaTime, SpriteBatch batch)
	{		
		if (inMenu)
		{
			menu.render(deltaTime, batch);
		}
		else
		{
			world.render(deltaTime, batch);
			if (paused)
			{
				pauseMenu.applyGameCam(world.getCamPos());
				pauseMenu.render(batch);
			}
		}
		
	}

	@Override
	public void update(float deltaTime)
	{
		boolean escPressed = Gdx.app.getType() == ApplicationType.Android ? Gdx.input.isTouched() && Gdx.input.getY() < Game.HEIGHT / 2 : Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
		if (escPressed && !pressedEscBefore && !inMenu)
			paused = !paused;
		
		if (!inMenu && !paused)
			world.update(deltaTime);
		
		pressedEscBefore = escPressed;
	}
	
	public boolean isInMenu()
	{
		return inMenu;
	}

	@Override
	public void startGame(String levelFilename)
	{
		world.setup(levelFilename);
		inMenu = false;
	}

	@Override
	public void changeState(MenuState state)
	{
		if (state == MenuState.Pause)
		{
			paused = true;
		}
		else if (state == MenuState.Continue)
		{
			paused = false;
		}
	}

}
