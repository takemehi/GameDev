package de.htw.saarland.gamedev.nap.assets;

import com.badlogic.gdx.files.FileHandle;
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
	public static final String CALIBRI_PATH = FOLDER_DATA + "font/calibri";
	
	private BitmapFont calibri;
	
	private AssetStorage() {
		calibri = new BitmapFont(new FileHandle(CALIBRI_PATH + ".fnt"), new FileHandle(CALIBRI_PATH + ".png"), false);
	}
	
	public BitmapFont getCalibriFont() {
		return calibri;
	}
}
