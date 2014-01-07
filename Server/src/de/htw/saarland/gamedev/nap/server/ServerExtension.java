package de.htw.saarland.gamedev.nap.server;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.extensions.SFSExtension;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.handler.GameMoveDownHandler;
import de.htw.saarland.gamedev.nap.server.launcher.Launcher;
import de.htw.saarland.gamedev.nap.server.launcher.LauncherOpcodes;
import de.htw.saarland.gamedev.nap.server.launcher.LauncherPlayer;
import de.htw.saarland.gamedev.nap.server.launcher.handler.ChangeCharacterRequestHandler;
import de.htw.saarland.gamedev.nap.server.launcher.handler.ChangeReadyRequestHandler;
import de.htw.saarland.gamedev.nap.server.launcher.handler.ChangeTeamRequestHandler;
import de.htw.saarland.gamedev.nap.server.launcher.handler.RoomJoinServerHandler;
import de.htw.saarland.gamedev.nap.server.launcher.handler.RoomLeaveServerHandler;
import de.htw.saarland.gamedev.nap.server.launcher.handler.StartGameRequestHandler;
import de.htw.saarland.gamedev.nap.server.launcher.handler.UserDisconnectHandler;

public class ServerExtension extends SFSExtension implements Runnable {

	private Launcher launcher;
	private GameServer game;
	private DeltaTime deltaTime;
	
	@Override
	public void init() {
		launcher = new Launcher(this, getParentRoom().getCapacity() / 2);
		
		addEventHandler(SFSEventType.USER_JOIN_ROOM, RoomJoinServerHandler.class);
		addEventHandler(SFSEventType.USER_LEAVE_ROOM, RoomLeaveServerHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectHandler.class);
		
		addRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_REQUEST, ChangeCharacterRequestHandler.class);
		addRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_TEAM_REQUEST, ChangeTeamRequestHandler.class);
		addRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_READY_REQUEST, ChangeReadyRequestHandler.class);
		addRequestHandler(LauncherOpcodes.LAUNCHER_START_GAME_REQUEST, StartGameRequestHandler.class);
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	public GameServer getGame() {
		return game;
	}
	
	public void startGame() {
		//clean up and start game logic
		removeEventHandler(SFSEventType.USER_DISCONNECT);
		removeEventHandler(SFSEventType.USER_JOIN_ROOM);
		removeEventHandler(SFSEventType.USER_LEAVE_ROOM);
		
		removeRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_REQUEST);
		removeRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_TEAM_REQUEST);
		removeRequestHandler(LauncherOpcodes.LAUNCHER_CHANGE_READY_REQUEST);
		removeRequestHandler(LauncherOpcodes.LAUNCHER_START_GAME_REQUEST);
		
		// TODO implement request handlers
//		addRequestHandler(GameOpcodes.GAME_CAPTURE_START_REQUEST, );
//		addRequestHandler(GameOpcodes.GAME_CAPTURE_STOP_REQUEST, theClass);
//		addRequestHandler(GameOpcodes.GAME_GET_MAP_CHARACTER, theClass);
//		addRequestHandler(GameOpcodes.GAME_GET_MOVEABLE_ENTITIES, theClass);
//		addRequestHandler(GameOpcodes.GAME_INITIALIZED, theClass);
		addRequestHandler(GameOpcodes.GAME_MOVE_DOWN_REQUEST, GameMoveDownHandler.class);
//		addRequestHandler(GameOpcodes.GAME_MOVE_DOWN_STOP_REQUEST, theClass);
//		addRequestHandler(GameOpcodes.GAME_MOVE_JUMP_REQUEST, theClass);
//		addRequestHandler(GameOpcodes.GAME_MOVE_LEFT_REQUEST, theClass);
//		addRequestHandler(GameOpcodes.GAME_MOVE_RIGHT_REQUEST, theClass);
//		addRequestHandler(GameOpcodes.GAME_MOVE_STOP_REQUEST, theClass);
		
		ArrayList<LauncherPlayer> redTeam = new ArrayList<LauncherPlayer>();
		ArrayList<LauncherPlayer> blueTeam = new ArrayList<LauncherPlayer>();
		launcher.getTeams(redTeam, blueTeam);
		
		ArrayList<SFSUser> sfsBlue = new ArrayList<SFSUser>();
		ArrayList<Integer> blueChars = new ArrayList<Integer>();
		ArrayList<SFSUser> sfsRed = new ArrayList<SFSUser>();
		ArrayList<Integer> redChars = new ArrayList<Integer>();
		
		for (int i = 0; i < blueTeam.size(); i++) {
			sfsBlue.add((SFSUser)blueTeam.get(i).getSfsUser());
			blueChars.add(blueTeam.get(i).getCharacterId());
		}
		
		for (int i = 0; i < redTeam.size(); i++) {
			sfsRed.add((SFSUser)redTeam.get(i).getSfsUser());
			redChars.add(redTeam.get(i).getCharacterId());
		}
		
		deltaTime = new DeltaTime();
		
		game = new GameServer((String)(getParentRoom().getVariable(LauncherOpcodes.MAP_NAME_VAR).getValue()),
				sfsBlue,
				sfsRed,
				blueChars,
				redChars,
				deltaTime,
				this);
		
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void run() {
		//gameloop
		
		deltaTime.update();
		game.create();
		
		while (!game.isGameEnd()) {
			deltaTime.update();
			game.render();
		}
		
		game.dispose();
	}
}
