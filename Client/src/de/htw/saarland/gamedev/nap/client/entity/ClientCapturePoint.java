package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.Team;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class ClientCapturePoint implements IRender {
	private static final int CAPTURE_POINT_TILE_X_OFFSET = 384;
	private static final int CAPTURE_POINT_TILE_Y_OFFSET = 288;
	private static final int CAPTURE_POINT_TILE_WIDTH = 96;
	private static final int CAPTURE_POINT_TILE_HEIGHT = 96;
	
	private CapturePoint capturePoint;
	private Vector2 pos;	
	private int teamId;
	private String playerCapturedName;
	
	private float captureTime;
	private boolean isBeingCaptured;
	
	private TextureRegion capturePointTex;
	private ShapeRenderer shapeRenderer;
	
	public ClientCapturePoint(CapturePoint capturePoint) {
		if (capturePoint == null) {
			throw new NullPointerException();
		}
		
		this.capturePoint = capturePoint;
		this.teamId = -1;
		this.captureTime = 0;
		this.isBeingCaptured = false;
		this.shapeRenderer = new ShapeRenderer();
		this.pos = capturePoint.getCapturePoint().getBody().getPosition();
		this.pos.x -= 0.5f;
		this.pos.y -= 0.5f;
		
		capturePointTex = new TextureRegion(
				AssetStorage.getInstance().tileset,
				CAPTURE_POINT_TILE_X_OFFSET,
				CAPTURE_POINT_TILE_Y_OFFSET,
				CAPTURE_POINT_TILE_WIDTH,
				CAPTURE_POINT_TILE_HEIGHT
			);
	}

	@Override
	public void render(SpriteBatch batch) {		
		Color col = batch.getColor();
		
		if (teamId == Team.ID_TEAM_RED) {
			batch.setColor(1f, 0f, 0f, 1f);
		}
		else if (teamId == Team.ID_TEAM_BLUE) {
			batch.setColor(0f, 0f, 1f, 1f);
		}
		
		batch.draw(
				capturePointTex,
				pos.x,
				pos.y,
				CAPTURE_POINT_TILE_WIDTH * GameServer.PIXELS_TO_METERS,
				CAPTURE_POINT_TILE_HEIGHT * GameServer.PIXELS_TO_METERS
			);
		batch.setColor(col);
		
		if (isBeingCaptured) {
			captureTime += Gdx.graphics.getDeltaTime();
			
			batch.end();
			
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			this.shapeRenderer.setColor(1f, 1f, 0f, 1f);
			shapeRenderer.rect(
					pos.x,
					pos.y + 1.2f,
					Math.min(captureTime / capturePoint.getTimeToCapture(), 1),
					10 * GameServer.PIXELS_TO_METERS
				);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(0f, 0f, 0f, 1f);
			shapeRenderer.rect(
					pos.x,
					pos.y + 1.2f,
					1,
					10 * GameServer.PIXELS_TO_METERS
				);
			shapeRenderer.end();
			
			batch.begin();
		}
	}
	
	public void setBeingCaptured(boolean isbeingCaptured) {
		this.isBeingCaptured = isbeingCaptured;
		
		if (isbeingCaptured) {
			captureTime = 0;
		}
	}
	
	public void setTeamCaptured(int teamId, String playerCapturedName) {
		this.teamId = teamId;
		this.playerCapturedName = playerCapturedName;
		
		setBeingCaptured(false);
	}
	
	public int getId() {
		return capturePoint.getCapturePoint().getId();
	}

	public int getTeamId() {
		return teamId;
	}

	public TextureRegion getCapturePointTex() {
		return capturePointTex;
	}

	public Vector2 getPos() {
		return pos;
	}
	
	
}
