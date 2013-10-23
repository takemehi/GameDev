package pj.development.game.menu;

import pj.development.game.Game;
import pj.development.game.assets.GraphicStorage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PauseMenu
{
	private Vector2 pos;
	
	public PauseMenu()
	{
		pos = new Vector2(0, 0);
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		batch.draw(GraphicStorage.pause, pos.x, pos.y, Game.WIDTH, Game.HEIGHT);
		batch.end();
	}
	
	public void applyGameCam(Vector3 camPos)
	{
		pos.x = camPos.x - Game.WIDTH / 2;		
	}
}
