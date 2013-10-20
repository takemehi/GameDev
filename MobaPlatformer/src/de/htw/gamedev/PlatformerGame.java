package de.htw.gamedev;

import Screens.SplashScreen;

import com.badlogic.gdx.Game;

public class PlatformerGame extends Game {
	public final static String VERSION = "0.0.1 Pre-Alpha";
	public final static String LOG = "PlatformerGame";
	
	
	@Override
	public void create() {	
		setScreen(new SplashScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
