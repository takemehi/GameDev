package de.htw.saarland.gamedev.nap.client.render.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.entity.MeClientPlayer;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class HUD {

	private static final int HEIGHT_TEAM_POINTS_BAR = 20; 
	
	private static final Color BLACK_COLOR = new Color(0f, 0f, 0f, 1f);
	private static final Color WHITE_COLOR = new Color(1f, 1f, 1f, 1f);
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
	
	public void render(int pointsRed, int pointsBlue, MeClientPlayer character) {
		batch.begin();
		
		if (respawnActive) {
			respawnTime -= Gdx.graphics.getDeltaTime();
			
			font.draw(batch, Integer.toString((int)respawnTime), width / 2, height / 2);
		}
		
		batch.end();
		
		//point bars
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
		
		//skill bar
		//skill1
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(BLACK_COLOR);
		shapeRenderer.rect(
				(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2,
				5,
				HEIGHT_TEAM_POINTS_BAR,
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(WHITE_COLOR);
		shapeRenderer.rect(
				(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2,
				5,
				HEIGHT_TEAM_POINTS_BAR,
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		
		//skill2
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(BLACK_COLOR);
		shapeRenderer.rect(
				(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2+HEIGHT_TEAM_POINTS_BAR+5,
				5,
				HEIGHT_TEAM_POINTS_BAR,
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(WHITE_COLOR);
		shapeRenderer.rect(
				(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2+HEIGHT_TEAM_POINTS_BAR+5,
				5,
				HEIGHT_TEAM_POINTS_BAR,
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		//skill3
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(BLACK_COLOR);
		shapeRenderer.rect(
				(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2+(HEIGHT_TEAM_POINTS_BAR+5)*2,
				5,
				HEIGHT_TEAM_POINTS_BAR,
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(WHITE_COLOR);
		shapeRenderer.rect(
				(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2+(HEIGHT_TEAM_POINTS_BAR+5)*2,
				5,
				HEIGHT_TEAM_POINTS_BAR,
				HEIGHT_TEAM_POINTS_BAR);
		shapeRenderer.end();
		//skill updates
		if(character.getPlayableCharacter().getAttack1().getDeltaTime()>0){
			int cd = (int) ((character.getPlayableCharacter().getAttack1().getCooldown()-character.getPlayableCharacter().getAttack1().getDeltaTime()));
			batch.begin();
			font.draw(batch, Integer.toString(cd)
					, (width/2)-HEIGHT_TEAM_POINTS_BAR-5
					, (HEIGHT_TEAM_POINTS_BAR / 2)+5);
			batch.end();
		}
		if(character.getPlayableCharacter().getAttack2().getDeltaTime()>0){
			int cd = (int) ((character.getPlayableCharacter().getAttack2().getCooldown()-character.getPlayableCharacter().getAttack2().getDeltaTime()))/1;
			batch.begin();
			font.draw(batch, Integer.toString(cd)
					, (width/2)
					, (HEIGHT_TEAM_POINTS_BAR / 2)+5);
			batch.end();
		}
		if(character.getPlayableCharacter().getAttack3().getDeltaTime()>0){
			int cd = (int) ((character.getPlayableCharacter().getAttack3().getCooldown()-character.getPlayableCharacter().getAttack3().getDeltaTime()))/1;
			batch.begin();
			font.draw(batch, Integer.toString(cd)
					, (width/2)+HEIGHT_TEAM_POINTS_BAR+5
					, (HEIGHT_TEAM_POINTS_BAR / 2)+5);
			batch.end();
		}
	}
	
	public void setRespawnActive(boolean respawnActive, float respawnTime) {
		this.respawnTime = respawnTime;
		this.respawnActive = respawnActive;	
	}

}
