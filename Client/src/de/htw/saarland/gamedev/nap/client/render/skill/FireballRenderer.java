package de.htw.saarland.gamedev.nap.client.render.skill;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.render.SkillRenderer;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class FireballRenderer extends SkillRenderer {

	private Texture fireballTex;
	private float radius;
	
	public FireballRenderer(Entity e) {
		super(e.getBody().getPosition(), e);
		
		fireballTex = AssetStorage.getInstance().fireball;
		radius = -(fireballTex.getWidth() / 2.0f) * GameServer.PIXELS_TO_METERS;
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(
				fireballTex,
				position.x,
				position.y,
				radius,
				radius,
				fireballTex.getWidth(),
				fireballTex.getHeight(),
				GameServer.PIXELS_TO_METERS,
				GameServer.PIXELS_TO_METERS,
				0,
				0,
				0,
				fireballTex.getWidth(),
				fireballTex.getHeight(),
				false,
				false
				);
	}
}
