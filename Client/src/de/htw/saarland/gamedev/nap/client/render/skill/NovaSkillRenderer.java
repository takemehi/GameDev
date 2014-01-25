package de.htw.saarland.gamedev.nap.client.render.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.render.SkillRenderer;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.skills.Skill;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class NovaSkillRenderer extends SkillRenderer {

	public static final float LIFETIME = 0.5f; 
	
	private Texture novaTex;
	private float stateTime;
	private float radius;
	
	public NovaSkillRenderer(Skill s, Entity e) {
		super(s.getPlayableCharacter().getBody().getPosition(), e);
		
		novaTex = AssetStorage.getInstance().nova;
		stateTime = 0;
		radius = -(novaTex.getWidth() / 2.0f) * GameServer.PIXELS_TO_METERS;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		batch.draw(
				novaTex,
				position.x,
				position.y,
				radius * (stateTime / LIFETIME),
				radius * (stateTime / LIFETIME),
				novaTex.getWidth(),
				novaTex.getHeight(),
				GameServer.PIXELS_TO_METERS * (stateTime / LIFETIME),
				GameServer.PIXELS_TO_METERS * (stateTime / LIFETIME),
				0,
				0,
				0,
				novaTex.getWidth(),
				novaTex.getHeight(),
				false,
				false
				);
	}
	
	public boolean isActive() {
		return stateTime < LIFETIME;
	}
	
	/**
	 * Nova handles its own life cycle
	 */
	@Override
	public void setActive(boolean isActive) {
	}

}
