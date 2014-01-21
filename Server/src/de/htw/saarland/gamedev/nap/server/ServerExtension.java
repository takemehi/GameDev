package de.htw.saarland.gamedev.nap.server;

import java.util.ArrayList;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.SFSUser;
import com.smartfoxserver.v2.extensions.SFSExtension;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.GameServer;
import de.htw.saarland.gamedev.nap.server.game.handler.ClientInitializedHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameGetMapCharacterHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveDownHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveDownStopHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveJumpHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveJumpStopHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveLeftHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveRightHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveStopLeftHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameMoveStopRightHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameSkill1StartHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameSkill2StartHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameSkill3StartHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GameSkillDirectionUpdateHandler;
import de.htw.saarland.gamedev.nap.server.game.handler.GetMoveableEntitiesHandler;
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
	
	private byte initializedCounter;
	
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
	
	public void clientInitialized() {
		initializedCounter++;
	}
	
	public boolean isClientInitDone() {
		return (initializedCounter == (game.getBlueTeam().getMembers().size + game.getRedTeam().getMembers().size));
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
		
		initializedCounter = 0;
		
//		addRequestHandler(GameOpcodes.GAME_CAPTURE_START_REQUEST, );
//		addRequestHandler(GameOpcodes.GAME_CAPTURE_STOP_REQUEST, theClass);
		addRequestHandler(GameOpcodes.GAME_GET_MAP_CHARACTER, GameGetMapCharacterHandler.class);
		addRequestHandler(GameOpcodes.GAME_GET_MOVEABLE_ENTITIES, GetMoveableEntitiesHandler.class);
		addRequestHandler(GameOpcodes.GAME_INITIALIZED, ClientInitializedHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_DOWN_REQUEST, GameMoveDownHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_DOWN_STOP_REQUEST, GameMoveDownStopHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_JUMP_REQUEST, GameMoveJumpHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_JUMP_STOP_REQUEST, GameMoveJumpStopHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_LEFT_REQUEST, GameMoveLeftHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_RIGHT_REQUEST, GameMoveRightHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_STOP_LEFT_REQUEST, GameMoveStopLeftHandler.class);
		addRequestHandler(GameOpcodes.GAME_MOVE_STOP_RIGHT_REQUEST, GameMoveStopRightHandler.class);
		addRequestHandler(GameOpcodes.GAME_SKILL1_START_REQUEST, GameSkill1StartHandler.class);
		addRequestHandler(GameOpcodes.GAME_SKILL2_START_REQUEST, GameSkill2StartHandler.class);
		addRequestHandler(GameOpcodes.GAME_SKILL3_START_REQUEST, GameSkill3StartHandler.class);
		addRequestHandler(GameOpcodes.GAME_SKILL_DIRECTION_UPDATE, GameSkillDirectionUpdateHandler.class);
		
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
		
		send(LauncherOpcodes.LAUNCHER_GAME_STARTS, null, getParentRoom().getPlayersList());
		
		while (!game.isGameEnd()) {
			deltaTime.update();
			game.render();
		}
		
		game.dispose();
	}
}
