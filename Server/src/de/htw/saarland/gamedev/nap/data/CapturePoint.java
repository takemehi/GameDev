package de.htw.saarland.gamedev.nap.data;

import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;

public class CapturePoint {

	private final static String EXCEPTION_ILLEGAL_TEAM_ID = "Team Id is not existing!";
	private final static String EXCEPTION_NULL_ENTITY = "Entity object is null!";
	
	private static final int ID_TEAM_BLUE = 0;
	private static final int ID_TEAM_RED = 1;

	private SensorEntity capturePoint;
	private int teamId;
	boolean beingCaptured;
	
	public CapturePoint(SensorEntity capturePoint){
		if(capturePoint==null) throw new NullPointerException(EXCEPTION_NULL_ENTITY);	
		this.capturePoint=capturePoint;
		teamId=-1;
	}	
	
	//getter / setter
	public SensorEntity getCapturePoint() {
		return capturePoint;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId){
		if(teamId!=ID_TEAM_BLUE && teamId!=ID_TEAM_RED) throw new IllegalArgumentException(EXCEPTION_ILLEGAL_TEAM_ID);
		this.teamId=teamId;
	}
	public boolean isBeingCaptured(){
		return beingCaptured;
	}
}
