package de.htw.saarland.gamedev.nap.client.render.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.entity.ClientPlayer;
import de.htw.saarland.gamedev.nap.client.entity.MeClientPlayer;
import de.htw.saarland.gamedev.nap.client.world.RenderableGameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class HUD {

	private static final int HEIGHT_TEAM_POINTS_BAR = 20;
	private static final float SCALE_MINIMAP = 1/48f;
	
	private static final Color BLACK_COLOR = new Color(0f, 0f, 0f, 1f);
	private static final Color BLUE_COLOR = new Color(0f, 0f, 1f, 1f);
	private static final Color RED_COLOR = new Color(1f, 0f, 0f, 1f);
	private static final Color WHITE_COLOR = new Color(1f, 1f, 1f, 1f);
	private static final Color YELLOW_COLOR = new Color(1f, 1f, 0f, 1f);
	private static final Color COBALT_COLOR = new Color(0.24f, 0.41f, 0.67f, 1f);
	private static final Color GREEN_COLOR = new Color(0f, 1f, 0f, 1f);
	
	private volatile boolean respawnActive;
	private volatile float respawnTime;
	
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private SpriteBatch batch; //has its own batch without projectionMatrix
	private OrthogonalTiledMapRenderer worldRenderer;
	private OrthographicCamera camera;
	private SpriteBatch cameraBatch;
	private RenderableGameWorld gameWorld;
	
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
		camera = new OrthographicCamera(width, height);
		cameraBatch = new SpriteBatch();
		cameraBatch.setProjectionMatrix(camera.combined);
	}
	
	public void render(int pointsRed, int pointsBlue, MeClientPlayer character, List<ClientPlayer> players) {
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
				((width / 2) - 10) * ((float)pointsBlue / (float)gameWorld.worldInfo.pointsToWin),
				HEIGHT_TEAM_POINTS_BAR);
		
		shapeRenderer.setColor(RED_COLOR);
		shapeRenderer.rect(
				(width / 2) + 5,
				height - HEIGHT_TEAM_POINTS_BAR - 5,
				((width / 2) - 10) * ((float)pointsRed / (float)gameWorld.worldInfo.pointsToWin),
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
		
		//status infos
		if(character.getPlayableCharacter().isSnared()){
			float percent = (character.getPlayableCharacter().getTimeSnared() / character.getPlayableCharacter().getSnareDuration());
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(YELLOW_COLOR);
			shapeRenderer.rect(
					(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2,
					HEIGHT_TEAM_POINTS_BAR*1.2f+9,
					(HEIGHT_TEAM_POINTS_BAR*3+10)*percent,
					HEIGHT_TEAM_POINTS_BAR / 5);
			shapeRenderer.end();
		}
		if(character.getPlayableCharacter().isStunned()){
			float percent = (character.getPlayableCharacter().getTimeStunned() / character.getPlayableCharacter().getStunDuration());
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(RED_COLOR);
			shapeRenderer.rect(
					(width/2)-((HEIGHT_TEAM_POINTS_BAR+5)*3)/2,
					HEIGHT_TEAM_POINTS_BAR+7,
					(HEIGHT_TEAM_POINTS_BAR*3+10)*percent,
					HEIGHT_TEAM_POINTS_BAR / 5);
			shapeRenderer.end();
		}
		
		//Minimap
		if(worldRenderer!=null){
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(COBALT_COLOR);
			shapeRenderer.rect(
					(width-gameWorld.getWidth()*SCALE_MINIMAP)-5,
					5,
					gameWorld.getWidth()*SCALE_MINIMAP,
					gameWorld.getHeight()*SCALE_MINIMAP);
			shapeRenderer.end();
			
			worldRenderer.setView(camera);
			worldRenderer.render(GameServer.LAYERS_TO_RENDER);
			//render own player
			float posX = character.getPlayableCharacter().getBody().getPosition().x*2+(width-gameWorld.getWidth()*SCALE_MINIMAP)-5;
			float posY = character.getPlayableCharacter().getBody().getPosition().y*2+5;
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(GREEN_COLOR);
			shapeRenderer.circle(posX, posY, 1);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(BLACK_COLOR);
			shapeRenderer.circle(posX, posY, 1);
			shapeRenderer.end();
			//render team mates
			for(ClientPlayer p: players){
				if(p.getPlayableCharacter().getTeamId()==character.getPlayableCharacter().getTeamId()){
					posX = p.getPlayableCharacter().getBody().getPosition().x*2+(width-gameWorld.getWidth()*SCALE_MINIMAP)-5;
					posY = p.getPlayableCharacter().getBody().getPosition().y*2+5;
					shapeRenderer.begin(ShapeType.Filled);
					if(p.getPlayableCharacter().getTeamId()==PlayableCharacter.ID_TEAM_BLUE){
						shapeRenderer.setColor(BLUE_COLOR);
					}else{
						shapeRenderer.setColor(RED_COLOR);
					}
					shapeRenderer.circle(posX, posY, 1);
					shapeRenderer.end();
					shapeRenderer.begin(ShapeType.Line);
					shapeRenderer.setColor(BLACK_COLOR);
					shapeRenderer.circle(posX, posY, 1);
					shapeRenderer.end();
				}
			}
			shapeRenderer.end();
			
		}
	}
	
	public void setRespawnActive(boolean respawnActive, float respawnTime) {
		this.respawnTime = respawnTime;
		this.respawnActive = respawnActive;	
	}
	
	public void setGameWorld(RenderableGameWorld gameWorld){
		if(gameWorld==null) throw new NullPointerException();
		this.gameWorld=gameWorld;
		worldRenderer=new OrthogonalTiledMapRenderer(this.gameWorld.getTiledMap(), SCALE_MINIMAP, cameraBatch);
		System.out.println(gameWorld.getWidth());
		camera.position.set((-width/2+gameWorld.getWidth()*SCALE_MINIMAP)+5, 155, 0);
		camera.update();
	}

}
