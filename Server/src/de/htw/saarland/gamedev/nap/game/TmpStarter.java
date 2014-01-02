package de.htw.saarland.gamedev.nap.game;

import java.util.ArrayList;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.Player;


public class TmpStarter {

	public static void main (String[] args) {
		Array<Player> teamBlue=new Array<Player>();
		Array<Player> teamRed=new Array<Player>();
		new LwjglApplication(new GameServer("map", 10, teamBlue, teamRed), "Game", 480, 320, true);
	}
}
