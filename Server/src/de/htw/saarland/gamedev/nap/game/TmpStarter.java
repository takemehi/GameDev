package de.htw.saarland.gamedev.nap.game;

import java.util.ArrayList;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import de.htw.saarland.gamedev.nap.data.Player;


public class TmpStarter {

	public static void main (String[] args) {
		ArrayList<Player> team= new ArrayList<Player>();
		new LwjglApplication(new GameServer("map2", 10, team, team), "Game", 480, 320, true);
	}
}
