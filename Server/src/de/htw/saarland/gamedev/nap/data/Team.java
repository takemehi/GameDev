package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.utils.Array;

public class Team {
	
	public static final int ID_TEAM_BLUE = 0;
	public static final int ID_TEAM_RED = 1;
	
	private SpawnPoint spawnPoint;
	private Array<Player> members;
	private int points;
	
	public Team(SpawnPoint spawnPoint, Array<Player> members) {
		if (members == null || spawnPoint == null) {
			throw new NullPointerException();
		}
		
		this.spawnPoint = spawnPoint;
		this.members = members;
		this.points = 0;
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
		this.points += points;
	}
}
