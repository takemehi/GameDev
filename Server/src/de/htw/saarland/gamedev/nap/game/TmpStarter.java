package de.htw.saarland.gamedev.nap.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;


public class TmpStarter {

	public static void main (String[] args) {
		new LwjglApplication(new GameServer("", 10), "Game", 480, 320, false);
	}
}
