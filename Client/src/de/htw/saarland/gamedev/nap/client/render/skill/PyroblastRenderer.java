package de.htw.saarland.gamedev.nap.client.render.skill;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.render.SkillRenderer;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class PyroblastRenderer extends SkillRenderer {

	private Texture pyroblastTex;
	private float radius;
	
	public PyroblastRenderer(Entity e) {
		super(e.getBody().getPosition(), e);
		
		pyroblastTex = AssetStorage.getInstance().pyroblast;
		radius = -(pyroblastTex.getWidth() / 2.0f) * GameServer.PIXELS_TO_METERS;
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(
				pyroblastTex,
				position.x,
				position.y,
				radius,
				radius,
				pyroblastTex.getWidth(),
				pyroblastTex.getHeight(),
				GameServer.PIXELS_TO_METERS,
				GameServer.PIXELS_TO_METERS,
				0,
				0,
				0,
				pyroblastTex.getWidth(),
				pyroblastTex.getHeight(),
				false,
				false
				);
	}

}
