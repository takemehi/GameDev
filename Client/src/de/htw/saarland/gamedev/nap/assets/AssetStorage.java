package de.htw.saarland.gamedev.nap.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
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
	public static final String FOLDER_MAP = FOLDER_DATA + "maps/";
	public static final String FOLDER_GFX_SKILLS = FOLDER_GFX + "skills/";
	public static final String CALIBRI_PATH = FOLDER_FONT + "calibri";
	public static final String FIREBALL_PATH = FOLDER_GFX_SKILLS + "fireball.png";
	public static final String PYROBLAST_PATH = FOLDER_GFX_SKILLS + "pyroblast.png";
	public static final String NOVA_PATH = FOLDER_GFX_SKILLS + "nova.png";
	public static final String MAGE_ANIMATION_PATH = FOLDER_GFX + "mageSheet.png";
	public static final String WARRIOR_ANIMATION_PATH = FOLDER_GFX + "warriorSheet.png";
	public static final String TILESET_PATH = FOLDER_MAP + "tileset.png";
	public static final String CROSSHAIR_PATH = FOLDER_GFX + "crosshair.png";
	
	public BitmapFont calibri;
	public Texture fireball;
	public Texture pyroblast;
	public Texture nova;
	public Texture mageAnimation;
	public Texture warriorAnimation;
	public Texture tileset;
	public Pixmap cursorCrosshair;
	
	private AssetStorage() {
		calibri = new BitmapFont(new FileHandle(CALIBRI_PATH + ".fnt"), new FileHandle(CALIBRI_PATH + ".png"), false);
		fireball = new Texture(new FileHandle(FIREBALL_PATH));
		pyroblast = new Texture(new FileHandle(PYROBLAST_PATH));
		nova = new Texture(new FileHandle(NOVA_PATH));
		mageAnimation = new Texture(new FileHandle(MAGE_ANIMATION_PATH));
		warriorAnimation = new Texture(new FileHandle(WARRIOR_ANIMATION_PATH));
		tileset = new Texture(new FileHandle(TILESET_PATH));
		cursorCrosshair = new Pixmap(new FileHandle(CROSSHAIR_PATH));
	}
}
