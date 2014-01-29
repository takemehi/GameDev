package de.htw.saarland.gamedev.nap.client.render.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class HUD {

	private static final int HEIGHT_TEAM_POINTS_BAR = 20; 
	
	private static final Color BLACK_COLOR = new Color(0f, 0f, 0f, 1f);
	private static final Color BLUE_COLOR = new Color(0f, 0f, 1f, 1f);
	private static final Color RED_COLOR = new Color(1f, 0f, 0f, 1f);
	
	private volatile boolean respawnActive;
	private volatile float respawnTime;
	
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private SpriteBatch batch; //has its own batch without projectionMatrix
	
	private float width;
	private float height;

	public HUD() {
		respawnActive = false;
		respawnTime = 0;
		
		font = AssetStorage.getInstance().calibri;
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
	}
	
	public void render(int pointsRed, int pointsBlue) {
		batch.begin();
		
		if (respawnActive) {
			respawnTime -= Gdx.graphics.getDeltaTime();
			
			font.draw(batch, Integer.toString((int)respawnTime), width / 2, height / 2);
		}
		
		batch.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(BLUE_COLOR);
		shapeRenderer.rect(
				5,
				height - HEIGHT_TEAM_POINTS_BAR - 5,
				((width / 2) - 10) * ((float)pointsBlue / (float)GameServer.POINTS_TO_WIN),
				HEIGHT_TEAM_POINTS_BAR);
		
		shapeRenderer.setColor(RED_COLOR);
		shapeRenderer.rect(
				(width / 2) + 5,
				height - HEIGHT_TEAM_POINTS_BAR - 5,
				((width / 2) - 10) * ((float)pointsRed / (float)GameServer.POINTS_TO_WIN),
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(BLACK_COLOR);
		shapeRenderer.rect(
				5,
				height - HEIGHT_TEAM_POINTS_BAR - 5,
				((width / 2) - 10),
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.rect(
				(width / 2) + 5,
				height - HEIGHT_TEAM_POINTS_BAR - 5,
				((width / 2) - 10),
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
	}
	
	public void setRespawnActive(boolean respawnActive, float respawnTime) {
		this.respawnTime = respawnTime;
		this.respawnActive = respawnActive;	
	}

}
