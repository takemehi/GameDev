package pj.development.game.map;

import pj.development.game.Game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Map
{
	private Background bg;
	private Level level;
	
	public Map(Level level)
	{
		bg = new Background();
		this.level = level;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	public void render(float deltaTime, SpriteBatch batch, Vector3 camPos)
	{
		bg.render(deltaTime, batch, camPos);
		level.renderTilesOnScreen(batch, camPos.x - Game.WIDTH / 2, camPos.x + Game.WIDTH / 2);
	}
}
