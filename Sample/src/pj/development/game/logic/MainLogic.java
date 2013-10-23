package pj.development.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface MainLogic
{
	public void setup(SpriteBatch batch);
	public void render(float deltaTime, SpriteBatch batch);
	public void update(float deltaTime);
}
