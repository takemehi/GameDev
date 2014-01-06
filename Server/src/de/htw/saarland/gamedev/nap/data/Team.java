package de.htw.saarland.gamedev.nap.data;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Disposable;

public class Team implements Disposable {
	private SpawnPoint spawnPoint;
	private ArrayList<Player> members;
	private int points;
	
	public Team(SpawnPoint spawnPoint, ArrayList<Player> members) {
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

	public ArrayList<Player> getMembers() {
		return members;
	}

	public int getPoints() {
		return points;
	}

	public void addPoints(int points) {
		this.points += points;
	}

	@Override
	public void dispose() {
		for (Player player: members) {
			player.dispose();
		}
	}
}
