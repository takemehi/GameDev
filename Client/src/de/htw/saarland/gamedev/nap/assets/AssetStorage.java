package de.htw.saarland.gamedev.nap.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class AssetStorage {
	
	private static AssetStorage instance;
	
	public static AssetStorage getInstance() {
		if (instance == null) {
			instance = new AssetStorage();
		}
		
		return instance;
	}
	
	public static final String FOLDER_DATA = "data/";
	public static final String FOLDER_FONT = FOLDER_DATA + "font/";
	public static final String FOLDER_GFX = FOLDER_DATA + "gfx/";
	public static final String FOLDER_GFX_SKILLS = FOLDER_GFX + "skills/";
	public static final String CALIBRI_PATH = FOLDER_FONT + "calibri";
	public static final String FIREBALL_PATH = FOLDER_GFX_SKILLS + "fireball.png";
	public static final String PYROBLAST_PATH = FOLDER_GFX_SKILLS + "pyroblast.png";
	public static final String NOVA_PATH = FOLDER_GFX_SKILLS + "nova.png";
	
	public BitmapFont calibri;
	public Texture fireball;
	public Texture pyroblast;
	public Texture nova;
	
	private AssetStorage() {
		calibri = new BitmapFont(new FileHandle(CALIBRI_PATH + ".fnt"), new FileHandle(CALIBRI_PATH + ".png"), false);
		fireball = new Texture(new FileHandle(FIREBALL_PATH));
		pyroblast = new Texture(new FileHandle(PYROBLAST_PATH));
		nova = new Texture(new FileHandle(NOVA_PATH));
	}
}