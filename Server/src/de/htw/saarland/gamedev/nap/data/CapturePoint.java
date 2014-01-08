package de.htw.saarland.gamedev.nap.data;

import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.game.GameServer;

public class CapturePoint {

	private final static String EXCEPTION_NULL_ENTITY = "Entity object is null!";

	private SensorEntity capturePoint;
	private Team team;
	private boolean beingCaptured;
	
	private float stateTime;
	
	public CapturePoint(SensorEntity capturePoint){
		if(capturePoint==null) throw new NullPointerException(EXCEPTION_NULL_ENTITY);	
		this.capturePoint=capturePoint;
		team = null;
		stateTime = 0.0f;
	}	
	
	//getter / setter
	public SensorEntity getCapturePoint() {
		return capturePoint;
	}
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team){
		this.team = team;
	}
	public boolean isBeingCaptured(){
		return beingCaptured;
	}
	
	public void setBeingCaptured(boolean beingCaptured){
		this.beingCaptured=beingCaptured;
	}
	
	public void update(float deltaTime) {
		if (team != null) {
			stateTime += deltaTime;
			if (stateTime >= GameServer.INTERVAL_POINTS) {
				team.addPoints(GameServer.POINTS_PER_INTERVAL);
				stateTime = 0.0f;
			}
		}
	}
}
