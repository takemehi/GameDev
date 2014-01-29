package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.ISendPacket;

public class Team {
	
	public static final int ID_TEAM_BLUE = 0;
	public static final int ID_TEAM_RED = 1;
	
	private SpawnPoint spawnPoint;
	private Array<Player> members;
	private int points;
	private int teamId;
	
	private ISendPacket sendPacket;
	
	public Team(SpawnPoint spawnPoint, Array<Player> members, int teamId, ISendPacket sendPacket) {
		if (members == null || spawnPoint == null) {
			throw new NullPointerException();
		}
		
		this.spawnPoint = spawnPoint;
		this.members = members;
		this.points = 0;
		this.teamId = teamId;
		this.sendPacket = sendPacket;
	}
	
	public void setSendPacket(ISendPacket sendPacket) {
		this.sendPacket = sendPacket;
	}

	public SpawnPoint getSpawnPoint() {
		return spawnPoint;
	}

	public Array<Player> getMembers() {
		return members;
	}

	public int getPoints() {
		return points;
	}

	public void addPoints(int points) {
		setPoints(this.points + points);
	}
	
	public void setPoints(int newAbsPoints) {
		this.points = newAbsPoints;
		
		if (sendPacket != null) {
			SFSObject params = new SFSObject();
			params.putInt(GameOpcodes.TEAM_ID_PARAM, teamId);
			params.putInt(GameOpcodes.POINTS_PARAM, points);
			
			sendPacket.sendServerPacket(GameOpcodes.GAME_UPDATE_GAME_POINTS, params);
		}
	}
}
