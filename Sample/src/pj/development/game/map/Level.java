package pj.development.game.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import pj.development.game.assets.GraphicStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Level
{
	public class TileLocation
	{
		public int i;
		public int j;
		
		public TileLocation(int i, int j)
		{			
			this.i = i;
			this.j = j;
		}
	}
	
	public class Point
	{
		public int x;
		public int y;
		
		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private TileLocation[][] map;
	private Point startPos;
	private Point endPos;
	public List<Point> enemyPos;
	
	/**
	 * Loads a level
	 * 
	 * @param filename the gdx internal filename
	 * @throws IOException if the file is not found
	 */
	public Level(String filename) throws IOException
	{
		enemyPos = new LinkedList<Point>();
		readLevel(filename);
	}
	
	private void readLevel(String filename) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(Gdx.files.internal(filename).read()));
		String line = null;
		List<String> lines = new LinkedList<String>();
		
		try
		{
			while ((line = br.readLine()) != null)
				lines.add(line);
		}
		finally
		{
			br.close();
		}
		
		int counter = 0;
		map = new TileLocation[lines.size()][lines.get(0).replace("-", "").length() / 5]; //5 cause of structure (X,Y)
		
		while(counter < lines.size())
		{
			String[] parts = lines.get(counter).split("\\(");
			
			for(int i = 1; i < parts.length; i++)
			{
				String[] rparts = parts[i].replace(")", "").split(",");
				map[(lines.size() - 1) - counter][i - 1] = new TileLocation(Integer.valueOf(rparts[0]), Integer.valueOf(rparts[1]));
			}
			
			counter++;
		}
		
		readLevelInfo(filename.replace(".txt", "info.txt"));
	}
	
	private void readLevelInfo(String filename) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(Gdx.files.internal(filename).read()));
		
		try
		{
			String line = br.readLine();
			line = line.replaceAll("#.*", "");
			line += br.readLine();
			line = line.replaceAll("#.*", "");
			
			String[] sStartPos = line.split(" ");
			startPos = new Point(Integer.valueOf(sStartPos[0]), Integer.valueOf(sStartPos[1]));
			
			line = br.readLine();
			line = line.replaceAll("#.*", "");
			line += br.readLine();
			line = line.replaceAll("#.*", "");
			
			String[] sEndPos = line.split(" ");
			endPos = new Point(Integer.valueOf(sEndPos[0]), Integer.valueOf(sEndPos[1]));
			
			while((line = br.readLine()) != null)
			{
				line = line.replaceAll("#.*", "");
				line = line.replace(" ", "");
				String[] sEnemyPos = line.split(",");
				enemyPos.add(new Point(Integer.valueOf(sEnemyPos[0]), Integer.valueOf(sEnemyPos[1])));			
			}
		}
		finally
		{
			br.close();
		}
	}
	
	public TileLocation[][] getMap()
	{
		return map;
	}
	
	
	public Point getStartPos()
	{
		return startPos;
	}
	
	public Point getEndPos()
	{
		return endPos;
	}
	
	public int getWidth()
	{
		return map[0].length * GraphicStorage.TILE_WIDTH;
	}
	
	public int getHeight()
	{
		return map.length * GraphicStorage.TILE_HEIGHT;
	}
	
	/**
	 * Returns the y value that is lower than the param y (to specify the bottom below an object)
	 * 
	 * @param x x coordinate of the object
	 * @param y the y of the object
	 * @return the Bottom y
	 */
	public int getBottomY(float x, float y)
	{
		int xValue = (int)(x / GraphicStorage.TILE_WIDTH);
		
		for(int result = (int)((y / GraphicStorage.TILE_HEIGHT) - 1); y >= 0; y--)
			if (map[result][xValue].i != -1)
				return result * GraphicStorage.TILE_HEIGHT;
		
		return -1;
	}
	
	/**
	 * Returns the tiles around the given coords
	 * 
	 * @param x
	 * @param y
	 * @return the tiles around the given coords 0 = top left, 8 = bottom right, 4 =x,y
	 */
	public TileLocation[] getTilesAround(float x, float y)
	{
		TileLocation[] result = new TileLocation[9];
		
		int xObject = (int)(x / GraphicStorage.TILE_WIDTH);
		int yObject = (int)(y / GraphicStorage.TILE_HEIGHT);
		
		if (xObject <= 0 || xObject >= map[0].length - 1 || yObject <= 0 || yObject == map.length - 1)
		{
			return null;
		}
		/*else if (xObject == map[0].length - 1)
		{
			TileLocation n = new TileLocation(0, 0);
			result[2] = n;
			result[5] = n;
			result[8] = n;
		}
		
		if (yObject == 0)
		{
			TileLocation n = new TileLocation(0, 0);
			result[6] = n;
			result[7] = n;
			result[8] = n;
		}
		else if (yObject == map.length - 1)
		{
			TileLocation n = new TileLocation(0, 0);
			result[0] = n;
			result[1] = n;
			result[2] = n;
		}*/
		
		if (result[0] == null)
			result[0] = map[yObject + 1][xObject - 1];
		if (result[1] == null)
			result[1] = map[yObject + 1][xObject];
		if (result[2] == null)
			result[2] = map[yObject + 1][xObject + 1];
		if (result[3] == null)
			result[3] = map[yObject][xObject - 1];
		result[4] = map[yObject][xObject];
		if (result[5] == null)
			result[5] = map[yObject][xObject + 1];
		if (result[6] == null)
			result[6] = map[yObject - 1][xObject - 1];
		if (result[7] == null)
			result[7] = map[yObject - 1][xObject];
		if (result[8] == null)
			result[8] = map[yObject - 1][xObject + 1];
		
		return result;
	}
	
	
	/**
	 * Returns the TileLocation for a Coordinate
	 * 
	 * @param x x coordinate of the tile to specify
	 * @param y y coordinate of the tile to specify
	 * @return the TileLocation of the tile to specify
	 */
	public TileLocation getTile(int x, int y)
	{
		return map[y / GraphicStorage.TILE_HEIGHT][x / GraphicStorage.TILE_WIDTH];
	}
	
	/**
	 * renders the tiles on the screen, only the tiles that are viewn are rendered 
	 * 
	 * @param x1 left x coordinate of the map to draw (should be cam.position.x - Game.WIDTH / 2)
	 * @param x2 right x coordinate of the map to draw (should be cam.position.x + Game.WIDTH / 2)
	 */
	public void renderTilesOnScreen(SpriteBatch batch, float x1, float x2)
	{
		batch.begin();
		
		for(int y = 0; y < map.length; y++)
			for(int x = (int)(x1 / GraphicStorage.TILE_WIDTH); x < x2 / GraphicStorage.TILE_WIDTH; x++)
			{
				if (map[y][x].i != -1)
					batch.draw(GraphicStorage.tileset[map[y][x].i][map[y][x].j], x * GraphicStorage.TILE_WIDTH, y * GraphicStorage.TILE_HEIGHT);
			}
		
		batch.end();		
	}
}
