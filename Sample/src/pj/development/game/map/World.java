package pj.development.game.map;

import java.util.LinkedList;
import java.util.List;

import pj.development.game.Game;
import pj.development.game.assets.GraphicStorage;
import pj.development.game.entity.Bolt;
import pj.development.game.entity.Droid;
import pj.development.game.entity.Yoda;
import pj.development.game.map.Level.Point;
import pj.development.game.menu.MenuListener;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class World
{
	public static final float GRAVITY = -80f;
	private static final float MAX_HP = 100f;
	
	private OrthographicCamera cam;
	public Yoda yoda;	
	private Map map;
	private boolean win;
	private float winTime;
	private MenuListener menuListener;
	private List<Bolt> objects;
	private List<Droid> enemys;
	
	public World(MenuListener menuListener)
	{
		this.menuListener = menuListener;
	}
	
	public void setup(String levelFilename)
	{
		cam = new OrthographicCamera(Game.WIDTH, Game.HEIGHT);
		cam.position.set(Game.WIDTH / 2, Game.HEIGHT / 2, 0);
		win = false;		
		objects = new LinkedList<Bolt>();
		enemys = new LinkedList<Droid>();
		
		try
		{
			map = new Map(new Level("data/level/level1.txt"));
			
			for(Point p: map.getLevel().enemyPos)
			{
				enemys.add(new Droid(MAX_HP, p.x, p.y, this));
			}
		}
		catch (Exception e)
		{
			Game.logger.error("Level couldn't get loaded " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		yoda = new Yoda(MAX_HP, map.getLevel().getStartPos().x, map.getLevel().getStartPos().y, this);
	}
	
	private float fps;
	
	public void render(float deltaTime, SpriteBatch batch)
	{
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		map.render(deltaTime, batch, cam.position);
		for (int i = 0; i < objects.size(); i++)
			objects.get(i).render(deltaTime, batch);
		
		for (int i = 0; i < enemys.size(); i++)
			enemys.get(i).render(deltaTime, batch);
		
		yoda.render(deltaTime, batch);
		
		if (win)
		{
			batch.begin();
			GraphicStorage.font.draw(batch, "victory!", cam.position.x - GraphicStorage.font.getBounds("victory!").width / 2, cam.position.y);
			batch.end();
		}
		
		if (System.currentTimeMillis() % 1000 == 0)
			fps = 1.0f / deltaTime;
		
		batch.begin();
		GraphicStorage.font.draw(batch, String.format("FPS: %f", fps), cam.position.x - Game.WIDTH / 2 + 10, Game.HEIGHT - GraphicStorage.font.getBounds("FPS").height - 10);
		batch.end();
	}
	
	public void update(float deltaTime)
	{
		if (win)
		{
			winTime += deltaTime;
			
			if (winTime > 3.0f)
			{
				menuListener.startGame("level1.text");
			}
		}
		
		if (!win)
		{
			yoda.update(deltaTime);
			yoda.checkWorldCollision(map.getLevel().getTilesAround(yoda.position.x, yoda.position.y), deltaTime);
			yoda.checkCharCollision(enemys.toArray(new Droid[0]));
			
			for (int i = 0; i < enemys.size(); i++)
			{
				Droid d = enemys.get(i);
				d.update(deltaTime);
				d.checkWorldCollision(map.getLevel().getTilesAround(d.position.x, d.position.y), deltaTime);
				
				if (d.remove)
				{
					enemys.remove(i);
					i--;
				}
			}
			
			for (int i = 0; i < objects.size(); i++)
			{
				Bolt b = objects.get(i);
				b.update(deltaTime);
				b.checkCollision(map.getLevel().getTilesAround(b.position.x, b.position.y), deltaTime, yoda, enemys.toArray(new Droid[0]));
				
				if (b.remove)
				{
					objects.remove(i);
					i--;
				}
			}
		}
		
		if (!win && yoda.position.x >= map.getLevel().getEndPos().x && yoda.position.y >= map.getLevel().getEndPos().y)
		{
			win = true;
			winTime = 0;
		}
		
		if (yoda.position.x > cam.position.x + Game.WIDTH / 4 && yoda.position.x + Game.WIDTH / 4 < map.getLevel().getWidth())
		{
			cam.position.x = yoda.position.x - Game.WIDTH / 4;
		}
		else if (yoda.position.x < cam.position.x - Game.WIDTH / 2)
			yoda.position.x = cam.position.x - Game.WIDTH / 2;		
	}
	
	public void addBolt(Bolt bolt)
	{
		objects.add(bolt);
	}
	
	public void addEnemy(Droid c)
	{
		enemys.add(c);
	}
	
	public Vector3 getCamPos()
	{
		return cam.position;
	}
}
