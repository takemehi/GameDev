package de.htw.saarland.gamedev.nap.data;

import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class SpawnPoint {
	
	private final static String EXCEPTION_NULL_ENTITY = "Entity object is null!";
	private final static String EXCEPTION_ILLEGAL_TEAM_ID = "Team Id is not existing!";
	
	private static final int ID_TEAM_BLUE = 0;
	private static final int ID_TEAM_RED = 1;

	private SensorEntity spawnPoint;
	private int teamId;
	
	public SpawnPoint(SensorEntity spawnPoint, int teamId){
		if(spawnPoint==null) throw new NullPointerException(EXCEPTION_NULL_ENTITY);
		if(teamId!=ID_TEAM_BLUE && teamId!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAM_ID);
		
		this.spawnPoint=spawnPoint;
		this.teamId=teamId;
		
		if(teamId==ID_TEAM_BLUE) spawnPoint.getFixture().setUserData("spawnBlue");
		else spawnPoint.getFixture().setUserData("spawnRed");
	}	
	
	//getter / setter
	public SensorEntity getSpawnPoint() {
		return spawnPoint;
	}
	public int getTeamId() {
		return teamId;
	}
}
