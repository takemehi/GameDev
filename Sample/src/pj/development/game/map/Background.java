package pj.development.game.map;

import pj.development.game.Game;
import pj.development.game.assets.GraphicStorage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Background
{
	public void render(float deltaTime, SpriteBatch batch, Vector3 camPos)
	{
		batch.begin();
		batch.draw(GraphicStorage.sky, camPos.x - Game.WIDTH / 2, camPos.y - Game.HEIGHT / 2, Game.WIDTH, Game.HEIGHT);
		batch.end();
	}
}
