package pj.development.game.assets;

import pj.development.game.fileManagement.Storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GraphicStorage implements Storage
{
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	
	public static Texture yoda;
	public static Texture droid;
	public static Texture bolt;
	public static Texture sky;
	public static Texture pause;
	public static BitmapFont font;
	public static TextureRegion[][] tileset;
	public static TextureRegion leftButton;
	public static TextureRegion rightButton;
	public static TextureRegion jumpButton;
	public static TextureRegion attackButton;
	public static TextureRegion enterButton;
	
	private Texture loadTexture(String filename)
	{
		return new Texture(Gdx.files.internal(filename));
	}
	
	@Override
	public void init()
	{
		yoda = loadTexture("data/yoda/yoda.png");
		droid = loadTexture("data/droid/droid.png");
		sky = loadTexture("data/bg/sky.png");
		bolt = loadTexture("data/bolt/bolt.png");
		pause = loadTexture("data/bg/pause.png");
		
		font = new BitmapFont(Gdx.files.internal("data/font/starjedi.fnt"), Gdx.files.internal("data/font/starjedi_0.png"), false);
		font.setColor(Color.BLACK);
		
		tileset = TextureRegion.split(loadTexture("data/map/tileset.png"), TILE_WIDTH, TILE_HEIGHT);
		
		leftButton = new TextureRegion(loadTexture("data/buttons/left.png"), 120, 50);
		rightButton = new TextureRegion(loadTexture("data/buttons/right.png"), 120, 50);
		jumpButton = new TextureRegion(loadTexture("data/buttons/jump.png"), 120, 50);
		attackButton = new TextureRegion(loadTexture("data/buttons/attack.png"), 120, 50);
		enterButton = new TextureRegion(loadTexture("data/buttons/enter.png"), 120, 50);
	}

}
