package pj.development.game;

import pj.development.game.assets.GraphicStorage;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class DesktopStarter
{

	public static void main(String[] args)
	{
		new JoglApplication(new StarWarsPlatformer(new Renderer(), new GraphicStorage()), "Star Wars Platformer", 800, 480, false);
	}

}
