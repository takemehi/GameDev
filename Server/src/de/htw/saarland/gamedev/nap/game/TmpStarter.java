package de.htw.saarland.gamedev.nap.game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.SFSUser;


public class TmpStarter {

	public static void main (String[] args) {
		Array<SFSUser> userBlue=new Array<SFSUser>();
		Array<SFSUser> userRed=new Array<SFSUser>();
		int charactersBlue[] = {1,1};
		int charactersRed[] = {1,0};
		new LwjglApplication(new GameServer("map", 10, userBlue, userRed, charactersBlue, charactersRed), "Game", 480, 320, true);
	}
}
