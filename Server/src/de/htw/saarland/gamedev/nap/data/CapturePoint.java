package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class CapturePoint {

	private final static String EXCEPTION_NULL_ENTITY = "Entity object is null!";

	private SensorEntity capturePoint;
	private int teamId;
	private Team teamBlue;
	private Team teamRed;
	private boolean beingCaptured;
	
	private float stateTime;
	
	public CapturePoint(SensorEntity capturePoint){
		if(capturePoint==null) throw new NullPointerException(EXCEPTION_NULL_ENTITY);	
		this.capturePoint=capturePoint;
		teamId = -1;
		stateTime = 0.0f;
	}	
	
	//getter / setter
	public SensorEntity getCapturePoint() {
		return capturePoint;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId){
		this.teamId = teamId;
	}
	public Team getTeamBlue(){
		return teamBlue;
	}
	public void setTeamBlue(Team teamBlue){
		this.teamBlue=teamBlue;
	}
	public Team getTeamRed(){
		return teamRed;
	}
	public void setTeamRed(Team teamRed){
		this.teamRed=teamRed;
	}
	public boolean isBeingCaptured(){
		return beingCaptured;
	}
	
	public void setBeingCaptured(boolean beingCaptured){
		this.beingCaptured=beingCaptured;
	}
	
	public void update(float deltaTime) {
		//TODO global timer
		if (teamId != -1) {
			stateTime += deltaTime;
			if (stateTime >= GameServer.INTERVAL_POINTS) {
				if(teamId==Team.ID_TEAM_BLUE)
					teamBlue.addPoints(GameServer.POINTS_PER_INTERVAL);
				else
					teamRed.addPoints(GameServer.POINTS_PER_INTERVAL);
				stateTime = 0.0f;
			}
		}
	}
}
