package de.htw.saarland.gamedev.nap.client.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class RenderableGameWorld extends GameWorld implements IRender, Disposable {

	OrthogonalTiledMapRenderer renderer;
	
	public RenderableGameWorld(World world, String mapName, int currentId, OrthographicCamera camera) {
		super(world, mapName, currentId);
		
		if (camera == null) {
			throw new NullPointerException();
		}
		
		renderer = new OrthogonalTiledMapRenderer(getTiledMap(), GameServer.PIXELS_TO_METERS);
		renderer.setView(camera);
	}
	
	public void update(OrthographicCamera camera) {
		renderer.setView(camera);
	}

	@Override
	public void render(SpriteBatch batch) {
		renderer.render(GameServer.LAYERS_TO_RENDER);
	}

	@Override
	public void dispose() {
		renderer.dispose();
	}

}
