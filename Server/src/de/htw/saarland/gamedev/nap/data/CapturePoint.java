package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.smartfoxserver.v2.entities.data.SFSObject;

import de.htw.saarland.gamedev.nap.data.GameWorld.WorldInfo;
import de.htw.saarland.gamedev.nap.data.entities.SensorEntity;
import de.htw.saarland.gamedev.nap.data.network.GameOpcodes;
import de.htw.saarland.gamedev.nap.game.ISendPacket;

public class CapturePoint {
	
	private final static String EXCEPTION_NULL_ENTITY = "Entity object is null!";

	private SensorEntity capturePoint;
	private int teamId;
	private Team teamBlue;
	private Team teamRed;
	private boolean beingCaptured;
	private final WorldInfo worldInfo;
	
	private float stateTime;
	private ISendPacket sendPacket;
	
	public CapturePoint(SensorEntity capturePoint, WorldInfo worldInfo){
		if(capturePoint==null) throw new NullPointerException(EXCEPTION_NULL_ENTITY);	
		this.capturePoint=capturePoint;
		this.worldInfo = worldInfo;
		teamId = -1;
		stateTime = 0.0f;
	}
	
	public void setSendPacket(ISendPacket sendPacket) {
		this.sendPacket = sendPacket;
	}
	
	//getter / setter
	public SensorEntity getCapturePoint() {
		return capturePoint;
	}
	
	public float getTimeToCapture() {
		return worldInfo.timeToCapture;
	}
	
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId, int entityId){
		if (sendPacket != null) {
			SFSObject params = new SFSObject();
			params.putInt(GameOpcodes.ENTITY_ID_PARAM, capturePoint.getId());
			params.putInt(GameOpcodes.PLAYER_ID_PARAM, entityId);
			params.putInt(GameOpcodes.TEAM_ID_PARAM, teamId);
			
			sendPacket.sendServerPacket(GameOpcodes.GAME_CAPTURE_SUCCESS, params);
		}
		
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
		
		if (sendPacket != null) {
			SFSObject params = new SFSObject();
			params.putInt(GameOpcodes.ENTITY_ID_PARAM, capturePoint.getId());
			params.putBool(GameOpcodes.CAPTURE_STATUS_PARAM, beingCaptured);
			
			sendPacket.sendServerPacket(GameOpcodes.GAME_CAPTURE_CHANGE, params);
		}
		
		this.beingCaptured=beingCaptured;
	}
	
	public void update(float deltaTime) {
		if (teamId != -1) {
			stateTime += deltaTime;
			if (stateTime >= worldInfo.intervalPoints) {
				if((teamBlue.getPoints()<worldInfo.pointsToWin) && (teamRed.getPoints()<worldInfo.pointsToWin)){
					if(teamId==Team.ID_TEAM_BLUE)
						teamBlue.addPoints(worldInfo.pointsPerInterval);
					else
						teamRed.addPoints(worldInfo.pointsPerInterval);
				}
				stateTime = 0.0f;
			}
		}
	}
	
	public static void handleContactBegin(Fixture fA, Fixture fB, Array<IPlayer> players, Array<CapturePoint> capturePoints){
		//Capture point permission
		if(fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_CAPTUREPOINT)
				&& fB.getUserData().equals(PlayableCharacter.USERDATA_PLAYER)){
			for(CapturePoint c: capturePoints){
				if(c.getCapturePoint().getFixture().equals(fA)){
					for(IPlayer p: players){
						if(p.getPlChar().getFixture().equals(fB)){
							p.getPlChar().setPointEligibleToCapture(c.getCapturePoint().getId());
							break;
						}
					}
				}
			}
		}
		else if(fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_CAPTUREPOINT)
				&& fA.getUserData().equals(PlayableCharacter.USERDATA_PLAYER)){
			for(CapturePoint c: capturePoints){
				if(c.getCapturePoint().getFixture().equals(fB)){
					for(IPlayer p: players){
						if(p.getPlChar().getFixture().equals(fA)){
							p.getPlChar().setPointEligibleToCapture(c.getCapturePoint().getId());
							break;
						}
					}
				}
			}
		}
	}
	
	public static void handleContactEnd(Fixture fA, Fixture fB, Array<IPlayer> players, Array<CapturePoint> capturePoints){
		//Capture point permission
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_CAPTUREPOINT)
				&& fB.getUserData()!=null && fB.getUserData().equals(PlayableCharacter.USERDATA_PLAYER)){
			for(IPlayer p: players){
				if(p.getPlChar().getFixture().equals(fB)){
					p.getPlChar().setPointEligibleToCapture(-1);
					break;
				}
			}
		}
		else if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_CAPTUREPOINT)
				&& fA.getUserData()!=null && fA.getUserData().equals(PlayableCharacter.USERDATA_PLAYER)){
			for(IPlayer p: players){
				if(p.getPlChar().getFixture().equals(fA)){
					p.getPlChar().setPointEligibleToCapture(-1);
					break;
				}
			}			
		}
	}
}
