package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

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
		
		if(teamId==ID_TEAM_BLUE) spawnPoint.getFixture().setUserData(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE);
		else spawnPoint.getFixture().setUserData(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED);
	}	
	
	//getter / setter
	public SensorEntity getSpawnPoint() {
		return spawnPoint;
	}
	public int getTeamId() {
		return teamId;
	}
	
	public static void handleContactBegin(Fixture fA, Fixture fB, Team blueTeam, Team redTeam){
		//Spawn point regeneration
		if (fA.getUserData() != null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			// spawnPoint team blue
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setAtSpawn(true);
					break;
				}
			}
		} else if (fB.getUserData() != null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setAtSpawn(true);
					break;
				}
			}
		} else if (fA.getUserData() != null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			// spawnPoint team red
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setAtSpawn(true);
					break;
				}
			}
		} else if (fB.getUserData() != null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setAtSpawn(true);
					break;
				}
			}				
		}
	}
	
	public static void handleContactEnd(Fixture fA, Fixture fB, Team blueTeam, Team redTeam){
		//Spawn point team blue
		if (fA != null && fA.getUserData() != null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setAtSpawn(false);
					break;
				}
			}
		} else if (fB != null && fB.getUserData() != null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setAtSpawn(false);
					break;
				}
			}
		}
		//Spawn point team red
		else if (fA != null && fA.getUserData() != null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB)) {
					p.getPlChar().setAtSpawn(false);
					break;
				}
			}
		} else if (fB != null && fB.getUserData() != null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA)) {
					p.getPlChar().setAtSpawn(false);
					break;
				}
			}
		}
	}
}
